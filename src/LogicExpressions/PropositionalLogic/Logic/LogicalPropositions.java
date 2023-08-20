package src.LogicExpressions.PropositionalLogic.Logic;

import src.LogicExpressions.PropositionalLogic.Characters.LogicalCharacters;
import src.LogicExpressions.PropositionalLogic.Laws.PropositionLaws;

import java.util.Stack;

import javax.naming.PartialResultException;

import java.util.Queue;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.LinkedList;

import src.DataStructures.PartitionedParsingTree;
import src.Exceptions.*;

/**
 * 
 */
public class LogicalPropositions {

    private LogicalExpression expression;
    private PropositionLaws laws;
    /** collection of partitioned/parsed propositional statements */
    private PartitionedParsingTree propositions;

    private static final boolean TRUE = true;
    private static final boolean FALSE = false;

    public LogicalPropositions()
            throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
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
    private void parsePropositions(String e) {
        this.propositions = new ParsingTreeArrayList.ParsingTree(e);

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

    public String getConvertedExpression() {
        return this.expression.getConvertedExpression();
    }

    public void setExpression(String e)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        this.expression.setExpression(e);
    }

    public String getAllPropositions() {
        String statements = null;
        for (int i = 0; i < propositions.size(); i++)
            statements += (i + ". " + propositions.get(i) + " ");

        return statements;
    }

    public String getPropositions(int from, int to) {
        String result = null;

        if (from > to)
            return result;
        else
            result += (from + ". " + propositions.get(from) + " ");

        return getPropositions(from + 1, to);
    }

    public String equivalences() {

    }

    public static void main(String[] args) throws InvalidOperandException, InvalidLogicOperatorException, InvalidExpressionException {
        LogicalPropositions e = new LogicalPropositions("(P&Q| ~R )->Z<>(Z&~R<>P>-<Q)");
        System.out.println(e.getExpression());
        System.out.println(e.getConvertedExpression());

    }

    /**
     * 
     */
    private class LogicalExpression extends LogicalCharacters {

        /** logical expression String representing math equation */
        private String expression;
        /** converted logical expression string representing math equation */
        private String convertedExpression;
        /** Maximum number of characters accepted in converted expression String */
        private final int MAX_CHARACTERS = 32;

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
                throw new InvalidExpressionException("Expression is too long; only 32 converted characters allowed.");
            else if (!containsAnyOperands(cE))
                throw new InvalidOperandException("Expression does not have at least one valid operand.");
            else if (containsAnyOperators(cE)) {
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
