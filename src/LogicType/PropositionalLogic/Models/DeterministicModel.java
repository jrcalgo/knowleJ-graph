package src.LogicType.PropositionalLogic.Models;

import src.Exceptions.InvalidExpressionException;
import src.Exceptions.InvalidLogicOperatorException;
import src.Exceptions.InvalidOperandException;
import src.LogicType.PropositionalLogic.Logic.Proposition;
import src.LogicType.PropositionalLogic.Logic.Validity;

import java.util.Map;
import java.util.Stack;

public class DeterministicModel extends Model {
    private String modelName;
    private Proposition expression;

    private char[] operands;
    private Map<Character, Character> defaultOperandCharValues;

    private String predicateBooleanModel;
    private boolean predicateEvaluation;
    private char[] allPredicateCharValues;
    private boolean[] allPredicateBooleanValues;
    private String validityEvaluation;

    private String symbolRepresentation;
    private Map<Character, String> operandSymbolicRepresentation;

    public DeterministicModel(String modelName, String expression) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null || expression.isEmpty())
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = new Proposition(expression);
        setOperands(null);
    }

    public DeterministicModel(String modelName, Proposition expression) throws InvalidOperandException {
        if (expression == null)
            throw new IllegalArgumentException("Expression cannot be null or empty.");
            
        this.modelName = modelName;
        this.expression = expression;
        setOperands(null);
    }

    public DeterministicModel(String modelName, String expression, Map<Character, Character> defaultOperandCharValues)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null || expression.isEmpty())
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = new Proposition(expression);

        setOperands(defaultOperandCharValues);
        this.defaultOperandCharValues = defaultOperandCharValues;

        setPredicateBooleanString(this.defaultOperandCharValues);
        this.predicateEvaluation = this.expression.evaluateExpression(this.defaultOperandCharValues);
        setAllPredicateTruthValues();
        setValidityEvaluation();
    }

    public DeterministicModel(String modelName, Proposition expression, Map<Character, Character> defaultOperandCharValues)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null)
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = expression;

        setOperands(this.defaultOperandCharValues);
        this.defaultOperandCharValues = defaultOperandCharValues;

        setPredicateBooleanString(this.defaultOperandCharValues);
        this.predicateEvaluation = this.expression.evaluateExpression(this.defaultOperandCharValues);
        setAllPredicateTruthValues();
        setValidityEvaluation();
    }

    public DeterministicModel(String modelName, String expression, Map<Character, String> operandSymbolicRepresentation,
            Map<Character, Character> defaultOperandCharValues)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null || expression.isEmpty())
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = new Proposition(expression);

        setOperands(defaultOperandCharValues);
        this.defaultOperandCharValues = defaultOperandCharValues;

        setPredicateBooleanString(this.defaultOperandCharValues);
        this.predicateEvaluation = this.expression.evaluateExpression(this.defaultOperandCharValues);
        setAllPredicateTruthValues();
        setValidityEvaluation();

        this.operandSymbolicRepresentation = operandSymbolicRepresentation;
        setSymbolicString(this.operandSymbolicRepresentation);
    }

    public DeterministicModel(String modelName, Proposition expression, Map<Character, String> operandSymbolicRepresentation,
            Map<Character, Character> defaultOperandCharValues)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null)
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = expression;

        setOperands(defaultOperandCharValues);
        this.defaultOperandCharValues = defaultOperandCharValues;

        setPredicateBooleanString(this.defaultOperandCharValues);
        this.predicateEvaluation = this.expression.evaluateExpression(this.defaultOperandCharValues);
        setAllPredicateTruthValues();
        setValidityEvaluation();

        this.operandSymbolicRepresentation = operandSymbolicRepresentation;
        setSymbolicString(this.operandSymbolicRepresentation);
    }

    private void setOperands(Map<Character, Character> defaultOperandCharValues) throws InvalidOperandException {
        operands = new char[this.expression.getOperandCount()];
        for (int i = 0; i < this.expression.getOperandCount(); i++) {
            String operand = this.expression.getSentence(i);
            this.operands[i] = operand.charAt(0);
        }
        // check if map contains all and only all expression operands
        if (defaultOperandCharValues != null) {
            for (int i = 0; i < operands.length; i++) {
                if (!defaultOperandCharValues.containsKey(operands[i]))
                    throw new InvalidOperandException(operands[i] + " not in expression.");
            }
        }
    }

    private void setPredicateBooleanString(Map<Character, Character> defaultOperandCharValues) {
        this.predicateBooleanModel = this.expression.getExpression();
        for (char operand : this.operands)
            this.predicateBooleanModel = this.predicateBooleanModel.replace(operand, defaultOperandCharValues.get(operand));
    }

    private void setAllPredicateTruthValues() {
        this.allPredicateCharValues = new char[this.defaultOperandCharValues.size() + 1];
        this.allPredicateBooleanValues = new boolean[this.defaultOperandCharValues.size() + 1];
        for (int i = 0; i < this.defaultOperandCharValues.size(); i++) {
            if (i < this.operands.length && i < this.allPredicateCharValues.length) {
                this.allPredicateCharValues[i] = this.defaultOperandCharValues.get(this.operands[i]);
                this.allPredicateBooleanValues[i] = this.allPredicateCharValues[i] == 'T' ? true : false;
            }
        }

        this.allPredicateCharValues[this.defaultOperandCharValues.size()] = this.predicateEvaluation ? 'T' : 'F';
        this.allPredicateBooleanValues[this.defaultOperandCharValues.size()] = this.predicateEvaluation;
    }

    private void setValidityEvaluation() {
        Validity validity = new Validity();

        if (validity.isTautology(this.allPredicateBooleanValues))
            this.validityEvaluation = "Tautology";
        else if (validity.isContradiction(this.allPredicateBooleanValues))
            this.validityEvaluation = "Contradiction";
        else if (validity.isContingency(this.allPredicateBooleanValues))
            this.validityEvaluation = "Contingency";
        else
            this.validityEvaluation = null;
    }

    private void setSymbolicString(Map<Character, String> operandSymbolicRepresentation) {
        this.symbolRepresentation = this.expression.getConvertedExpression();

        Stack<Integer> parenthesesStack = new Stack<>();
        for (int i = 0; i < this.symbolRepresentation.length(); i++) {
            if (Character.isDigit(this.symbolRepresentation.charAt(i))
                    && !parenthesesStack.contains(this.symbolRepresentation.charAt(i) - '0')) {
                parenthesesStack.push(this.symbolRepresentation.charAt(i) - '0');
                this.symbolRepresentation = this.symbolRepresentation
                        .replaceFirst(this.symbolRepresentation.charAt(i) + "", '(' + "");
            } else if (parenthesesStack.contains(this.symbolRepresentation.charAt(i) - '0')) {
                parenthesesStack.pop();
                this.symbolRepresentation = this.symbolRepresentation
                        .replaceFirst(this.symbolRepresentation.charAt(i) + "", ')' + "");
            }
        }
        parenthesesStack.clear();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.symbolRepresentation.length(); i++) {
            if (this.operandSymbolicRepresentation.containsKey(this.symbolRepresentation.charAt(i))) {
                if ((i+1 < this.symbolRepresentation.length()) && this.symbolRepresentation.charAt(i + 1) == ')') {
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

    public String[][] getTruthTable() throws InvalidExpressionException {
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
        return this.defaultOperandCharValues;
    }

    public Map<Character, String> getOperandSymbolicRepresentation() {
        return this.operandSymbolicRepresentation;
    }

    public char[] getAllPredicateTruthValues() {
        return this.allPredicateCharValues;
    }

    public boolean[] getAllPredicateBooleanValues() {
        return this.allPredicateBooleanValues;
    }

    public String getValidityEvaluation() {
        return this.validityEvaluation;
    }

    public String getpredicateBooleanModel() {
        return this.predicateBooleanModel;
    }

    public String getSymbolicRepresentation() {
        return this.symbolRepresentation;
    }

    public Boolean getPredicateEvaluation() {
        return this.predicateEvaluation;
    }

    @Override
    public char[] getAllPredicateCharValues() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllPredicateCharValues'");
    }
}
