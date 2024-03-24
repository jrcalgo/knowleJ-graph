package src.LogicType.PropositionalLogic.Models;

import java.util.Map;

import src.Exceptions.InvalidExpressionException;
import src.Exceptions.InvalidLogicOperatorException;
import src.Exceptions.InvalidOperandException;
import src.LogicType.PropositionalLogic.Logic.Proposition;
import src.LogicType.PropositionalLogic.Logic.Validity;

public class StochasticModel extends Model {
    private String modelName;
    private Proposition expression;

    private char[] operands;
    private Map<Character, Double> defaultOperandTruthValues;

    private double defaultTruthThreshold;
    private Map<Character, Double> operandTruthThresholds;

    private String predicateModel;
    private boolean predicateEvaluation;
    private double[] allPredicateProbabilityValues;
    private boolean[] allPredicateBooleanValues;
    private String validityEvaluation;

    private String symbolRepresentation;
    private Map<Character, String> operandSymbolicRepresentation;

    public StochasticModel(String modelName, String expression) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null || expression.isEmpty()) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = new Proposition(expression);
    }

    public StochasticModel(String modelName, Proposition expression) {
        if (expression == null) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = expression;
    }

    public StochasticModel(String modelName, String expression, Map<Character, String> operandSymbolicRepresentation) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null || expression.isEmpty()) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = new Proposition(expression);

        this.operandSymbolicRepresentation = operandSymbolicRepresentation;
        setSymbolicString(this.operandSymbolicRepresentation);

    }

    public StochasticModel(String modelName, Proposition expression, Map<Character, String> operandSymbolicRepresentation) {
        if (expression == null) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = expression;

        this.operandSymbolicRepresentation = operandSymbolicRepresentation;
        setSymbolicString(this.operandSymbolicRepresentation);
    }

    public StochasticModel(String modelName, String expression, Map<Character, String> operandSymbolicRepresentation, Map<Character, Double> defaultOperandTruthValues,  double defaultTruthThreshold) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null || expression.isEmpty()) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = new Proposition(expression);

        this.operandSymbolicRepresentation = operandSymbolicRepresentation;
        setSymbolicString(this.operandSymbolicRepresentation);

        setOperands(defaultOperandTruthValues);
        
    }

    public StochasticModel(String modelName, Proposition expression, Map<Character, String> operandSymbolicRepresentation, Map<Character, Double> defaultOperandTruthValues, double defaultTruthThreshold) {
        if (expression == null) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = expression;
    }
    
    public StochasticModel(String modelName, String expression, Map<Character, String> operandSymbolicRepresentation, double defaultTruthThreshold, Map<Character, Double> defaultOperandTruthValues, Map<Character, Double> operandTruthThresholds) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (expression == null || expression.isEmpty()) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = new Proposition(expression);
    }

    public StochasticModel(String modelName, Proposition expression, Map<Character, String> operandSymbolicRepresentation, double defaultTruthThreshold, Map<Character, Double> defaultOperandTruthValues, Map<Character, Double> operandTruthThresholds) {
        if (expression == null) 
            throw new IllegalArgumentException("Expression cannot be null or empty.");

        this.modelName = modelName;
        this.expression = expression;
    }

    private void setOperands(Map<Character, Double> defaultOperandTruthValues) {
        operands = new char[this.expression.getOperandCount()];
        for (int i = 0; i < this.expression.getOperandCount(); i++) {
            String operand = this.expression.getSentence(i);
            operands[i] = operand.charAt(0);
        }
        for (int i = 0; i < operands.length; i++) {
            if (!defaultOperandTruthValues.containsKey(operands[i]))
                throw new IllegalArgumentException(operands[i] + " not in expression.");
        }
    }

    private void setPredicateString(Map<Character, Double> defaultOperandTruthValues, double defaultTruthThreshold, Map<Character, Double> operandTruthThresholds) {
            
    }

    private void setAllPredicateTruthValues() {

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
        return this.defaultOperandTruthValues;
    }

    @Override
    public Map<Character, String> getOperandSymbolicRepresentation() {
        return this.operandSymbolicRepresentation;
    }

    @Override
    public double[] getAllPredicateTruthValues() {
        return this.allPredicateProbabilityValues;
    }

    @Override
    public boolean[] getAllPredicateBooleanValues() {
        return this.allPredicateBooleanValues;
    }

    @Override
    public String getValidityEvaluation() {
        return this.validityEvaluation;
    }

    @Override
    public String getPredicateModel() {
        return this.predicateModel;
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
