package ai.knowlej.DataStructures;

import ai.knowlej.Exceptions.InvalidExpressionException;
import ai.knowlej.Exceptions.InvalidLogicOperatorException;
import ai.knowlej.Exceptions.InvalidOperandException;
import ai.knowlej.PropositionalLogic.Logic.Proposition;

public class ActionSchema {
    // preconditions and effects before conjunciton
    private String[] preconditions;
    private String[] effects;

    public ActionSchema() {
        this.preconditions = null;
        this.effects = null;
    }

    public ActionSchema(String[] preconditions, String[] effects) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        for (String statement : preconditions) {
            try {
                Proposition propTest = new Proposition(statement);
            } catch (InvalidExpressionException e) {
                throw new InvalidExpressionException("Invalid statement included in preconditions");
            }
        }
        for (String statement : effects) {
            try {
                Proposition propTest = new Proposition(statement);
            } catch (InvalidExpressionException e) {
                throw new InvalidExpressionException("Invalid statement included in effects");
            }
        }
        System.gc();
        this.preconditions = preconditions;
        this.effects = effects;
    }

    public String[] getPreconditions() {
        return this.preconditions;
    }

    public String[] getEffects() {
        return this.effects;
    }

    public void setPreconditions(String[] preconditions) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        for (String statement : preconditions) {
            try {
                Proposition propTest = new Proposition(statement);
            } catch (InvalidExpressionException e) {
                throw new InvalidExpressionException("Invalid statement included in preconditions");
            }
        }
        this.preconditions = preconditions;
    }

    public void setEffects(String[] effects) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        for (String statement : effects) {
            try {
                Proposition propTest = new Proposition(statement);
            } catch (InvalidExpressionException e) {
                throw new InvalidExpressionException("Invalid statement included in preconditions");
            }
        }
        this.effects = effects;
    }
}
