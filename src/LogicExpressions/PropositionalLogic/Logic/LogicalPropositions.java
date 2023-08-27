package src.LogicExpressions.PropositionalLogic.Logic;

import src.LogicExpressions.PropositionalLogic.Characters.LogicalSyntax;
import src.LogicExpressions.PropositionalLogic.Laws.PropositionLaws;

import java.util.ArrayList;

import src.DataStructures.PartitionedParsingTree;
import src.Exceptions.*;
import src.Interfaces.Equivalencies;

/**
 * 
 */
public class LogicalPropositions extends LogicalSyntax implements Equivalencies {
    /** Logical expression, its string and other functions */
    private LogicalExpression expression;
    /** collection of partitioned/parsed propositional statements */
    private PartitionedParsingTree<String> partitions;
    /** count of partitions from expression */
    private int partitionCount;
    /** operands contained in expression's String */
    private ArrayList<String> operands;
    /** count of operands in expression */
    private int operandCount;
    /** combination of operands and partition propositional statements */
    private ArrayList<String> propositions;

    private String[][] truthTable;
    /** applicable laws evaluator */ 
    private PropositionLaws laws;

    public LogicalPropositions()
            throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
        this.expression = new LogicalExpression();
        setPropositions();
        setTruthTable();
        this.laws = null;
    }

    public LogicalPropositions(String e)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        this.expression = new LogicalExpression(e);
        setPropositions();
        setTruthTable();
        this.laws = null;
    }

    private void parseOperands() {
        operands = new ArrayList<String>();
        for (Character c : this.expression.getConvertedExpression().toCharArray()) {
            if (isOperand(c)) {
                if (!operands.contains(c.toString())) {
                    operands.add(c.toString());
                    operandCount++;
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
        this.partitions = new PartitionedParsingTree<String>(this.expression.getConvertedExpression());
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

        //parsePartitions();
        // for (int i = 0; i < this.partitions.size(); i++) {
        //     propositions.add(this.partitions.get(i));
        // }

        // for (int i = 0; i < this.partitions.size(); i++) {
        //     propositions.add(this.partitions.get(i));
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
        else 
            if (!(from == to))
                return propositions.get(from) + " , " + getPropositions(from + 1, to);
            else
                return propositions.get(from);
    }

    private static void permuteOperandValues(char[] chars, int n, String prefix) {
        if (n == 0) {
            return;
        }
        for (int i = 0; i < chars.length; i++) {
            permuteOperandValues(chars, n - 1, prefix + chars[i]);
        }
    }

    private void setTruthTable() {
        truthTable = new String[(int) (Math.pow(2, operandCount)) + 1][propositions.size()];
        Boolean[][] values = new Boolean[(int) Math.pow(2, operandCount)][propositions.size()];
        for (int i = 0; i < propositions.size(); i++) {
            truthTable[0][i] = propositions.get(i) + "\s\s"; // titles each column with corresponding proposition/compound proposition
        }

        for(int i = 0; i < values.length; i++) {
            for(int j = 0; j < operandCount; j++) {

                }
            }
        }

        

    }

    public String[][] getTruthTable() {
        return this.truthTable;
    }

    public String[][] getTruthTable(int from, int to) {

    }

    public String[][] getTruthTable(int from, int to, int from2, int to2) {

    }

    public String[][] getTruthTable(int from, int to, int from2, int to2, int from3, int to3) {

    }

    public void printTruthTable() {
        for (int i = 0; i < truthTable.length; i++) {
            for (int j = 0; j < truthTable[i].length; j++) {
                System.out.print(truthTable[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void printTruthTable(int from, int to) {

    }

    public String inverse(String p) {
        
    }

    public String converse(String p) {
    
    }

    public String contrapositive(String p) {
    
    }

    public String proveProposition() {
        return "";
    }

    @Override
    public boolean isTautology() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isTautology'");
    }

    @Override
    public boolean isContradiction() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isContradiction'");
    }

    @Override
    public boolean isContingency() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isContingency'");
    }

    public static void main(String[] args) throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
        LogicalPropositions e = new LogicalPropositions("(P&Q| ~F )->Z<>(Y&~R<>P>-<Q)");
        System.out.println(e.getExpression());
        System.out.println(e.getConvertedExpression());
        System.out.println(e.getPropositions());
        System.out.println(e.getPropositions(0,3));
        System.out.println(e.getPropositions(3, 5));
        System.out.println(e.getPropositions(1,1));
        e.printTruthTable();

    }

    /**
     * 
     */
    private class LogicalExpression {

        /** logical expression String representing math equation */
        private String expression;
        /** converted logical expression string for easier back-end operations */
        private String convertedExpression;
        /** Maximum number of characters accepted in converted expression String */
        private final int MAX_CHARACTERS = 64;

        /**
         * @throws InvalidExpressionException
         * @throws InvalidLogicOperatorException
         * @throws InvalidOperandException
         * 
         */
        public LogicalExpression()
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
        public LogicalExpression(String e)
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
                throw new InvalidExpressionException("Expression is too long; only 64 converted characters allowed.");
            else if (!containsAnyOperands(cE))
                throw new InvalidOperandException("Expression does not have at least one valid operand.");
            else if (containsAnyConversionOperators(cE)) {
                while (i < getInvalidOrderSize()) {
                    if (cE.contains(getInvalidOrderSet(i)))
                        throw new InvalidLogicOperatorException("Invalid operator syntax in expression.");
                    i++;
                }
                i = 0;
            }
        }

        private String convertExpression(String e) {
            String cE = e; // converted String variable
            cE = cE.replaceAll("<>", getConversionValueFromOperatorKey("<>"));
            cE = cE.replaceAll("->", getConversionValueFromOperatorKey("->"));
            cE = cE.replaceAll(">-<", getConversionValueFromOperatorKey(">-<"));
            cE = cE.replaceAll("<-", getConversionValueFromOperatorKey("<-"));
            cE = cE.replaceAll("\s", getConversionValueFromOperatorKey("\s"));
            return cE;
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
}
