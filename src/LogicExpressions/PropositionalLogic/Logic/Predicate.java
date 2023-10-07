package src.LogicExpressions.PropositionalLogic.Logic;

import src.DataStructures.PartitionedParsingTree;
import src.Interfaces.Equivalencies;
import src.LogicExpressions.PropositionalLogic.Characters.Quantifiers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class Predicate extends Quantifiers implements Equivalencies {

    private Map<String, Boolean> operands;
    private Propositions expression;

    private static final boolean TRUE = true;
    private static final boolean FALSE = false;

    public Predicate(Propositions expression, Map<String, Boolean> operands) {
        this.operands = new HashMap<>(operands);
        this.expression = expression;
    }

    

    public boolean universal(List<T> elements, Predicate predicate) {
        for (T element : elements) {
            if (!predicate.test(element))
                return FALSE;
        }
        return TRUE;
    }

    public boolean universal(PartitionedParsingTree<T> elements, Predicate predicate) {
        for (T element : elements) {
            if (!predicate.test(element))
                return FALSE;
        }
        return TRUE;
    }

    public boolean existential(List<T> elements, Predicate<T> predicate) {
        for (T element : elements) {
            if (predicate.test(element))
                return TRUE;
        }
        return FALSE;
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
