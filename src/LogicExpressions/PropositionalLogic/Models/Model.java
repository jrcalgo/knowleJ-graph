package src.LogicExpressions.PropositionalLogic.Models;

import java.util.Map;

import src.LogicExpressions.PropositionalLogic.Logic.Proposition;

public abstract class Model {
    public abstract String getModelName();

    public abstract Proposition getProposition();

    public abstract String[][] getTruthTable();

    public abstract String getExpression();

    public abstract char[] getOperands();

    public abstract char getOperand(int index);

    public abstract Map<Character, Character> getOperandTruthValues();

    public abstract Map<Character, String> getOperandSymbolicRepresentation();

    public abstract char[] getAllPredicateTruthValues();

    public abstract boolean[] getAllPredicateBooleanValues();

    public abstract String getEquivalencyEvaluation();

    public abstract String getPredicateModel();

    public abstract String getSymbolicRepresentation();

    public abstract Boolean getPredicateEvaluation();
}
