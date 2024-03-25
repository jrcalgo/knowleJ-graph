package src.LogicType.PropositionalLogic.Models;

import java.util.Map;

import src.Exceptions.InvalidExpressionException;
import src.LogicType.PropositionalLogic.Logic.Proposition;

public abstract class Model {
    public abstract String getModelName();

    public abstract Proposition getProposition();

    public abstract String[][] getTruthTable() throws InvalidExpressionException;

    public abstract String getExpression();

    public abstract char[] getOperands();

    public abstract char getOperand(int index);

    public abstract Map<Character, String> getOperandSymbolicRepresentation();

    public abstract char[] getAllPredicateTruthValues();

    public abstract boolean[] getAllPredicateBooleanValues();

    public abstract String getValidityEvaluation();

    public abstract String getSymbolicRepresentation();

    public abstract Boolean getPredicateEvaluation();
}
