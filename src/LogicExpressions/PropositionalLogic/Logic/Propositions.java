package src.LogicExpressions.PropositionalLogic.Logic;

import src.LogicExpressions.PropositionalLogic.Laws.PropositionLaws;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import src.Exceptions.*;
import src.Interfaces.Equivalencies;

/**
 * 
 */
public class Propositions implements Equivalencies {
    /** Logical expression, its string and other functions */
    private Expression expression;
    /** syntax for propositional logic */
    private LogicalSyntax syntax = new LogicalSyntax();
    /** collection of partitioned/parsed propositional statements */
    private LinkedList<String> statements;
    /** count of statements from expression */
    private int statementCount;
    /** operands contained in expression's String */
    private ArrayList<String> operands;
    /** count of operands in expression */
    private int operandCount;
    /** combination of operands and statement propositional statements */
    private ArrayList<String> propositions;
    /** count of propositions in expression */
    private int propositionCount;
    /** truth table for expression */
    private String[][] truthTable;
    /** value table for truth table */
    private Boolean[][] valueTable;
    /** count of values in truth table */
    private int valueCount;
    /** count of rows in truth table */
    private int valueRows;
    /** count of columns in truth table */
    private int valueCols;

    public Propositions()
            throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
        super();
    }

    public Propositions(String e)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        this.expression = new Expression(e);
        setPropositions();
        setTruthTable();
    }

    private void parseOperands() {
        operands = new ArrayList<String>();
        for (Character c : this.expression.getConvertedExpression().toCharArray()) {
            if (syntax.isOperand(c)) {
                if (!operands.contains(c.toString())) {
                    operands.add(c.toString());
                    operandCount++;
                }
            }
        }
    }

    /**
     * Writing this method gave me a headache. Parsed statements based off of parentheses. 
     * 
     * METHOD MUST BE REWRITTEN TO ACCOUNT FOR NUMERICAL REPRESENTATION ON PARENTHESES.
     * @return
     */
    private void parseStatements() {
        Queue<Character> statementCharQueue = new ArrayDeque<Character>();
        statements = new LinkedList<String>();
        String tempPartition = "";
        String cE = this.expression.getConvertedExpression();



        for (int p = 0; p < cE.length(); p++) {
            if (cE.contains("(")) {
                if (!statementCharQueue.isEmpty() && cE.charAt(p) == '(') {
                    if (syntax.containsAnyConversionOperators(statementCharQueue.peek() + "")) {
                        while (!statementCharQueue.isEmpty())
                            statementCharQueue.remove();
                    } else if (syntax.isOperand(statementCharQueue.peek())) {
                        while (!statementCharQueue.isEmpty()) {
                            tempPartition += statementCharQueue.peek();
                            statementCharQueue.remove();
                        }
                        statements.add(tempPartition);
                        tempPartition = "";
                        statementCount++;
                    }
                }
                if (syntax.containsAnyConversionOperators(cE.charAt(p) + "")) {
                    if (cE.charAt(p) == ')') {
                        statementCharQueue.add(cE.charAt(p));
                        while (!statementCharQueue.isEmpty()) {
                            tempPartition += statementCharQueue.peek();
                            statementCharQueue.remove();
                        }
                        statements.add(tempPartition);
                        tempPartition = "";
                        statementCount++;
                    } else
                        statementCharQueue.add(cE.charAt(p));
                } else if (syntax.isOperand(cE.charAt(p))) {
                    statementCharQueue.add(cE.charAt(p));
                }
            } else {
                if (syntax.containsAnyConversionOperators(cE.charAt(p) + "")) {
                    statementCharQueue.add(cE.charAt(p));
                } else if (syntax.isOperand(cE.charAt(p))) {
                    statementCharQueue.add(cE.charAt(p));
                }
            }
        }
        // then convert expressions back to original format
        for (int i = 0; i < statements.size(); i++) {
            statements.set(i, this.expression.revertConvertedExpression(statements.get(i)));
        }

        if (!statements.contains(expression.getExpression())) {
            statements.add(expression.getExpression());
            statementCount++;
        }
    }

    /**
     * Helper method for parsePropositions() method, balances tree expression
     * statements
     */
    private void setPropositions() {
        propositions = new ArrayList<String>();

        parseOperands();
        for (int i = 0; i < this.operands.size(); i++) {
            propositions.add(this.operands.get(i));
            propositionCount++;
        }

        parseStatements();
        for (int i = 0; i < this.statements.size(); i++) {
            propositions.add(this.statements.get(i));
            propositionCount++;
        }
    }

    /**
     * recursively combines sets of T and F values for each operand in expression,
     * and then assigns them to the string truthTable and
     * the boolean valueTable after all combinations are made in the recursive
     * winding up.
     * 
     * @param n
     * @param prefix
     */
    private void combineOperandValues() {
        String[] operandValues = new String[operandCount];
        for (int i = 0; i < operandCount; i++)
            operandValues[i] = "T";

        for (int i = 0; i < valueRows; i++) {
            for (int j = 0; j < operandCount; j++) {
                if (operandValues[j].equals("T")) {
                    operandValues[j] = "F";
                    break;
                } else {
                    operandValues[j] = "T";
                }
            }
            for (int j = 0; j < operandCount; j++) {
                valueTable[i][j] = operandValues[j].equals("T") ? true : false;
                truthTable[i + 1][j] = operandValues[j];
            }
        }
    }

    private void setTruthTable() {
        valueRows = (int) Math.pow(2, operandCount);
        valueCols = propositions.size();
        valueCount = valueRows * valueCols;
        valueTable = new Boolean[valueRows][valueCols];
        truthTable = new String[valueRows + 1][valueCols]; // +1 for column titles

        for (int i = 0; i < propositions.size(); i++)
            truthTable[0][i] = propositions.get(i) + ""; // titles each column with corresponding
                                                         // proposition/compound proposition

        combineOperandValues();
        int combinationCount = valueTable.length;

        // HashMap<Character, Character> valueMap = new HashMap<>();
        // int row = 0;
        // do {
        //     for (int i = 0; i < operandCount; i++) 
        //         valueMap.put(operands.get(i).charAt(0), truthTable[row+1][i].charAt(0));

        //     for (int i = 0; i < statementCount; i++) {
        //         valueTable[row][i+operandCount] = evaluateExpression(statements.get(i), valueMap);
        //         truthTable[row+1][i+operandCount] = valueTable[row][i+operandCount] ? "T" : "F";
        //     }

        //     row++;
        //     valueMap.clear();
        // } while (row < valueRows);

    }

    public boolean evaluateExpression(String expression, HashMap<Character, Character> valueMap) {
        PropositionEvaluator evaluator = new PropositionEvaluator();
        boolean answer = false;

        expression = this.expression.convertExpression(expression);

        String markedExpression = "";
        for (int i = 0; i < expression.length(); i++) {
            if (syntax.isOperand(expression.charAt(i) + ""))
                markedExpression.replace(expression.charAt(i) + "", valueMap.get(expression.charAt(i)) + "");
        }

        int opCount = 0;
        if (expression.contains("n")) {
            opCount = (int) expression.chars().filter(c -> c == 'n').count();
            for ( ; opCount > 0; opCount--) {
                if (markedExpression.contains("nT")) {
                    expression = expression.replace("nT", "F");
                } else if (markedExpression.contains("nF")) {
                    expression = expression.replace("nF", "T");
                } else if (expression.contains("n(")) {

                }
            }
        }

        for (int i = 0; i < expression.length(); i++) {

        }


        return answer;

    }

    public String getExpression() {
        return this.expression.getExpression();
    }

    public String getConvertedExpression() {
        return this.expression.getConvertedExpression();
    }

    public void setExpression(String e)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        this.expression.setExpression(e);
        setPropositions();
        setTruthTable();
    }

    public String getPropositions() {
        String statements = "";

        for (int i = 0; i < propositions.size(); i++) {
            if (!(i == propositions.size() - 1))
                statements += (propositions.get(i) + " , ");
            else
                statements += propositions.get(i);
        }

        return statements;
    }

    public String getPropositions(int from, int to) throws IndexOutOfBoundsException {
        if ((from < 0) || (to > propositions.size()))
            throw new IndexOutOfBoundsException(to + " is out of bounds.");

        if (from > to)
            return "";
        else if (!(from == to))
            return propositions.get(from) + " , " + getPropositions(from + 1, to);
        else
            return propositions.get(from);
    }

    public String[][] getTruthTable() {
        return this.truthTable;
    }

    public String[] getStringTableRow(int row) {
        return this.truthTable[row];
    }

    public String[] getStringTableColumn(int col) {
        String[] column = new String[truthTable.length];
        for (int i = 0; i < truthTable.length; i++)
            column[i] = truthTable[i][col];

        return column;
    }

    public Boolean[][] getBooleanTable() {
        return this.valueTable;
    }

    public Boolean[] getBooleanTableRow(int row) {
        return this.valueTable[row];
    }

    public Boolean[] getBooleanTableColumn(int col) {
        Boolean[] column = new Boolean[valueCount];
        int columnElements = valueCount / valueCols;

        for (int i = 0; i < columnElements; i++)
            column[i] = valueTable[i][col];

        return column;
    }

    public void printTruthTable() {
        for (int i = 0; i < truthTable[i].length; i++) {
            System.out.print(i + ".\s");
            System.out.print(truthTable[0][i] + "\s\s\s");
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

    public void printTruthTable(int fromCol, int toCol) throws IndexOutOfBoundsException {
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

    public void csvTable(String name, int createNew) throws IOException {
        if (createNew < 0 || createNew > 1)
            throw new IOException("new must be 0 or 1");

        FileWriter csvWriter = null;
        Path path = Paths.get(".\\src\\LogicExpressions\\PropositionalLogic\\PropositionData\\TableData\\");
        String file = name + propositionCount + "TT.csv";

        if (createNew == 1) {
            int i = 1;
            while (Files.exists(path.resolve(file))) {
                file = name + propositionCount + "(" + i + ")" + "TT.csv";
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
        String file = e + propositionCount + "Expr.csv";

    }

    public String inverse(String p) throws InvalidExpressionException {
        if (!p.contains("->") || !p.contains("m"))
            throw new InvalidExpressionException("Invalid proposition; must contain implication");

        if (p.contains("->")) {

        } else if (p.contains("m")) {

        }
    }

    public String converse(String p) throws InvalidExpressionException {
        if (!p.contains("->") || !p.contains("m"))
            throw new InvalidExpressionException("Invalid proposition; must contain implication");

        if (p.contains("->")) {

        } else if (p.contains("m")) {
            
        }
    }

    public String contrapositive(String p) throws InvalidExpressionException {
        if (!p.contains("->") || !p.contains("m"))
            throw new InvalidExpressionException("Invalid proposition; must contain implication");

        if (p.contains("->")) {

        } else if (p.contains("m")) {
            
        }
    }

    @Override
    public boolean isTautology(String[] rowOrColumn) {
        for (String s : rowOrColumn) {
            if (s.equals("F"))
                return false;
        }
        return true;
    }

    @Override
    public boolean isTautology(Boolean[] rowOrColumn) {
        for (Boolean b : rowOrColumn) {
            if (b == false)
                return false;
        }
        return true;
    }

    @Override
    public boolean isContradiction(String[] rowOrColumn) {
        for (String s : rowOrColumn) {
            if (s.equals("T"))
                return false;
        }
        return true;
    }

    @Override
    public boolean isContradiction(Boolean[] rowOrColumn) {
        for (Boolean b : rowOrColumn) {
            if (b == true)
                return false;
        }
        return true;
    }

    @Override
    public boolean isContingency(String[] rowOrColumn) {
        return !(isTautology(rowOrColumn) || isContradiction(rowOrColumn));
    }

    @Override
    public boolean isContingency(Boolean[] rowOrColumn) {
        return !(isTautology(rowOrColumn) || isContradiction(rowOrColumn));
    }

    private class PropositionEvaluator {

        public PropositionEvaluator() {
            super();
        }

        public boolean not(boolean operand) {
            return (operand == true ? false : true);
        }

        public boolean and(boolean left, boolean right) {
            return (left && right);
        }

        public boolean or(boolean left, boolean right) {
            return (left || right);
        }

        public boolean implies(boolean left, boolean right) {
            if (right == false && left == true)
                return false;
            else
                return true;
        }

        public boolean iff(boolean left, boolean right) {
            if (left == right)
                return true;
            else
                return false;
        }

        public boolean xor(boolean left, boolean right) {
            if (left == true && right == false)
                return true;
            else if (left == false && right == true)
                return true;
            else
                return false;
        }

    }

    /**
     * 
     */
    private class Expression {

        /** logical expression String representing math equation */
        private String expression;

        private LogicalSyntax syntax = new LogicalSyntax();
        /** converted logical expression string for easier back-end operations */
        private String convertedExpression;
        /** Maximum number of characters accepted in converted expression String */
        private final int MAX_CHARACTERS = 32;

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

        /**
         * 
         * @param e
         * @throws InvalidExpressionException
         * @throws InvalidOperandException
         * @throws InvalidLogicOperatorException
         */
        private void checkSyntax(String cE)
                throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
            int i = 0;
            if (cE.length() > MAX_CHARACTERS)
                throw new InvalidExpressionException("Expression is too long; only 32 converted characters allowed.");
            else if (!syntax.containsAnyOperands(cE))
                throw new InvalidOperandException("Expression does not have at least one valid operand.");
            else if (syntax.containsAnyConversionOperators(cE)) {
                while (i < syntax.getInvalidOrderSize()) {
                    if (cE.contains(syntax.getInvalidOrderSet(i)))
                        throw new InvalidLogicOperatorException("Invalid operator syntax in expression.");
                    i++;
                }
                i = 0;
                // TODO: CHANGE SYNTAX CHECKER TO ACCOUNT FOR NUMERICAL REPRESENTATION ON PARENTHESES
                // check parenthesis count for both ( and ), and then compare if they are equal
                int leftParentheses = 0, rightParentheses = 0;
                for (int j = 0; j < cE.length(); j++) {
                    if (cE.charAt(j) == '(')
                        leftParentheses++;
                    else if (cE.charAt(j) == ')')
                        rightParentheses++;
                }

                if (leftParentheses != rightParentheses)
                    throw new InvalidExpressionException("left-and-right parentheses count is not equal.");
            }
        }

        public String convertExpression(String e) {
            String cE = e; // converted String variable
            cE = cE.replaceAll("<>", syntax.getConversionValueFromOperatorKey("<>"));
            cE = cE.replaceAll("->", syntax.getConversionValueFromOperatorKey("->"));
            cE = cE.replaceAll(">-<", syntax.getConversionValueFromOperatorKey(">-<"));
            cE = cE.replaceAll("~", syntax.getConversionValueFromOperatorKey("~"));
            cE = cE.replaceAll("&", syntax.getConversionValueFromOperatorKey("&"));
            cE = cE.replaceAll("|", syntax.getConversionValueFromOperatorKey("|"));

            if (e.contains("(")) {
                Integer lp = 1;
                Integer rp = 0;
                for (int i = 0; i < e.length(); i++) {
                    if (e.charAt(i) == '(') {
                        cE.replaceFirst("(", lp.toString());
                        lp++;
                    }
                    
                    if (!cE.contains("(")) {
                        rp = lp - 1;
                        cE.replaceFirst(")", rp.toString());
                        lp--;
                    }
                }
            }
            return cE;
        }

        public String revertConvertedExpression(String e) {
            String rE = e; // reverted String variable
            rE = rE.replaceAll("i", syntax.getOperatorKeyFromValue("i"));
            rE = rE.replaceAll(syntax.getConversionValueFromOperatorKey("->"), "->");
            rE = rE.replaceAll(syntax.getConversionValueFromOperatorKey(">-<"), ">-<");
            rE = rE.replaceAll(syntax.getConversionValueFromOperatorKey("~"), "~");
            rE = rE.replaceAll(syntax.getConversionValueFromOperatorKey("&"), "&");
            rE = rE.replaceAll(syntax.getConversionValueFromOperatorKey("|"), "|");

            return rE;
        }

        /**
         * Checks syntax of expression argument and assigns it to expression field if
         * valid
         * 
         * @param e propositional logic expression String
         * @throws InvalidExpressionException
         * @throws InvalidLogicOperatorExcept
         * @throws InvalidOperandException
         */
        private void loadExpression(String e)
                throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
            e = e.replaceAll("\s", syntax.getConversionValueFromOperatorKey("\s"));
            String cE = convertExpression(e); // converts expression to valid, converted format
            checkSyntax(cE); // checks validity of converted expression argument
            this.expression = e;
            this.convertedExpression = cE;
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
                put("\s", new ArrayList<String>() {
                    {
                        add("");
                        add("space");
                    }
                });
            }
        };

        private final ArrayList<String> INVALID_OPERATOR_ORDER = new ArrayList<String>() {
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
                add("on");
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
            }
        };

        /**
         * default constructor calls super()/Object constructor
         */
        public LogicalSyntax() {
            super();
        }

        // ~~~~~~~~OPERAND METHODS~~~~~~~~

        /**
         * 
         * @param c
         * @return
         */
        public boolean isOperand(char c) {
            return OPERAND_LIST.contains(c);
        }

        /**
         * 
         * @param s
         * @return
         */
        public boolean isOperand(String s) {
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
                if (s.contains(Character.toString(c))) {
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

        public int getInvalidOrderSize() {
            return INVALID_OPERATOR_ORDER.size();
        }

        public String getInvalidOrderSet(int i) {
            return INVALID_OPERATOR_ORDER.get(i);
        }

    }

}
