package src.LogicExpressions.PropositionalLogic.Models;

import src.Exceptions.InvalidExpressionException;
import src.Exceptions.InvalidLogicOperatorException;
import src.Exceptions.InvalidOperandException;
import src.LogicExpressions.PropositionalLogic.Logic.Equivalencies;
import src.LogicExpressions.PropositionalLogic.Logic.Proposition;

import java.util.Map;
import java.util.Stack;
public class DeterministicModel extends Model {
    private String modelName;
    private Proposition expression;

    private char[] operands;
    private Map<Character, Character> defaultOperandTruthValues;

    private String predicateModel;
    private boolean predicateEvaluation;
    private char[] totalPredicateCharValues;
    private boolean[] totalPredicateBooleanValues;
    private String equivalencyEvaluation;

    private String symbolRepresentation;
    private Map<Character, String> operandSymbolicRepresentation;

    public DeterministicModel(String modelName, String expression, Map<Character, Character> defaultOperandTruthValues)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null || expression.isEmpty())
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = new Proposition(expression);

        setOperands(defaultOperandTruthValues);
        this.defaultOperandTruthValues = defaultOperandTruthValues;

        setPredicateString(this.defaultOperandTruthValues);
        this.predicateEvaluation = this.expression.evaluateExpression(this.defaultOperandTruthValues);
        setTotalPredicateTruthValues();
        setEquivalencyEvaluation();
    }

    public DeterministicModel(String modelName, Proposition expression, Map<Character, Character> defaultOperandTruthValues)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null)
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = expression;

        setOperands(this.defaultOperandTruthValues);
        this.defaultOperandTruthValues = defaultOperandTruthValues;

        setPredicateString(this.defaultOperandTruthValues);
        this.predicateEvaluation = this.expression.evaluateExpression(this.defaultOperandTruthValues);
        setTotalPredicateTruthValues();
        setEquivalencyEvaluation();
    }

    public DeterministicModel(String modelName, String expression, Map<Character, Character> defaultOperandTruthValues,
            Map<Character, String> operandSymbolicRepresentation)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null || expression.isEmpty())
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = new Proposition(expression);

        setOperands(defaultOperandTruthValues);
        this.defaultOperandTruthValues = defaultOperandTruthValues;

        setPredicateString(this.defaultOperandTruthValues);
        this.predicateEvaluation = this.expression.evaluateExpression(this.defaultOperandTruthValues);
        setTotalPredicateTruthValues();
        setEquivalencyEvaluation();

        this.operandSymbolicRepresentation = operandSymbolicRepresentation;
        setSymbolicString(this.operandSymbolicRepresentation);
    }

    public DeterministicModel(String modelName, Proposition expression, Map<Character, Character> defaultOperandTruthValues,
            Map<Character, String> operandSymbolicRepresentation)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null)
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = expression;

        setOperands(defaultOperandTruthValues);
        this.defaultOperandTruthValues = defaultOperandTruthValues;

        setPredicateString(this.defaultOperandTruthValues);
        this.predicateEvaluation = this.expression.evaluateExpression(this.defaultOperandTruthValues);
        setTotalPredicateTruthValues();
        setEquivalencyEvaluation();

        this.operandSymbolicRepresentation = operandSymbolicRepresentation;
        setSymbolicString(this.operandSymbolicRepresentation);
    }

    private void setOperands(Map<Character, Character> defaultOperandTruthValues) throws InvalidOperandException {
        operands = new char[this.expression.getOperandCount()];
        for (int i = 0; i < this.expression.getOperandCount(); i++) {
            String operand = this.expression.getProposition(i);
            operands[i] = operand.charAt(0);
        }
        // check if map contains all and only all expression operands
        for (int i = 0; i < operands.length; i++) {
            if (!defaultOperandTruthValues.containsKey(operands[i]))
                throw new InvalidOperandException(operands[i] + " not in expression.");
        }
    }

    private void setPredicateString(Map<Character, Character> defaultOperandTruthValues) {
        this.predicateModel = this.expression.getExpression();
        for (char operand : this.operands)
            this.predicateModel = this.predicateModel.replace(operand, defaultOperandTruthValues.get(operand));
    }

    private void setTotalPredicateTruthValues() {
        this.totalPredicateCharValues = new char[this.defaultOperandTruthValues.size() + 1];
        this.totalPredicateBooleanValues = new boolean[this.defaultOperandTruthValues.size() + 1];
        for (int i = 0; i < this.defaultOperandTruthValues.size(); i++) {
            this.totalPredicateCharValues[i] = this.defaultOperandTruthValues.get(this.operands[i]);
            this.totalPredicateBooleanValues[i] = this.totalPredicateCharValues[i] == 'T' ? true : false;
        }

        this.totalPredicateCharValues[this.defaultOperandTruthValues.size()] = this.predicateEvaluation ? 'T' : 'F';
        this.totalPredicateBooleanValues[this.defaultOperandTruthValues.size()] = this.predicateEvaluation;
    }

    private void setEquivalencyEvaluation() {
        Equivalencies equivalencies = new Equivalencies();

        if (equivalencies.isTautology(this.totalPredicateCharValues))
            this.equivalencyEvaluation = "Tautology";
        else if (equivalencies.isContradiction(this.totalPredicateCharValues))
            this.equivalencyEvaluation = "Contradiction";
        else if (equivalencies.isContingency(this.totalPredicateCharValues))
            this.equivalencyEvaluation = "Contingency";
        else
            this.equivalencyEvaluation = null;
    }

    private void setSymbolicString(Map<Character, String> operandSymbolicRepresentation) {
        this.symbolRepresentation = this.expression.getConvertedExpression();

        Stack<Integer> parenthesesStack = new Stack<>();
        for (int i = 0; i < this.symbolRepresentation.length(); i++) {
            if (Character.isDigit(this.symbolRepresentation.charAt(i))
                    && !parenthesesStack.contains(this.symbolRepresentation.charAt(i) - '0')) {
                parenthesesStack.push(this.symbolRepresentation.charAt(i) - '0');
                this.symbolRepresentation = this.symbolRepresentation.replaceFirst(this.symbolRepresentation.charAt(i) + "", '(' + "");
            } else if (parenthesesStack.contains(this.symbolRepresentation.charAt(i) - '0')) {
                parenthesesStack.pop();
                this.symbolRepresentation = this.symbolRepresentation.replaceFirst(this.symbolRepresentation.charAt(i) + "", ')' + "");
            }
        }
        parenthesesStack.clear();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.symbolRepresentation.length(); i++) {
            if (this.operandSymbolicRepresentation.containsKey(this.symbolRepresentation.charAt(i))) {
                if (this.symbolRepresentation.charAt(i + 1) == ')') {
                    sb.append("'" + this.operandSymbolicRepresentation.get(this.symbolRepresentation.charAt(i)) + "'");
                } else
                    sb.append("'" + this.operandSymbolicRepresentation.get(this.symbolRepresentation.charAt(i)) + "' ");
            } else {
                switch (this.symbolRepresentation.charAt(i)) {
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

        this.symbolRepresentation = sb.toString();
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelName() {
        return this.modelName;
    }

    public Proposition getProposition() {
        return this.expression;
    }
    
    public String[][] getTruthTable() {
        return this.expression.getTruthTable();
    }

    public String getExpression() {
        return this.expression.getExpression();
    }

    public char[] getOperands() {
        return this.operands;
    }
    
    public char getOperand(int index) {
        if (index < 0 || index >= this.operands.length)
            throw new IllegalArgumentException("Operand index out of bounds");

        return this.operands[index];
    }

    public Map<Character, Character> getOperandTruthValues() {
        return this.defaultOperandTruthValues;
    }

    public Map<Character, String> getOperandSymbolicRepresentation() {
        return this.operandSymbolicRepresentation;
    }

    public char[] getAllPredicateTruthValues() {
        return this.totalPredicateCharValues;
    }

    public boolean[] getAllPredicateBooleanValues() {
        return this.totalPredicateBooleanValues;
    }

    public String getEquivalencyEvaluation() {
        return this.equivalencyEvaluation;
    }

    public String getPredicateModel() {
        return this.predicateModel;
    }

    public String getSymbolicRepresentation() {
        return this.symbolRepresentation;
    }

    public Boolean getPredicateEvaluation() {
        return this.predicateEvaluation;
    }
}
