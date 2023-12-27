package src.LogicExpressions.PropositionalLogic.Logic;

import src.Interfaces.Equivalencies;
import src.LogicExpressions.PropositionalLogic.Laws.InferenceLaws;

public class Argument extends InferenceLaws implements Equivalencies {
    private Propositions expression;
    private Predicate predicate;

    private Propositions[] propHypothesis;
    private Propositions[] propConclusion;
    private Predicate[] predHypothesis;
    private Predicate[] predConclusion;


    public Argument(Propositions expression) {
        this.expression = expression;
    }

    public Argument(Predicate predicate) {
        this.predicate = predicate;
    }

    public Argument(Propositions expression, Predicate predicate) {
        this.expression = expression;
        this.predicate = predicate;
    }

    public void construct()

    @Override
    public boolean isTautology() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isTautology'");
    }

    @Override
    public boolean isContradiction() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isContradiction'");
    }

    @Override
    public boolean isContingency() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isContingency'");
    }

    @Override
    public boolean isTautology(String[] s) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isTautology'");
    }

    @Override
    public boolean isTautology(Boolean[] b) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isTautology'");
    }

    @Override
    public boolean isContradiction(String[] s) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isContradiction'");
    }

    @Override
    public boolean isContradiction(Boolean[] b) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isContradiction'");
    }

    @Override
    public boolean isContingency(String[] s) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isContingency'");
    }

    @Override
    public boolean isContingency(Boolean[] b) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isContingency'");
    }



}
