package ai.knowlej.PropositionalLogic.Models;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import ai.knowlej.Exceptions.InvalidExpressionException;
import ai.knowlej.Exceptions.InvalidLogicOperatorException;
import ai.knowlej.Exceptions.InvalidOperandException;
import ai.knowlej.PropositionalLogic.Logic.Proposition;
import ai.knowlej.PropositionalLogic.Logic.Validity;

public class StochasticModel extends Model {
    private String modelName;
    private Proposition expression;

    private char[] operands;
    private Map<Character, Double> defaultOperandProbabilityValues;
    private Map<Character, Character> defaultOperandCharValues;
    private Double defaultTruthThreshold;
    private Map<Character, Double> individualOperandTrumvnthThresholds;

    private String predicateProbabilityModel;
    private String predicateBooleanModel;
    private double[] allPredicateProbabilityValues;
    private char[] allPredicateCharValues;
    private boolean[] allPredicateBooleanValues;
    private boolean predicateEvaluation;
    private String validityEvaluation;

    private String symbolRepresentation;
    private Map<Character, String> operandSymbolicRepresentation;

    public StochasticModel(String modelName, String expression) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null || expression.isEmpty()) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = new Proposition(expression);
        setOperands();
    }

    public StochasticModel(String modelName, Proposition expression) {
        if (expression == null) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = expression;
        setOperands();
    }

    public StochasticModel(String modelName, String expression, Map<Character, String> operandSymbolicRepresentation) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null || expression.isEmpty()) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = new Proposition(expression);
        setOperands();

        this.operandSymbolicRepresentation = operandSymbolicRepresentation;
        setSymbolicString(this.operandSymbolicRepresentation);
    }

    public StochasticModel(String modelName, Proposition expression, Map<Character, String> operandSymbolicRepresentation) {
        if (expression == null) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = expression;
        setOperands();

        this.operandSymbolicRepresentation = operandSymbolicRepresentation;
        setSymbolicString(this.operandSymbolicRepresentation);
    }

    public StochasticModel(String modelName, String expression, Map<Character, String> operandSymbolicRepresentation, Double defaultTruthThreshold, Map<Character, Double> defaultOperandProbabilityValues) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null || expression.isEmpty()) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = new Proposition(expression);
        setOperands(defaultTruthThreshold, defaultOperandProbabilityValues, null);

        this.operandSymbolicRepresentation = operandSymbolicRepresentation;
        setSymbolicString(this.operandSymbolicRepresentation);

        this.defaultOperandProbabilityValues = defaultOperandProbabilityValues;
        this.defaultTruthThreshold = defaultTruthThreshold;

        setPredicateProbabilityString(this.defaultOperandProbabilityValues);
        setPredicateBooleanString(this.defaultOperandProbabilityValues, defaultTruthThreshold, null);
        this.predicateEvaluation = this.expression.evaluateExpression(defaultOperandCharValues);
        setAllPredicateTruthValues();
        setValidityEvaluation();
    }

    public StochasticModel(String modelName, Proposition expression, Map<Character, String> operandSymbolicRepresentation, Double defaultTruthThreshold, Map<Character, Double> defaultOperandProbabilityValues) throws InvalidExpressionException {
        if (expression == null) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = expression;
        setOperands(defaultTruthThreshold, defaultOperandProbabilityValues, null);

        this.operandSymbolicRepresentation = operandSymbolicRepresentation;
        setSymbolicString(this.operandSymbolicRepresentation);

        this.defaultOperandProbabilityValues = defaultOperandProbabilityValues;
        this.defaultTruthThreshold = defaultTruthThreshold;

        setPredicateProbabilityString(this.defaultOperandProbabilityValues);
        setPredicateBooleanString(this.defaultOperandProbabilityValues, defaultTruthThreshold, null);
        this.predicateEvaluation = this.expression.evaluateExpression(defaultOperandCharValues);
        setAllPredicateTruthValues(); 
        setValidityEvaluation();
    }
    
    public StochasticModel(String modelName, String expression, Map<Character, String> operandSymbolicRepresentation, Double defaultTruthThreshold, Map<Character, Double> defaultOperandProbabilityValues, Map<Character, Double> individualOperandTruthThresholds) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null || expression.isEmpty()) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = new Proposition(expression);
        setOperands(defaultTruthThreshold, defaultOperandProbabilityValues, individualOperandTruthThresholds);

        this.operandSymbolicRepresentation = operandSymbolicRepresentation;
        setSymbolicString(this.operandSymbolicRepresentation);

        this.defaultOperandProbabilityValues = defaultOperandProbabilityValues;
        this.defaultTruthThreshold = defaultTruthThreshold;

        setPredicateProbabilityString(this.defaultOperandProbabilityValues);
        setPredicateBooleanString(this.defaultOperandProbabilityValues, defaultTruthThreshold, individualOperandTruthThresholds);
        this.predicateEvaluation = this.expression.evaluateExpression(defaultOperandCharValues);
        setAllPredicateTruthValues();
        setValidityEvaluation();
    }

    public StochasticModel(String modelName, Proposition expression, Map<Character, String> operandSymbolicRepresentation, Double defaultTruthThreshold, Map<Character, Double> defaultOperandProbabilityValues, Map<Character, Double> individualOperandTruthThresholds) throws InvalidExpressionException {
        if (expression == null) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = expression;
        setOperands(defaultTruthThreshold, defaultOperandProbabilityValues, individualOperandTruthThresholds);

        this.operandSymbolicRepresentation = operandSymbolicRepresentation;
        setSymbolicString(this.operandSymbolicRepresentation);

        this.defaultOperandProbabilityValues = defaultOperandProbabilityValues;
        this.defaultTruthThreshold = defaultTruthThreshold;

        setPredicateProbabilityString(this.defaultOperandProbabilityValues);
        setPredicateBooleanString(this.defaultOperandProbabilityValues, defaultTruthThreshold, individualOperandTruthThresholds);
        this.predicateEvaluation = this.expression.evaluateExpression(defaultOperandCharValues);
        setAllPredicateTruthValues();
        setValidityEvaluation();
    }

    private void setOperands() {
        this.operands = new char[this.expression.getOperandCount()];
        for (int i = 0; i < this.expression.getOperandCount(); i++) {
            String operand = this.expression.getSentence(i);
            this.operands[i] = operand.charAt(0);
        }
    }

    private void setOperands(Double defaultTruthThreshold, Map<Character, Double> defaultOperandProbabilityValues, Map<Character, Double> individualOperandTruthThresholds) {
        setOperands();
        if (defaultOperandProbabilityValues != null) {
            for (int i = 0; i < operands.length; i++) {
                if (!defaultOperandProbabilityValues.containsKey(operands[i]))
                    throw new IllegalArgumentException(operands[i] + " not in expression.");
            }
        }

        if (individualOperandTruthThresholds == null && defaultTruthThreshold == null)
            throw new IllegalArgumentException("Either defaultTruthThreshold and/or individualOperandTruthThresholds must be specified.");

        if (defaultTruthThreshold != null && (defaultTruthThreshold < 0 || defaultTruthThreshold > 1))
            throw new IllegalArgumentException("Default truth threshold must be >= 0 or <= 1.");

        if (individualOperandTruthThresholds == null) {
            this.defaultOperandCharValues = new HashMap<>();
            for (Character operand : defaultOperandProbabilityValues.keySet()) {
                if (defaultOperandProbabilityValues.get(operand) < 0.0 || defaultOperandProbabilityValues.get(operand) > 1.0)
                    throw new IllegalArgumentException(operand + " probability must be >= 0 or <= 1.");

                this.defaultOperandCharValues.put(operand, (defaultOperandProbabilityValues.get(operand) >= defaultTruthThreshold) ? 'T' : 'F');
            }
        } else {
            this.defaultOperandCharValues = new HashMap<>();
            for (Character operand : defaultOperandProbabilityValues.keySet()) {
                if (defaultOperandProbabilityValues.get(operand) < 0.0 || defaultOperandProbabilityValues.get(operand) > 1.0)
                    throw new IllegalArgumentException(operand + " probability must be >= 0 or <= 1.");
                
                if (individualOperandTruthThresholds.containsKey(operand)) {
                    if (individualOperandTruthThresholds.get(operand) < 0.0 || individualOperandTruthThresholds.get(operand) > 1.0)
                        throw new IllegalArgumentException(operand + "'s truth threshold must be >= 0 or <= 1.");
                }
                
                if (defaultTruthThreshold == null) {
                    if (!individualOperandTruthThresholds.containsKey(operand))
                        throw new IllegalArgumentException(operand + " not in expression.");
    
                    this.defaultOperandCharValues.put(operand, (defaultOperandProbabilityValues.get(operand) >= individualOperandTruthThresholds.get(operand)) ? 'T' : 'F');
                } else {
                    if (individualOperandTruthThresholds.containsKey(operand)) {
                        this.defaultOperandCharValues.put(operand, (defaultOperandProbabilityValues.get(operand) >= individualOperandTruthThresholds.get(operand)) ? 'T' : 'F');
                    } else {
                        this.defaultOperandCharValues.put(operand, (defaultOperandProbabilityValues.get(operand) >= defaultTruthThreshold) ? 'T' : 'F');
                    }
                }
            }
        }
    }

    private void setAllPredicateTruthValues() {
        this.allPredicateProbabilityValues = new double[this.defaultOperandCharValues.size() + 1];
        this.allPredicateCharValues = new char[this.defaultOperandCharValues.size() + 1];
        this.allPredicateBooleanValues = new boolean[this.defaultOperandCharValues.size() + 1];
        for (int i = 0; i < this.operands.length; i++) {
            this.allPredicateProbabilityValues[i] = this.defaultOperandProbabilityValues.get(this.operands[i]);
            this.allPredicateCharValues[i] = this.operands[i];
            this.allPredicateBooleanValues[i] = (this.defaultOperandCharValues.get(this.operands[i]) == 'T') ? true : false;
        }

        this.allPredicateProbabilityValues[this.defaultOperandCharValues.size()] = this.predicateEvaluation ? 1.0 : 0.0;
        this.allPredicateCharValues[this.defaultOperandCharValues.size()] = this.predicateEvaluation ? 'T' : 'F';
        this.allPredicateBooleanValues[this.defaultOperandCharValues.size()] = this.predicateEvaluation;
    }

    private void setPredicateProbabilityString(Map<Character, Double> defaultOperandProbabilityValues) {
        this.predicateProbabilityModel = this.expression.getExpression();
        for (char operand : this.operands) {
            this.predicateProbabilityModel = this.predicateProbabilityModel.replace(Character.toString(operand), "{" + defaultOperandProbabilityValues.get(operand).toString() + "}");
        }
    }

    private void setPredicateBooleanString(Map<Character, Double> defaultOperandProbabilityValues, Double defaultTruthThreshold, Map<Character, Double> individualOperandTruthThresholds) {
        this.predicateBooleanModel = this.expression.getExpression();
        for (char operand : this.operands) {
            this.predicateBooleanModel = this.predicateBooleanModel.replace(Character.toString(operand), defaultOperandCharValues.get(operand).toString());
        }
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

    @Override
    public String getModelName() {
        return this.modelName;
    }

    @Override
    public Proposition getProposition() {
        return this.expression;
    }

    @Override
    public String[][] getTruthTable() throws InvalidExpressionException {
        return this.expression.getTruthTable();
    }

    @Override
    public String getExpression() {
        return this.expression.getExpression();
    }

    @Override
    public char[] getOperands() {
        return this.operands;
    }

    @Override
    public char getOperand(int index) {
        if (index < 0 || index >= this.operands.length)
            throw new IllegalArgumentException("Operand index out of bounds");

        return this.operands[index];
    }

    public Map<Character, Double> getOperandTruthValues() {
        return this.defaultOperandProbabilityValues;
    }

    @Override
    public Map<Character, String> getOperandSymbolicRepresentation() {
        return this.operandSymbolicRepresentation;
    }

    public double[] getAllPredicateProbabilityValues() {
        return this.allPredicateProbabilityValues;
    }

    @Override
    public char[] getAllPredicateCharValues() {
        return this.allPredicateCharValues;
    }

    @Override
    public boolean[] getAllPredicateBooleanValues() {
        return this.allPredicateBooleanValues;
    }

    @Override
    public String getValidityEvaluation() {
        return this.validityEvaluation;
    }

    public String getPredicateProbabilityModel() {
        return this.predicateProbabilityModel;
    }

    public String getPredicateBooleanModel() {
        return this.predicateBooleanModel;
    }

    @Override
    public String getSymbolicRepresentation() {
        return this.symbolRepresentation;
    }

    @Override
    public Boolean getPredicateEvaluation() {
        return this.predicateEvaluation;
    }
}
