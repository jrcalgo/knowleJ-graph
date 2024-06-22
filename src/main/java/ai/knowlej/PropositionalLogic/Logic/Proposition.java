package ai.knowlej.PropositionalLogic.Logic;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Stack;
import java.util.function.Function;

import ai.knowlej.Exceptions.*;

/**
 * 
 * 
 */
public class Proposition {
    /** syntax for propositional logic */
    private LogicalSyntax syntax;
    /** Logical expression, its string and other functions */
    private Expression expression;
    /** operands contained in expression's String */
    private ArrayList<String> operands;
    /** count of operands in expression */
    private byte operandCount;
    /** combination of operands and statement propositional sentences */
    private ArrayList<String> sentences;
    /** count of total sentences (operands + sentences/expressions) */
    private byte sentenceCount;
    /** truth table for expression */
    private String[][] truthTable;
    /** boolean value table for truth table (excludes title row) */
    private Boolean[][] tableValues;
    /** total count of boolean values in truth table */
    private int valueCount;
    /** count of rows in truth table excluding title row */
    private int boolRowsCount;
    /** count of columns in truth table */
    private int boolColsCount;

    public Proposition()
            throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
        super();
    }

    public Proposition(String e)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        this.syntax = new LogicalSyntax();
        this.expression = new Expression(e);
        setSentences();
    }

    public void setExpression(String e)
    throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        this.expression.setExpression(e);
        setSentences();
        setTruthTable();
    }

    /**
     * Helper method for parsePropositions() method, balances tree expression
     * sentences
     * 
     * @throws InvalidExpressionException
     */
    private void setSentences() throws InvalidExpressionException {
        sentences = new ArrayList<String>();

        parseOperands();
        for (int i = 0; i < this.operands.size(); i++) {
            sentences.add(this.operands.get(i));
        }

        sentences.add(this.expression.getExpression());

        sentenceCount = (byte) ((int) operandCount + 1);
    }

    public String getExpression() {
        return this.expression.getExpression();
    }

    public String getConvertedExpression() {
        return this.expression.getConvertedExpression();
    }

    public ArrayList<String> getSentences() {
        return this.sentences;
    }

    public String getSentence(int index) {
        if (index >= 0 && index < sentences.size()) {
            return sentences.get(index);
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds");
        }
    }

    public ArrayList<String> getSentences(int from, int to) throws IndexOutOfBoundsException {
        if ((from < 0) || (to > sentences.size()))
            throw new IndexOutOfBoundsException("either 'from' or 'to' is out of bounds.");

        ArrayList<String> subPropositions = new ArrayList<>();
        for (; from <= to; from++) {
            subPropositions.add(this.sentences.get(from));
        }

        return subPropositions;
    }

    public byte getOperandCount() {
        return this.operandCount;
    }

    public byte getSentenceCount() {
        return this.sentenceCount;
    }

    public String[][] getTruthTable() throws InvalidExpressionException {
        if (this.tableValues == null || this.truthTable == null) {
            setTruthTable();
        }
        return this.truthTable;
    }

    public String[] getStringTableRow(int row) throws InvalidExpressionException {
        if (this.tableValues == null || this.truthTable == null) {
            setTruthTable();
        }
        if (row > truthTable.length) {
            throw new IndexOutOfBoundsException();
        }
        return this.truthTable[row];
    }

    public String[] getStringTableColumn(int col) throws InvalidExpressionException {
        if (this.tableValues == null || this.truthTable == null) {
            setTruthTable();
        }
        if (col > truthTable[0].length) {
            throw new IndexOutOfBoundsException();
        }

        String[] column = new String[truthTable.length];
        for (int i = 0; i < truthTable.length; i++)
            column[i] = truthTable[i][col];

        return column;
    }

    public Boolean[][] getBooleanTable() throws InvalidExpressionException {
        if (this.tableValues == null || this.truthTable == null) {
            setTruthTable();
        }
        return this.tableValues;
    }

    public Boolean[] getBooleanTableRow(int row) throws InvalidExpressionException {
        if (this.tableValues == null || this.truthTable == null) {
            setTruthTable();
        }
        return this.tableValues[row];
    }

    public Boolean[] getBooleanTableColumn(int col) throws InvalidExpressionException {
        if (this.tableValues == null || this.truthTable == null) {
            setTruthTable();
        }
        Boolean[] column = new Boolean[valueCount];
        int columnElements = valueCount / boolColsCount;

        for (int i = 0; i < columnElements; i++)
            column[i] = tableValues[i][col];

        return column;
    }

    private void parseOperands() throws InvalidExpressionException {
        operands = new ArrayList<String>();
        for (Character c : this.expression.getConvertedExpression().toCharArray()) {
            if (syntax.isOperand(c) && !c.equals('T') && !c.equals('F')) {
                if (!operands.contains(c.toString())) {
                    operands.add(c.toString());
                    operandCount++;
                    if (operandCount > 15) {
                        this.expression = null;
                        throw new InvalidExpressionException(
                                "Too many unique operands; only 15 allowed; there are " + operandCount + " operands.");
                    }
                }
            }
        }
    }

    private void setTruthTable() throws InvalidExpressionException {
        boolRowsCount = (int) Math.pow(2, operandCount);
        boolColsCount = sentences.size();

        TruthTableBuilder ttb = new TruthTableBuilder(operands, boolRowsCount, boolColsCount);
        tableValues = ttb.getValueTable();
        truthTable = ttb.getTruthTable();
        valueCount = ttb.getBoolCount();
        ttb.close();

        truthTable[0][boolColsCount - 1] = this.expression.getExpression();

        HashMap<Character, Character> valueMap = new HashMap<>();
        for (int rows = 0; rows < boolRowsCount; rows++) {
            for (int i = 0; i < operandCount; i++)
                valueMap.put(operands.get(i).charAt(0), truthTable[rows + 1][i].charAt(0));

            truthTable[rows + 1][boolColsCount - 1] = evaluateExpression(valueMap) ? "T" : "F";
            tableValues[rows][boolColsCount - 1] = truthTable[rows + 1][boolColsCount - 1].equals("T");
            valueMap.clear();
        }
    }

    public void printTruthTable() throws InvalidExpressionException {
        if (this.tableValues == null || this.truthTable == null) {
            setTruthTable();
        }
        System.out.print("\s\s\s\s\s");
        for (int i = 0; i < truthTable[i].length; i++) {
            System.out.print(i + "\s\s\s");
        }
        System.out.println();
        for (int i = 0; i < truthTable[i].length; i++) {
            System.out.print("\s\s\s\s\s");
            System.out.print(truthTable[0][i]);
        }
        System.out.println();
        for (int i = 1; i < truthTable.length; i++) {
            for (int j = 0; j < truthTable[i].length; j++) {
                System.out.print(i + "." + j + ".\s");
                System.out.print(truthTable[i][j] + "\s\s\s");
            }
            System.out.println();
        }
    }

    public void printTruthTable(int fromCol, int toCol) throws IndexOutOfBoundsException, InvalidExpressionException {
        if (this.tableValues == null || this.truthTable == null) {
            setTruthTable();
        }
        if (fromCol > toCol)
            throw new IndexOutOfBoundsException(fromCol + " is out of bounds.");

        for (int i = 0; i < truthTable[i].length; i++) {
            System.out.print(i + ".\s");
            System.out.print(truthTable[0][i] + "\s\s\s");
        }

        for (int i = 1; i < truthTable.length; i++) {
            for (int j = fromCol; j < toCol; j++) {
                System.out.print(i + "." + j + ".\s");
                System.out.print(truthTable[i][j] + "\s\s\s");
            }
            System.out.println();
        }
    }

    /**
     * incredibly inefficient but at least it works for now, will optimize later to
     * be recursive :^)
     * 
     * 
     * @param valueMap
     * @return
     * @throws InvalidExpressionException
     */
    public boolean evaluateExpression(Map<Character, Character> valueMap) throws InvalidExpressionException {
        PropositionOperators operator = new PropositionOperators();
        boolean answer = false;

        // mE = markedExpression contains boolean values for operands
        String mE = this.expression.getConvertedExpression();
        // replace operands with bool values
        int o = 0;
        while (syntax.containsAnyOperands(mE)) {
            mE = mE.replaceAll(operands.get(o), valueMap.get(operands.get(o).charAt(0)).toString());
            o++;
        }

        // apply negation where necessary throughout whole expression
        if (mE.contains("n")) {
            while (mE.contains("n")) {
                if (mE.contains("nT")) {
                    mE = mE.replaceFirst("nT", operator.not("T"));
                } else if (mE.contains("nF")) {
                    mE = mE.replaceFirst("nF", operator.not("F"));
                }
                if (!mE.contains("nT") && !mE.contains("nF")) {
                    if (mE.matches(".*n[0-9].*")) {
                        char[] mEChars = mE.toCharArray();
                        Integer parenthesesValue = 0;
                        int index = mE.indexOf("n");
                        if (!(mE.charAt(index + 1) == 0))
                            parenthesesValue = Integer.parseInt(mE.charAt(index + 1) + "");

                        index += 2;
                        while (index != mE.lastIndexOf(parenthesesValue.toString())) {
                            if (mE.charAt(index) == 'T')
                                mEChars[index] = operator.not("T").charAt(0);
                            else if (mE.charAt(index) == 'F')
                                mEChars[index] = operator.not("F").charAt(0);

                            index++;
                        }
                        mE = new String(mEChars);
                        mE = mE.replace("n" + parenthesesValue.toString(), parenthesesValue.toString());
                    }
                }
            }
        }
        // lambda function for evaluating expression based on order of operations
        Function<String, String> evaluate = (expression) -> {
            LinkedList<String> operators = new LinkedList<>(Arrays.asList("a", "o", "m", "i", "x"));
            String result;
            for (String op : operators) {
                if (expression.contains(op)) {
                    int opCount = (int) expression.chars().filter(c -> c == op.charAt(0)).count();
                    for (; opCount > 0; opCount--) {
                        int index = expression.indexOf(op);
                        String leftOperand = expression.charAt(index - 1) + "";
                        String rightOperand = expression.charAt(index + 1) + "";
                        switch (op) {
                            case "a": {
                                result = operator.and(leftOperand, rightOperand);
                                expression = expression.replaceFirst(expression.substring(index - 1, index + 2),
                                        result);
                                break;
                            }
                            case "o": {
                                result = operator.or(leftOperand, rightOperand);
                                expression = expression.replaceFirst(expression.substring(index - 1, index + 2),
                                        result);
                                break;
                            }
                            case "m": {
                                result = operator.implies(leftOperand, rightOperand);
                                expression = expression.replaceFirst(expression.substring(index - 1, index + 2),
                                        result);
                                break;
                            }
                            case "i": {
                                result = operator.iff(leftOperand, rightOperand);
                                expression = expression.replaceFirst(expression.substring(index - 1, index + 2),
                                        result);
                                break;
                            }
                            case "x": {
                                result = operator.xor(leftOperand, rightOperand);
                                expression = expression.replaceFirst(expression.substring(index - 1, index + 2),
                                        result);
                                break;
                            }
                            default:
                                break;
                        }
                    }
                }
            }
            if (expression.matches(".*[0-9].*"))
                expression = expression.replaceAll("[0-9]", "");

            return expression;
        };

        // evaluate parentheses and their contents highest to lowest, and left-to-right
        String replacement;
        do {
            OptionalInt max = mE.chars().filter(Character::isDigit).map(Character::getNumericValue).max();
            if (max.isPresent()) {
                for (int m = max.getAsInt(); m >= 0; m--) {
                    String mString = String.valueOf(m);
                    replacement = mE.substring(mE.indexOf(mString), mE.lastIndexOf(mString) + mString.length());
                    mE = mE.replace(replacement, evaluate.apply(replacement));
                }
            } else { // if no digit in String, evaluate expression left-to-right
                mE = evaluate.apply(mE);
            }
        } while (mE.length() > 1);

        answer = mE.equals("T");
        return answer;
    }

    /**
     * Form: P->Q == ~P->~Q
     * @return an inverse of the current converted expression from a specific 'm' operator starting point.
     */
    public String inverse(int fromConvertedImplicationIndex) throws InvalidExpressionException {
        if (!this.expression.getConvertedExpression().contains("m"))
            throw new InvalidExpressionException("Invalid proposition; must contain implication");
        else if (this.expression.getExpression().charAt(fromConvertedImplicationIndex) != 'm')
            throw new InvalidExpressionException("Invalid implication index for converted expression; must be 'm'");

        String[] pair = implicationSubstringInversion(implicationSubstrings(fromConvertedImplicationIndex));
        return pair[0] + 'm' + pair[1];
    }

    /**
     * Form: P->Q == Q->P
     * @return a converse of the current converted expression from a specific 'm' operator starting point.
     */
    public String converse(int fromConvertedImplicationIndex) throws InvalidExpressionException {
        if (!this.expression.getConvertedExpression().contains("m"))
            throw new InvalidExpressionException("Invalid proposition; must contain implication");
        else if (this.expression.getConvertedExpression().charAt(fromConvertedImplicationIndex) != 'm')
            throw new InvalidExpressionException("Invalid implication index for converted expression; must be 'm'");

        String[] pair = implicationSubstrings(fromConvertedImplicationIndex);
        return pair[1] + 'm' + pair[0];
    }

    /**
     * Form: P->Q == ~Q->~P
     * @return a contrapositive from of the current converted expression from a specific 'm' operator starting point.
     */
    public String contrapositive(int fromConvertedImplicationIndex) throws InvalidExpressionException {
        if (!this.expression.getConvertedExpression().contains("m"))
            throw new InvalidExpressionException("Invalid proposition; must contain implication");
        else if (this.expression.getConvertedExpression().charAt(fromConvertedImplicationIndex) != 'm')
            throw new InvalidExpressionException("Invalid implication index for converted expression; must be 'm'");

        String[] pair = implicationSubstringInversion(implicationSubstrings(fromConvertedImplicationIndex));
        return pair[1] + 'm' + pair[0];
    }

    private String[] implicationSubstringInversion(String[] pairs) {
        String[] invertedPairs = pairs;
        // TODO: implement inversion of implication substrings
        return invertedPairs;
    }

    private String[] implicationSubstrings(int convertedImplicationIndex) {
        String[] pair = new String[2];
        String cE = this.expression.getConvertedExpression();
        // TODO: implement substring extraction of implication substrings
        return pair;
    }

    public void csvTable(String name, int createNew) throws IOException {
        if (createNew < 0 || createNew > 1)
            throw new IOException("new must be 0 or 1");

        FileWriter csvWriter = null;
        Path path = Paths.get(".\\src\\LogicExpressions\\PropositionalLogic\\PropositionData\\ModelTableData\\");
        String file = name + sentenceCount + "-ModelTable.csv";

        if (createNew == 1) {
            int i = 1;
            while (Files.exists(path.resolve(file))) {
                file = name + sentenceCount + "-(" + i + ")" + "-ModelTable.csv";
                i++;
            }
        }

        try {
            csvWriter = new FileWriter(path.resolve(file).toString());

            for (int i = 0; i < truthTable.length; i++) {
                for (int j = 0; j < truthTable[i].length; j++) {
                    csvWriter.append(truthTable[i][j]);
                    csvWriter.append(", ");
                }
                csvWriter.append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                csvWriter.flush();
                csvWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void csvExpressions(String e, int createNew) throws IOException {
        if (createNew < 0 || createNew > 1)
            throw new IOException("new must be 0 or 1");

        FileWriter csvWriter = null;
        Path path = Paths.get(".\\src\\LogicExpressions\\PropositionalLogic\\PropositionData\\Expressions\\");
        String file = e + sentenceCount + "Expr.csv";

    }

    private class PropositionOperators {
        // boolean methods
        public boolean not(boolean operand) {
            return !operand;
        }

        public boolean and(boolean left, boolean right) {
            return left && right;
        }

        public boolean or(boolean left, boolean right) {
            return left || right;
        }

        public boolean implies(boolean left, boolean right) {
            if (!right && left)
                return false;
            else
                return true;
        }

        public boolean iff(boolean left, boolean right) {
            return left == right;
        }

        public boolean xor(boolean left, boolean right) {
            if (left && !right)
                return true;
            else if (!left && right)
                return true;
            else
                return false;
        }

        // String methods

        public String not(String operand) {
            return (operand.equals("T") ? "F" : "T");
        }

        public String and(String left, String right) {
            if ((!left.equals("T") && !left.equals("F")) || (!right.equals("T") && !right.equals("F")))
                throw new IllegalArgumentException("Invalid operand(s) for and operation.");
            else if (left.equals("T") && right.equals("T"))
                return "T";
            else
                return "F";
        }

        public String or(String left, String right) {
            if ((!left.equals("T") && !left.equals("F")) || (!right.equals("T") && !right.equals("F")))
                throw new IllegalArgumentException("Invalid operand(s) for or operation.");
            else if (left.equals("T") || right.equals("T"))
                return "T";
            else
                return "F";
        }

        public String implies(String left, String right) {
            if ((!left.equals("T") && !left.equals("F")) || (!right.equals("T") && !right.equals("F")))
                throw new IllegalArgumentException("Invalid operand(s) for implies operation.");
            else if (left.equals("T") && right.equals("F"))
                return "F";
            else
                return "T";
        }

        public String iff(String left, String right) {
            if ((!left.equals("T") && !left.equals("F")) || (!right.equals("T") && !right.equals("F")))
                throw new IllegalArgumentException("Invalid operand(s) for iff operation.");
            else if (left.equals(right))
                return "T";
            else
                return "F";
        }

        public String xor(String left, String right) {
            if ((!left.equals("T") && !left.equals("F")) || (!right.equals("T") && !right.equals("F")))
                throw new IllegalArgumentException("Invalid operand(s) for xor operation.");
            else if (left.equals("T") && right.equals("F"))
                return "T";
            else if (left.equals("F") && right.equals("T"))
                return "T";
            else
                return "F";
        }

    }

    /**
     * 
     */
    protected class Expression {

        /** logical expression String representing math equation */
        private String expression;

        private LogicalSyntax syntax = new LogicalSyntax();
        /** converted logical expression string for easier back-end operations */
        private String convertedExpression;
        /** Maximum number of characters accepted in converted expression String */
        private final short MAX_CHARACTERS = 255;

        /**
         * @throws InvalidExpressionException
         * @throws InvalidLogicOperatorException
         * @throws InvalidOperandException
         * 
         */
        public Expression()
                throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
            super();
        }

        /**
         * 
         * @param e logical expression argument
         * @throws InvalidExpressionException    thrown by
         *                                       checkExpressionForExceptions()
         * @throws InvalidLogicOperatorException thrown by
         *                                       checkExpressionForExceptions()
         * @throws InvalidOperandException       thrown by
         *                                       checkExpressionForExceptions()
         */
        public Expression(String e)
                throws InvalidExpressionException, InvalidLogicOperatorException, InvalidOperandException {
            loadExpression(e);
        }

        public void setExpression(String e)
        throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
            loadExpression(e);
        }

        public String getExpression() {
            return this.expression;
        }

        public String getConvertedExpression() {
            return this.convertedExpression;
        }

        /**
         * Checks syntax of expression argument and assigns it to expression field if
         * valid
         * 
         * @param e propositional logic expression String
         * @throws InvalidExpressionException
         * @throws InvalidLogicOperatorException
         * @throws InvalidOperandException
         */
        private void loadExpression(String e)
                throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
            e = e.replaceAll("\s", "");
            String cE = convertExpression(e); // converts expression to valid, converted format
            validateSyntax(e, cE); // checks validity of converted expression argument
            this.expression = e;
            this.convertedExpression = cE;
        }

        /**
         * 
         * @param e
         * @throws InvalidExpressionException
         * @throws InvalidOperandException
         * @throws InvalidLogicOperatorException
         */
        private void validateSyntax(String e, String cE)
                throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
            int i = 0;

            if (e.length() > MAX_CHARACTERS)
                throw new InvalidExpressionException("Expression is too long; only 256 characters allowed.");
            else if (!syntax.containsAnyOperands(cE))
                throw new InvalidOperandException("Expression does not have at least one valid operand.");
            else if (e.charAt(0) == ')' || cE.charAt(0) == 'a' || cE.charAt(0) == 'o' || cE.charAt(0) == 'i'
                    || cE.charAt(0) == 'x' || cE.charAt(0) == 'm')
                throw new InvalidLogicOperatorException("Expression cannot begin with invalid operator.");
            else if (e.charAt(e.length() - 1) == '(' || cE.charAt(cE.length() - 1) == 'a'
                    || cE.charAt(cE.length() - 1) == 'o'
                    || cE.charAt(cE.length() - 1) == 'i' || cE.charAt(cE.length() - 1) == 'x'
                    || cE.charAt(cE.length() - 1) == 'm'
                    || cE.charAt(cE.length() - 1) == 'n')
                throw new InvalidLogicOperatorException("Expression cannot end with invalid operator.");
            else if (syntax.containsAnyConversionOperators(cE)) {
                while (i < syntax.getInvalidOperatorPairsSize()) {
                    if (cE.contains(syntax.getInvalidOperatorPairsSet(i)))
                        throw new InvalidLogicOperatorException("Invalid operator syntax in expression;" + " "
                                + syntax.getInvalidOperatorPairsSet(i) + " is not a valid operator pair.");
                    i++;
                }
            }
        }

        public String convertExpression(String e) throws InvalidExpressionException {
            String cE = e; // converted String variable
            cE = cE.replace("<>", syntax.getConversionValueFromOperatorKey("<>"));
            cE = cE.replace("->", syntax.getConversionValueFromOperatorKey("->"));
            cE = cE.replace(">-<", syntax.getConversionValueFromOperatorKey(">-<"));
            cE = cE.replace("~", syntax.getConversionValueFromOperatorKey("~"));
            cE = cE.replace("&", syntax.getConversionValueFromOperatorKey("&"));
            cE = cE.replace("|", syntax.getConversionValueFromOperatorKey("|"));

            if (e.contains("(") || e.contains(")")) {
                // count to ensure there are not more than 10 parentheses
                Integer lpCount = 0, rpCount = 0;
                for (int i = 0; i < e.length(); i++) {
                    if (e.charAt(i) == '(')
                        lpCount++;
                    else if (e.charAt(i) == ')')
                        rpCount++;
                }
                if (lpCount > 10 || rpCount > 10)
                    throw new InvalidExpressionException("Must be less than 11 left and right parentheses.");
                else if (lpCount != rpCount)
                    throw new InvalidExpressionException("left and right parentheses count is not equal.");

                // replace first 10 left parentheses with 0-9
                lpCount = 0;
                while (cE.contains("(")) {
                    cE = cE.replaceFirst("\\(", lpCount.toString());
                    lpCount++;
                }

                // then replace last 10 right parentheses with 9-0 according to order
                Stack<Integer> lpStack = new Stack<Integer>();
                for (int i = 0; i < cE.length(); i++) {
                    if (Character.isDigit(cE.charAt(i))) {
                        lpStack.push(Integer.parseInt(cE.charAt(i) + ""));
                    } else if (cE.charAt(i) == ')') {
                        cE = cE.replaceFirst("\\)", Integer.toString(lpStack.pop()));
                    }
                }
                lpStack.clear();

            }
            return cE;
        }
    }

    protected static class LogicalSyntax {

        /**
         * custom/default loaded operand list for propositional logic
         */
        private final ArrayList<Character> OPERAND_LIST = new ArrayList<Character>() {
            {
                add('A');
                add('B');
                add('C');
                add('D');
                add('E');
                add('G');
                add('H');
                add('I');
                add('J');
                add('K');
                add('L');
                add('M');
                add('N');
                add('O');
                add('P');
                add('Q');
                add('R');
                add('S');
                add('U');
                add('V');
                add('W');
                add('X');
                add('Y');
                add('Z');
                /** below are treated as true and false, not as normal operands */
                add('T');
                add('F');
            }
        };

        /** Operator map index reference integers */
        private static final int OPERATOR_CONVERSION_INDEX = 0;
        private final int OPERATOR_NAME_INDEX = 1;

        /**
         * Operator map for all hand-typed operators and their corresponding conversion
         * values and names
         */
        private final Map<String, ArrayList<String>> OPERATOR_MAPS = new HashMap<String, ArrayList<String>>() {
            {
                put("&", new ArrayList<String>() {
                    {
                        add("a");
                        add("and");
                    }
                });
                put("|", new ArrayList<String>() {
                    {
                        add("o");
                        add("or");
                    }
                });
                put("->", new ArrayList<String>() {
                    {
                        add("m");
                        add("implies");
                    }
                });
                put("<>", new ArrayList<String>() {
                    {
                        add("i");
                        add("iff");
                    }
                });
                put("~", new ArrayList<String>() {
                    {
                        add("n");
                        add("not");
                    }
                });
                put(">-<", new ArrayList<String>() {
                    {
                        add("x");
                        add("xor");
                    }
                });
                put("(", new ArrayList<String>() {
                    {
                        add("(");
                        add("left-parenthesis");
                    }
                });
                put(")", new ArrayList<String>() {
                    {
                        add(")");
                        add("right-parenthesis");
                    }
                });
            }
        };

        private final ArrayList<String> INVALID_OPERATOR_PAIRS = new ArrayList<String>() {
            {
                add("na");
                add("no");
                add("nm");
                add("nx");
                add("n)");
                add("ni");

                add("aa");
                add("aaa");
                add("ao");
                add("am");
                add("ax");
                add("a)");
                add("ai");

                add("oo");
                add("ooo");
                add("oa");
                add("om");
                add("ox");
                add("o)");
                add("oi");

                add("mm");
                add("mmm");
                add("mo");
                add("ma");
                add("mx");
                add("m)");
                add("mi");

                add("xx");
                add("xxx");
                add("xa");
                add("xo");
                add("xm");
                add("x)");
                add("xi");

                add("ii");
                add("iii");
                add("ia");
                add("io");
                add("im");
                add("i)");
                add("ix");

                add("()");
                add("(o");
                add("(a");
                add("(m");
                add("(x");
                add("(i");

                add(")(");
            }
        };

        public ArrayList<Character> getOperandList() {
            return this.OPERAND_LIST;
        }

        public Map<String, ArrayList<String>> getOperatorMaps() {
            return this.OPERATOR_MAPS;
        }

        public ArrayList<String> getInvalidOperatorPairs() {
            return this.INVALID_OPERATOR_PAIRS;
        }

                /**
         * Used to get the operator/key from a given value
         * 
         * @param value
         * @return
         */
        public String getOperatorKeyFromValue(String value) {
            for (String key : OPERATOR_MAPS.keySet()) {
                if (OPERATOR_MAPS.get(key).contains(value)) {
                    return key;
                }
            }
            return null;
        }

        /**
         * Used to get the converted value from a given operator/key
         * 
         * @param operator
         * @return
         */
        public String getConversionValueFromOperatorKey(String operator) {
            return OPERATOR_MAPS.get(operator).get(OPERATOR_CONVERSION_INDEX);
        }

        /**
         * Used to get the converted value from a given name value
         * 
         * @param name
         * @return
         */
        public String getConversionValueFromNameValue(String name) {
            for (String key : OPERATOR_MAPS.keySet()) {
                if (OPERATOR_MAPS.get(key).contains(name)) {
                    return OPERATOR_MAPS.get(key).get(OPERATOR_CONVERSION_INDEX);
                }
            }
            return null;
        }

        /**
         * Used to get the name of the operator/key from a given operator/key
         * 
         * @param operator
         * @return
         */
        public String getNameValueFromOperatorKey(String operator) {
            return OPERATOR_MAPS.get(operator).get(OPERATOR_NAME_INDEX);
        }

        /**
         * Used to get the name valueof the operator/key from a given conversion value
         * 
         * @param conversion
         * @return
         */
        public String getNameValueFromConversionValue(String conversion) {
            for (String key : OPERATOR_MAPS.keySet()) {
                if (OPERATOR_MAPS.get(key).contains(conversion)) {
                    return OPERATOR_MAPS.get(key).get(OPERATOR_NAME_INDEX);
                }
            }
            return null;
        }

        /**
         * 
         * @return
         */
        public String getStringOperatorKeys() {
            return OPERATOR_MAPS.keySet().toString();
        }

        /**
         * 
         * @return
         */
        public String getStringOperatorConversion() {
            ArrayList<String> conversions = new ArrayList<String>();
            for (String key : OPERATOR_MAPS.keySet()) {
                conversions.add(OPERATOR_MAPS.get(key).get(OPERATOR_CONVERSION_INDEX));
            }
            return conversions.toString();
        }

        /**
         * 
         * @return
         */
        public String getStringOperatorName() {
            ArrayList<String> names = new ArrayList<String>();
            for (String key : OPERATOR_MAPS.keySet()) {
                names.add(OPERATOR_MAPS.get(key).get(OPERATOR_NAME_INDEX));
            }
            return names.toString();
        }

        public ArrayList<Character> getValidOperators() {
            return OPERAND_LIST;
        }

        // ~~~~~~~~LOGICAL METHODS~~~~~~~~

        public int getInvalidOperatorPairsSize() {
            return INVALID_OPERATOR_PAIRS.size();
        }

        public String getInvalidOperatorPairsSet(int i) {
            return INVALID_OPERATOR_PAIRS.get(i);
        }

        // ~~~~~~~~ BOOLEAN METHODS~~~~~~~~

        /**
         * 
         * @param c
         * @return
         */
        public boolean isOperand(char c) {
            if (c == 'T' || c == 'F') {
                return false;
            }
            return OPERAND_LIST.contains(c);
        }

        /**
         * 
         * @param s
         * @return
         */
        public boolean isOperand(String s) {
            if (s == "T" || s == "F") {
                return false;
            }
            return OPERAND_LIST.contains(s);
        }

        /**
         * 
         * @param s
         * @param c
         * @return
         */
        public boolean containsOperand(String s, char operand) {

            return s.contains(Character.toString(operand));
        }

        /**
         * 
         */
        public boolean containsOperand(String s, String operand) {
            return s.contains(operand);
        }

        /**
         * 
         * @param s
         * @return
         */
        public boolean containsAnyOperands(String s) {
            for (char c : OPERAND_LIST) {
                if (s.contains(Character.toString(c)) && (c != 'T' && c != 'F')) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 
         * @return
         */
        public String getValidOperands() {
            return OPERAND_LIST.toString();
        }

        // ~~~~~~~~OPERATOR METHODS~~~~~~~~
        /**
         * Used to check if a specific, statemented string/sequence of characters IS an
         * operator/key
         * 
         * @param s
         * @return
         */
        public boolean isOperator(String s) {
            return OPERATOR_MAPS.containsKey(s);
        }

        /**
         * Used to check if a specific, statemented string/sequence of characters HAS a
         * given operator/key
         * 
         * @param s
         * @param key
         * @return
         */
        public boolean containsOperator(String s, String key) {
            return s.contains((CharSequence) OPERATOR_MAPS.get(key));
        }

        /**
         * Used to check if a specific, statemented string/sequence of characters HAS
         * ANY operators/keys
         * 
         * @param s
         * @return
         */
        public boolean containsAnyOperators(String s) {
            for (String key : OPERATOR_MAPS.keySet()) {
                if (s.contains(key)) {
                    return true;
                }
            }

            return false;
        }

        public boolean containsAnyConversionOperators(String s) {
            for (String key : OPERATOR_MAPS.keySet()) {
                if (s.contains(OPERATOR_MAPS.get(key).get(OPERATOR_CONVERSION_INDEX))) {
                    return true;
                }
            }
            return false;
        }
    }
}
