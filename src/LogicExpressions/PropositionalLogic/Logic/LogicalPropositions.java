package src.LogicExpressions.PropositionalLogic.Logic;

import src.LogicExpressions.PropositionalLogic.Characters.LogicalCharacters;
import src.LogicExpressions.PropositionalLogic.Laws.PropositionLaws;

import java.util.Stack;
import java.util.Queue;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import src.Exceptions.*;

/**
 * 
 */
public class LogicalPropositions {

    private LogicalExpression expression;
    private PropositionLaws laws;
    /** collection of partitioned/parsed propositional statements */
    private ArrayList<String> propositions;

    private static final boolean TRUE = true;
    private static final boolean FALSE = false;

    public LogicalPropositions() throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
        this.expression = new LogicalExpression();
        this.laws = null;
    }

    public LogicalPropositions(String e)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        this.expression = new LogicalExpression(e);
    }

    /**
     * Utilizes a tree data structure to parse the expression into its constituent
     * propositions
     * 
     * @param e
     * @return
     */
    private void parsePropositions(String e, int front, int end) {

        // TODO: partition expression into its constituent propositions, such as "p" or
        // "Q" or "p&Q" or "(p&Q)" etc.
        // create a b-tree data structure to first store the expression field as the
        // root, and th

    }

    /**
     * Helper method for parsePropositions() method, balances tree expression
     * partitions
     */
    private void sortPropositions() {
        // TODO: sort single variable partitions at front of list
        // TODO: sort larger partitions after single variables in list
        // TODO: append whole expression at the end of list
    }

    public String getExpression() {
        return this.expression.getExpression();
    }

    public void setExpression(String e)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        this.expression.setExpression(e);
    }

    public String getStatements() {
        String statements = null;
        for (int i = 0; i < propositions.size(); i++)
            statements += (i + ". " + propositions.get(i) + " ");

        return statements;
    }

    public String getStatements(int from, int to) {
        String result = null;

        if (from > to)
            return result;
        else
            result += (from + ". " + propositions.get(from) + " ");

        return getStatements(from + 1, to);
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
    private class LogicalExpression extends LogicalCharacters {
        // TODO: Implement the logic for parsing expressions and statements

        private final int MAX_CHARACTERS = 64;

        private final static LinkedList<String> VALID_OPERATORS = new LinkedList<String>() {
            {
                add("&");
                add("|");
                add("->");
                add("<->");
                add("~");
                add("><");
                add("<-");
                add("(");
                add(")");
                add(" ");
                add("T");
                add("F");
            }
        };

        private final static LinkedList<String> INVALID_OPERATOR_ORDER = new LinkedList<String>() {
            {
                add("~~");
                add("<)");
                add("(<");
                add(">)");
                add("(>");
                add("()");
                add("~)");
                add("~&");
                add("&&");
                add("||");
                add("~|");
                add("(&");
                add("&)");
                add("(|");
                add("|)");
            }
        };

        /** queue for storing/processing partitions of logical expression */
        private Queue<Character> expressionBuffer;
        /** logical expression String representing math equation */
        private String expression;

        /**
         * @throws InvalidExpressionException
         * @throws InvalidLogicOperatorException
         * @throws InvalidOperandException
         * 
         */
        public LogicalExpression()
                throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
            loadExpression("(~P&Q)|~R"); // example expression
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
        private void checkSyntax(String e)
                throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
            int i = 0;
            if (e.length() > MAX_CHARACTERS)
                throw new InvalidExpressionException("Expression is too long; only 64 characters allowed.");
            else if (!containsAnyOperands(e))
                throw new InvalidOperandException("Expression does not have at least one valid operand.");
            else if (containsAnyOperators(e)) {
                while (i < INVALID_OPERATOR_ORDER.size()) {
                    if (e.contains(INVALID_OPERATOR_ORDER.get(i)))
                        throw new InvalidLogicOperatorException("Invalid operator syntax in expression.");
                    i++;
                }
                i = 0;
            }
            for ( ; i < e.length(); i++) {
                if (!e.contains((CharSequence) OPERATOR_MAPS.get(i)))
                    throw new InvalidLogicOperatorException("Invalid operator syntax in expression.");
            }


        }

        /**
         * @param e propositional logic String
         * @throws InvalidExpressionException
         * @throws InvalidLogicOperatorExcept
         * @throws InvalidOperandException
         */
        private void loadExpression(String e)
                throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
            checkSyntax(e); // checks validity of expression argument
            this.expressionBuffer = null;
            this.expression = e;
        }

        public void setExpression(String e)
                throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
            loadExpression(e);
        }

        public String getExpression() {
            return this.expression;
        }
    }
}
