package lib.src.main.java.knowlej.PropositionalLogic.Models;

import java.util.Map;

import lib.src.main.java.knowlej.Exceptions.InvalidExpressionException;
import lib.src.main.java.knowlej.PropositionalLogic.Logic.Proposition;

public abstract class Model {
    public abstract String getModelName();

    public abstract Proposition getProposition();

    public abstract String[][] getTruthTable() throws InvalidExpressionException;

    public abstract String getExpression();

    public abstract char[] getOperands();

    public abstract char getOperand(int index);

    public abstract Map<Character, String> getOperandSymbolicRepresentation();

    public abstract char[] getAllPredicateCharValues();

    public abstract boolean[] getAllPredicateBooleanValues();

    public abstract String getValidityEvaluation();

    public abstract String getSymbolicRepresentation();

    public abstract Boolean getPredicateEvaluation();
}
