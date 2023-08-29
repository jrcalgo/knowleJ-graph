package src.LogicExpressions.PropositionalLogic.Logic;

import src.Interfaces.Equivalencies;
import src.LogicExpressions.PropositionalLogic.Laws.InferenceLaws;

public class LogicalArgument extends InferenceLaws implements Equivalencies {
    private LogicalPropositions expression;
    private LogicalPredicate predicate;

    private LogicalPropositions[] propHypothesis;
    private LogicalPropositions[] propConclusion;
    private LogicalPredicate[] predHypothesis;
    private LogicalPredicate[] predConclusion;


    public LogicalArgument(LogicalPropositions expression) {
        this.expression = expression;
    }

    public LogicalArgument(LogicalPredicate predicate) {
        this.predicate = predicate;
    }

    public LogicalArgument(LogicalPropositions expression, LogicalPredicate predicate) {
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



}
