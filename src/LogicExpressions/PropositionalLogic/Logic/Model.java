package src.LogicExpressions.PropositionalLogic.Logic;

import src.Exceptions.InvalidExpressionException;
import src.Exceptions.InvalidLogicOperatorException;
import src.Exceptions.InvalidOperandException;
import src.LogicExpressions.PropositionalLogic.Characters.Quantifiers;


import java.util.Map;
import java.util.Stack;
public class Model extends Quantifiers {
    private String modelName;
    public Proposition expression;

    private char[] operands;
    private Map<Character, Character> operandTruthValues;

    private String predicateModel;
    private boolean predicateEvaluation;
    private char[] totalPredicateCharValues;
    private boolean[] totalPredicateBooleanValues;
    private String equivalencyEvaluation;

    private String symbolicModel;
    private Map<Character, String> operandSymbolicRepresentation;

    /**
     * 
     * @param expression must obey Proposition object format rules
     * @param operands
     * @throws InvalidLogicOperatorException
     * @throws InvalidOperandException
     * @throws InvalidExpressionException
     */
    public Model(String modelName, String expression)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null || expression.isEmpty())
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = new Proposition(expression);
    }

    public Model(String modelName, Proposition expression)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null)
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = expression;
    }

    public Model(String modelName, String expression, Map<Character, Character> operandTruthValues)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null || expression.isEmpty())
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = new Proposition(expression);

        setOperands(operandTruthValues);
        this.operandTruthValues = operandTruthValues;

        setPredicateString(this.operandTruthValues);
        this.predicateEvaluation = this.expression.evaluateExpression(this.operandTruthValues);
        setTotalPredicateTruthValues();
        setEquivalencyEvaluation();
    }

    public Model(String modelName, Proposition expression, Map<Character, Character> operandTruthValues)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null)
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = expression;

        setOperands(this.operandTruthValues);
        this.operandTruthValues = operandTruthValues;

        setPredicateString(this.operandTruthValues);
        this.predicateEvaluation = this.expression.evaluateExpression(this.operandTruthValues);
        setTotalPredicateTruthValues();
        setEquivalencyEvaluation();
    }

    public Model(String modelName, String expression, Map<Character, Character> operandTruthValues,
            Map<Character, String> operandSymbolicRepresentation)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null || expression.isEmpty())
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = new Proposition(expression);

        setOperands(operandTruthValues);
        this.operandTruthValues = operandTruthValues;

        setPredicateString(this.operandTruthValues);
        this.predicateEvaluation = this.expression.evaluateExpression(this.operandTruthValues);
        setTotalPredicateTruthValues();
        setEquivalencyEvaluation();

        this.operandSymbolicRepresentation = operandSymbolicRepresentation;
        setSymbolicString(this.operandSymbolicRepresentation);
    }

    public Model(String modelName, Proposition expression, Map<Character, Character> operandTruthValues,
            Map<Character, String> operandSymbolicRepresentation)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null)
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = expression;

        setOperands(operandTruthValues);
        this.operandTruthValues = operandTruthValues;

        setPredicateString(this.operandTruthValues);
        this.predicateEvaluation = this.expression.evaluateExpression(this.operandTruthValues);
        setTotalPredicateTruthValues();
        setEquivalencyEvaluation();

        this.operandSymbolicRepresentation = operandSymbolicRepresentation;
        setSymbolicString(this.operandSymbolicRepresentation);
    }

    private void setOperands(Map<Character, Character> operandTruthValues) throws InvalidOperandException {
        operands = new char[this.expression.getOperandCount()];
        for (int i = 0; i < this.expression.getOperandCount(); i++) {
            String operand = this.expression.getProposition(i);
            operands[i] = operand.charAt(0);
        }
        // check if map contains all and only all expression operands
        for (int i = 0; i < operands.length; i++) {
            if (!operandTruthValues.containsKey(operands[i]))
                throw new InvalidOperandException(operands[i] + " not in expression.");
        }
    }

    private void setPredicateString(Map<Character, Character> operandTruthValues) {
        this.predicateModel = this.expression.getExpression();
        for (char operand : this.operands)
            this.predicateModel = this.predicateModel.replace(operand, operandTruthValues.get(operand));
    }

    private void setTotalPredicateTruthValues() {
        this.totalPredicateCharValues = new char[this.operandTruthValues.size() + 1];
        for (int i = 0; i < this.operandTruthValues.size() - 1; i++) {
            this.totalPredicateCharValues[i] = this.operandTruthValues.get(this.operands[i]);
            this.totalPredicateBooleanValues[i] = this.totalPredicateCharValues[i] == 'T' ? true : false;
        }

        this.totalPredicateCharValues[this.operandTruthValues.size()] = this.predicateEvaluation ? 'T' : 'F';
        this.totalPredicateBooleanValues[this.operandTruthValues.size()] = this.predicateEvaluation;
    }

    private void setEquivalencyEvaluation() {
        Equivalencies equivalencies = new Equivalencies();

        if (equivalencies.isTautology(this.totalPredicateCharValues))
            this.equivalencyEvaluation = "Tautology";
        else if (equivalencies.isContradiction(this.totalPredicateCharValues))
            this.equivalencyEvaluation = "Contradiction";
        else if (equivalencies.isContingency(this.totalPredicateCharValues))
            this.equivalencyEvaluation = "Contingency";
    }

    private void setSymbolicString(Map<Character, String> operandSymbolicRepresentation) {
        this.symbolicModel = this.expression.getConvertedExpression();
        StringBuilder sb = new StringBuilder();

        Stack<Integer> parenthesesStack = new Stack<>();
        for (int i = 0; i < this.symbolicModel.length(); i++) {
            if (Character.isDigit(this.symbolicModel.charAt(i))
                    && !parenthesesStack.contains(this.symbolicModel.charAt(i) - '0')) {
                parenthesesStack.push(this.symbolicModel.charAt(i) - '0');
                this.symbolicModel = this.symbolicModel.replaceFirst(this.symbolicModel.charAt(i) + "", '(' + "");
            } else if (parenthesesStack.contains(this.symbolicModel.charAt(i) - '0')) {
                parenthesesStack.pop();
                this.symbolicModel = this.symbolicModel.replaceFirst(this.symbolicModel.charAt(i) + "", ')' + "");
            }
        }
        parenthesesStack.clear();

        for (int i = 0; i < this.symbolicModel.length(); i++) {
            if (this.operandSymbolicRepresentation.containsKey(this.symbolicModel.charAt(i))) {
                if (this.symbolicModel.charAt(i + 1) == ')') {
                    sb.append("'" + this.operandSymbolicRepresentation.get(this.symbolicModel.charAt(i)) + "'");
                } else
                    sb.append("'" + this.operandSymbolicRepresentation.get(this.symbolicModel.charAt(i)) + "' ");
            } else {
                switch (this.symbolicModel.charAt(i)) {
                    case '(':
                        sb.append("(");
                        break;
                    case ')':
                        sb.append(") ");
                        break;
                    case 'n':
                        sb.append("not ");
                        break;
                    case 'a':
                        sb.append("and ");
                        break;
                    case 'o':
                        sb.append("or ");
                        break;
                    case 'x':
                        sb.append("xor ");
                        break;
                    case 'm':
                        sb.append("implies ");
                        break;
                    case 'i':
                        sb.append("iff ");
                        break;
                    default:
                        break;
                }
            }
        }

        this.symbolicModel = sb.toString();
    }

    public String getModelName() {
        return this.modelName;
    }

    public Proposition getProposition() {
        return this.expression;
    }

    public String getExpression() {
        return this.expression.getExpression();
    }

    public char[] getOperands() {
        return this.operands;
    }

    public Map<Character, Character> getOperandTruthValues() {
        return this.operandTruthValues;
    }

    public Map<Character, String> getOperandSymbolicRepresentation() {
        return this.operandSymbolicRepresentation;
    }

    public char[] getTotalPredicateTruthValues() {
        return this.totalPredicateCharValues;
    }

    public boolean[] getTotalPredicateBooleanValues() {
        return this.totalPredicateBooleanValues;
    }

    public String getEquivalencyEvaluation() {
        return this.equivalencyEvaluation;
    }

    public String getPredicateModel() {
        return this.predicateModel;
    }

    public String getSymbolicModel() {
        return this.symbolicModel;
    }

    public boolean getPredicateEvaluation() {
        return this.predicateEvaluation;
    }

    // public boolean universal(List<T> elements, Model predicate) {
    // for (T element : elements) {
    // if (!predicate.test(element))
    // return FALSE;
    // }
    // return TRUE;
    // }

    // public boolean universal(PartitionedParsingTree<T> elements, Model predicate)
    // {
    // for (T element : elements) {
    // if (!predicate.test(element))
    // return FALSE;
    // }
    // return TRUE;
    // }

    // public boolean existential(List<T> elements, Model<T> predicate) {
    // for (T element : elements) {
    // if (predicate.test(element))
    // return TRUE;
    // }
    // return FALSE;
    // }

}
