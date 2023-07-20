package src.LogicExpressions.PropositionalLogic;

import src.LogicExpressions.PropositionalLogic.Laws.PropositionLaws;
import src.LogicExpressions.PropositionalLogic.Operators.LogicalOperators;
import java.util.Queue;
import java.util.LinkedList;
import src.Exceptions.*;

/**
 * 
 */
public class Proposition extends LogicalOperators {

    private Expression expression;
    private PropositionLaws laws;

    private static final boolean TRUE = true;
    private static final boolean FALSE = false;

    public Proposition() {
        this.expression = new Expression();
        this.laws = null;
    }

    public Proposition(String e)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        this.expression = new Expression(e);
    }

    public String equivalences() {

    }

    public String identifyLaw() {
        String e = expression.getExpression();

    }

    public String evaluate() {
        boolean result = TRUE;

        return this.expression + " : " + result;
    }

    public String truthTable() {
        boolean[][] table;

    }

    /**
     * 
     */
    private static class Expression {
        // TODO: Implement the logic for parsing expressions and statements

        /** queue for storing/processing partitions of logical expression */
        private Queue<Character> expressionBuffer;
        /** logical expression String representing math equation */
        private String expression;
        /** collection of partitioned propositional statements */
        private LinkedList<String> propositions;

        /**
         * @throws InvalidExpressionException
         * @throws InvalidLogicOperatorException
         * @throws InvalidOperandException
         * 
         */
        public Expression() throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
            loadExpression("(~P&Q)");
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
         * Checks if contents of expression argument is valid.
         * <ul>
         * <li>First, checks if any valid variables and have been passed</li>
         * <li>Second, checks if
         * 
         * @param e String representative of propositional logic expression
         * @throws InvalidOperandException       if does not have alphabetic
         *                                       elements/variables
         * @throws InvalidLogicOperatorException if any operators in expression has
         *                                       syntax errors
         * @throws InvalidExpressionException    if has non-alphabetic OR non-operator
         *                                       elements
         */
        private void checkExpressionForExceptions(String e)
                throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {

            if (!hasVariable(e)) {
                throw new InvalidOperandException("Expression does not have at least one variable");
            }

            /** temp implementation, bound to be changed and/or encapsulated */
            boolean invalidOperatorOrder = e.contains("~~") || e.contains("<)") || e.contains("(<") ||
                    e.contains(">)") || e.contains("(>") || e.contains("()") ||
                    e.contains("~)") || e.contains("~&") || e.contains("&&") ||
                    e.contains("||") || e.contains("~|") || e.contains("(&") ||
                    e.contains("&)") || e.contains("(|") || e.contains("|)");

            if (hasOperator(e)) {
                if (invalidOperatorOrder)
                    throw new InvalidLogicOperatorException("Invalid operator syntax in expression.");
            }

            char[] c = e.toCharArray();
            for (int i = 0; i < c.length;) {
                if ((isOperator(c[i]) || isVariable(c[i]))) {
                    i++;
                } else
                    throw new InvalidExpressionException("Invalid character(s) passed in expression.");
            }
        }

        /**
         * @param e propositional logic String
         * @throws InvalidExpressionException
         * @throws InvalidLogicOperatorExcept throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionExceptionion
         * @throws InvalidOperandException
         */
        private void loadExpression(String e) throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
            checkExpressionForExceptions(e); // checks validity of expression argument
            this.expressionBuffer = null;
            this.expression = e;
            parseStatements(e); // sets this.proposition
        }

        /**
         * @param e
         */
        private boolean checkSyntax(String e) {

            return result;
        }

        /**
         * 
         * @param e
         * @return
         */
        private void parseStatements(String e) {
            // char[] expressionChar = e.toCharArray();

            /**
             * must break each statement into partitions/separated and grouped statements.
             * 1. Split String in two.
             *  1.2. Check if '~' or '(' is at the left end of first string half.
             *   1.2.1. If this is the case, pop top elements 
             *   1.2.2. and append the element(s) to the right end of second half.
             * 2. Convert each partition 
             *  2.1. 
             *  2.2. 
             *  2.3. 
             * 3. Check for which letters are in each String. 
             * 4. 
             * 
             */

             

            // String newStatement;
            // int count = 0;
            // for (int i = e.length(); i >= 0; i--) {

            // }
            // sortStatements();
        }

        private void sortStatements() {
            // TODO: sort single variable partitions at front of list
            // TODO: sort larger partitions after single variables in list
            // TODO: append whole expression at the end of list
        }

        private boolean hasOperator(String e) {
            return (e.contains(">>") || e.contains("<<") || e.contains("<>") ||
                    e.contains("><") || e.contains("&") || e.contains("|") ||
                    e.contains("&") || e.contains("|") || e.contains("(") ||
                    e.contains(")"));
        }

        private boolean hasVariable(String e) {
            return ((e.contains("[a-zA-Z]+")));
        }

        private boolean isOperator(char c) {
            return ((c == '&') || (c == '|') || (c == '~') || (c == '<') || (c == '>') ||
                    (c == ')') || (c == '('));
        }

        private boolean isVariable(char c) {
            return ((c >= 'A' && c <= 'Z') || (c >= 'a' && c >= 'z'));
        }

        public void setExpression(String e)
                throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
            checkExpressionForExceptions(e);
            loadExpression(e);
        }

        public String getExpression() {
            return this.expression;
        }

        public String getStatements() {
            String statements = null;
            for (int i = 0; i < propositions.size(); i++)
                statements = i + ". " + propositions.get(i) + ", ";

            return statements;
        }

        public String getStatementAtIndex(int index) {

        }

        public String getValidOperators() {
            return "&, |, ~, >>, <<, ><, <>";
        }

        public String getInvalidOperators() {
            return "+, -, *, /, %, &&, ||";
        }
    }
}
