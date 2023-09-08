package src.LogicExpressions.PropositionalLogic.Logic;

import src.LogicExpressions.PropositionalLogic.Laws.PropositionLaws;

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
    private LinkedList<String> partitions;
    /** queue of partitioned/parsed propositional statements */
    private Queue<Character> partitionCharQueue;
    /** count of partitions from expression */
    private int partitionCount;
    /** operands contained in expression's String */
    private ArrayList<String> operands;
    /** count of operands in expression */
    private int operandCount;
    /** combination of operands and partition propositional statements */
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
        this.expression = new Expression();
        setPropositions();
        setTruthTable();
        this.laws = null;
    }

    public Propositions(String e)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        this.expression = new Expression(e);
        setPropositions();
        setTruthTable();
        this.laws = null;
    }

    private void parseOperands() {
        operands = new ArrayList<String>();
        for (Character c : this.expression.getConvertedExpression().toCharArray()) {
            if (syntax.isOperand(c)) {
                if (!operands.contains(c.toString())) {
                    operands.add(c.toString());
                    operandCount++;
                    propositionCount++;
                }
            }
        }
    }

    /**
     * Utilizes a tree data structure to parse the expression into its constituent
     * propositions
     * 
     * @param e
     * @return
     */
    private void parsePartitions() {
        partitionCharQueue = new ArrayDeque<Character>();
        Queue<Character> tempQueue = new ArrayDeque<Character>();
        partitions = new LinkedList<String>();
        String tempPartition = "";
        String cE = this.expression.getConvertedExpression();

        for (int p = 0; p < cE.length(); p++) {
            if (syntax.isConversionOperator(cE.charAt(p))) {
                if (cE.contains("(")) {
                    if (cE.charAt(p) == ')') {
                        partitionCharQueue.add(cE.charAt(p));
                        while (!partitionCharQueue.isEmpty()) {
                            tempPartition += partitionCharQueue.peek();
                            partitionCharQueue.remove();
                        }
                        partitions.add(tempPartition);
                        tempPartition = "";
                        partitionCount++;
                    } else {
                        tempQueue.add(cE.charAt(p));
                        partitionCharQueue.add(cE.charAt(p));
                    }
                }
            } else if (syntax.isOperand(cE.charAt(p))) {
                partitionCharQueue.add(cE.charAt(p));
            } else {

            }
        }

        if (!partitions.contains(cE)) {
            partitions.add(this.expression.getExpression());
            partitionCount++;
        }

        // then convert expressions back to original format
        for (int i = 0; i < partitions.size(); i++) {
            partitions.set(i, this.expression.revertExpression(partitions.get(i)));
        }
    }

    /**
     * Helper method for parsePropositions() method, balances tree expression
     * partitions
     */
    private void setPropositions() {
        propositions = new ArrayList<String>();

        parseOperands();
        for (int i = 0; i < this.operands.size(); i++) {
            propositions.add(this.operands.get(i));
        }

        parsePartitions();
        for (int i = 0; i < this.partitions.size(); i++) {
            propositions.add(this.partitions.get(i));
        }

        // parsePartitions();
        // for (int i = 0; i < this.partitions.size(); i++) {
        // propositions.add(this.partitions.get(i));
        // }

        // for (int i = 0; i < this.partitions.size(); i++) {
        // propositions.add(this.partitions.get(i));
        // }
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
    }

    public String getPropositions() {
        String statements = "";

        for (int i = 0; i < propositions.size(); i++) {
            if (!(i == propositions.size() - 1))
                statements += (propositions.get(i) + " , ");
            else
                statements += (propositions.get(i));
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

    private void permuteOperandValues(int n, String prefix) {
        final char[] chars = { 'T', 'F' };
        // do {
        if (n == 0) {
            for (int i = 0; i < 1; i++) {
                for (int j = 0; j < operandCount; j++) {
                    truthTable[i + 1][j] = prefix.charAt(j) + "";
                }

            }
            return;

        }

        for (int i = 0; i < chars.length; i++)
            permuteOperandValues(n - 1, prefix + chars[i]);

    }

    private void evaluatePartitionValues() {

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

        permuteOperandValues(operandCount, "");
        int permutationCount = valueTable.length;

        // for (int i = 1; i < permutationCount; i++) {
        // for(int j = 0; i < elements[i].length; j++) {
        // truthTable[i][j] = elements[i][j];
        // // if (elements[i][j].equals("T"))
        // valueTable[i][j] = true;
        // else
        // // valueTable[i][j] = false;
        // }
        // }

        //
        // for (int i = 0; i < valueTable.length; i++) {
        // for (int j = 0; j < operandCount; j++) {
        // }

        // }
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

        //for (int i = fromCol; i < toCol; i++)

    }

    public String inverse(String p) {

    }

    public String converse(String p) {

    }

    public String contrapositive(String p) {

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
        private Expression expression;

        public PropositionEvaluator() {
            super();
        }

        public PropositionEvaluator(Expression e) {
            this.expression = e;
        }

        public boolean and(String p, String q) {
            return (p.equals("T") && q.equals("T"));
        }

        public boolean or(String p, String q) {
            return (p.equals("T") || q.equals("T"));
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
            loadExpression("(~P&Q)|~R "); // example expression
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

                if ((cE.contains("(") && !cE.contains(")")) || (!cE.contains("(") && cE.contains(")")))
                    throw new InvalidExpressionException("Expression is missing parenthesis.");
            }
        }

        public String convertExpression(String e) {
            String cE = e; // converted String variable
            cE = cE.replaceAll("<>", syntax.getConversionValueFromOperatorKey("<>"));
            cE = cE.replaceAll("->", syntax.getConversionValueFromOperatorKey("->"));
            cE = cE.replaceAll(">-<", syntax.getConversionValueFromOperatorKey(">-<"));
            cE = cE.replaceAll("<-", syntax.getConversionValueFromOperatorKey("<-"));
            cE = cE.replaceAll("\s", syntax.getConversionValueFromOperatorKey("\s"));
            return cE;
        }

        public String revertExpression(String e) {
            String rE = e; // reverted String variable
            rE = rE.replaceAll("i", syntax.getOperatorKeyFromValue("i"));
            rE = rE.replaceAll(syntax.getConversionValueFromOperatorKey("->"), "->");
            rE = rE.replaceAll(syntax.getConversionValueFromOperatorKey(">-<"), ">-<");
            rE = rE.replaceAll(syntax.getConversionValueFromOperatorKey("<-"), "<-");
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
         * Default operand list for propositional logic
         */
        private final ArrayList<Character> DEFAULT_OPERAND_LIST = new ArrayList<Character>() {
            {
                add('A');
                add('B');
                add('C');
                add('D');
                add('E');
                add('F');
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
                add('T');
                add('U');
                add('V');
                add('W');
                add('X');
                add('Y');
                add('Z');
            }
        };

        private ArrayList<Character> CUSTOM_OPERAND_LIST = new ArrayList<Character>();

        /**
         * custom/default loaded operand list for propositional logic
         */
        private ArrayList<Character> OPERAND_LIST = new ArrayList<Character>() {
            {
                addAll(DEFAULT_OPERAND_LIST);
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
                        add("&");
                        add("and");
                    }
                });
                put("|", new ArrayList<String>() {
                    {
                        add("|");
                        add("or");
                    }
                });
                put("->", new ArrayList<String>() {
                    {
                        add(">");
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
                        add("~");
                        add("not");
                    }
                });
                put(">-<", new ArrayList<String>() {
                    {
                        add("x");
                        add("xor");
                    }
                });
                put("<-", new ArrayList<String>() {
                    {
                        add("<");
                        add("reduces");
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
                put("T", new ArrayList<String>() {
                    {
                        add("T");
                        add("true");
                    }
                });
                put("F", new ArrayList<String>() {
                    {
                        add("F");
                        add("false");
                    }
                });
            }
        };

        private final ArrayList<String> INVALID_OPERATOR_ORDER = new ArrayList<String>() {
            {
            add("~&");
            add("~|");
            add("~>");
            add("~<");
            add("~x");
            add("~)");
            add("~i");

            add("&&");
            add("&&&");
            add("&|");
            add("&>");
            add("&<");
            add("&x");
            add("&)");
            add("&i");

            add("||");
            add("|||");
            add("|&");
            add("|>");
            add("|<");
            add("|x");
            add("|)");
            add("|i");

            add(">>");
            add(">>>");
            add(">|");
            add(">&");
            add(">x");
            add(">)");
            add(">i");

            add("xx");
            add("xxx");
            add("x&");
            add("x|");
            add("x>");
            add("x<");
            add("x)");
            add("xi");

            add("<<");
            add("<<<");
            add("<|");
            add("<&");
            add("<x");
            add("<)");
            add("<i");
            add("<>");

            add("ii");
            add("iii");
            add("i&");
            add("i|");
            add("i>");
            add("i<");
            add("i)");
            add("ix");

            add("()");
            add("(|");
            add("(&");
            add("(>");
            add("(<");
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
        public void customOperands(ArrayList<Character> ops) throws IOException {
            final int MAX_OPERANDS = 30;
            if (ops.size() > MAX_OPERANDS) {
                throw new IOException("Custom operands cannot exceed " + MAX_OPERANDS + " characters");
            }
            for (char c : ops) {
                if (OPERATOR_MAPS.containsKey(Character.toString(c))) {
                    throw new IOException("Custom operands cannot contain operators");
                }
            }
            CUSTOM_OPERAND_LIST.clear();
            CUSTOM_OPERAND_LIST.addAll(ops);
            OPERAND_LIST.addAll(CUSTOM_OPERAND_LIST);
        }

        public void resetOperands() {
            CUSTOM_OPERAND_LIST.clear();
            OPERAND_LIST.clear();
            OPERAND_LIST.addAll(DEFAULT_OPERAND_LIST);
        }

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
         * Used to check if a specific, partitioned string/sequence of characters IS an
         * operator/key
         * 
         * @param s
         * @return
         */
        public boolean isOperator(String s) {
            return OPERATOR_MAPS.containsKey(s) || OPERATOR_MAPS.containsValue(s);
        }

        /**
         * Used to check if a specific, partitioned string/sequence of characters HAS a
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
         * Used to check if a specific, partitioned string/sequence of characters HAS
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

        public boolean isConversionOperator(char c) {
            return Character.toString(c).equals(getOperatorKeyFromValue(Character.toString(c)));
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

