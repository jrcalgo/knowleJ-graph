package src.LogicExpressions.PropositionalLogic.Laws;

import src.Exceptions.InvalidExpressionException;
import src.Exceptions.InvalidLogicOperatorException;
import src.Exceptions.InvalidOperandException;
import src.LogicExpressions.PropositionalLogic.Logic.*;

public class InferenceLaws {
    private Model model;
    private String mPredicate;
    private Proposition mProposition;
    private char[] mOperands;
    private String e;
    // converted expression
    private String cE;

    public InferenceLaws(Model model) {
        this.model = model;
        this.mProposition = model.getProposition();
        this.mOperands = model.getOperands();
        this.mPredicate = model.getPredicateModel();
        this.e = mProposition.getExpression();
        this.cE = mProposition.getConvertedExpression();
    }

    public Proposition[][] getLawPropositions()
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        return new Proposition[][] { idempotentLaw(), associativeLaw(), commutativeLaw(), distributiveLaw(),
                identityLaw(),
                dominationLaw(), doubleNegationLaw(), complementLaw(), deMorgansLaw(), absorptionLaw(),
                conditionalIdentity() };
    }

    public Argument[][] getLawArguments() {
        return new Argument[][] { modusPonens(), modusTollens(), addition(), simplification(), conjunction(),
                hypotheticalSyllogism(), disjunctiveSyllogism(), resolution() };
    }

    /**
     * Laws of Propositional Logic
     * 
     * @throws InvalidLogicOperatorException
     * @throws InvalidOperandException
     * @throws InvalidExpressionException
     */
    private Proposition[] idempotentLaw()
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        Proposition[] iL = new Proposition[this.mOperands.length];
        for (int i = 0; i < this.mOperands.length; i++) {
            if (this.cE.contains(this.mOperands[i] + "a" + this.mOperands[i])
                    || this.cE.contains(this.mOperands[i] + "o" + this.mOperands[i])) {
                iL[i] = new Proposition(this.mOperands[i] + "");
            } else
                continue;
        }
        return iL;
    }

    private Proposition[] associativeLaw() throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        Proposition[] aL = new Proposition[this.mOperands.length];
        for (int i = 0; i < this.mOperands.length; i++) {
            for (int j = 0; j < this.mOperands.length; j++) {
                for (int k = 0; k < this.mOperands.length; k++) {
                    if (this.cE.contains(this.mOperands))
                }
            }
            if (this.cE.contains(this.mOperands[i] + "a" + this.mOperands[i]) || this.cE.contains(this.mOperands[i] + "o" + this.mOperands[i])) {
                aL[i] = new Proposition(this.mOperands[i] + "");
            } else
                continue;
        }
        return aL;
    }

    private Proposition[] commutativeLaw()
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        Proposition[] cL = new Proposition[this.mOperands.length];
        for (int i = 0; i < this.mOperands.length; i++) {
            if (this.cE.contains(this.mOperands[i] + "a" + this.mOperands[i])
                    || this.cE.contains(this.mOperands[i] + "o" + this.mOperands[i])) {
                cL[i] = new Proposition(this.mOperands[i] + "");
            } else
                continue;
        }
        return cL;
    }

    private Proposition[] distributiveLaw()
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        Proposition[] iL = new Proposition[this.mOperands.length];
        for (int i = 0; i < this.mOperands.length; i++) {
            if (this.cE.contains(this.mOperands[i] + "a" + this.mOperands[i])
                    || this.cE.contains(this.mOperands[i] + "o" + this.mOperands[i])) {
                iL[i] = new Proposition(this.mOperands[i] + "");
            } else
                continue;
        }
        return iL;
    }

    private Proposition[] identityLaw()
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        Proposition[] iL = new Proposition[this.mOperands.length];
        for (int i = 0; i < this.mOperands.length; i++) {
            if (this.cE.contains(this.mOperands[i] + "oF") || (this.cE.contains(this.mOperands[i] + "aT"))) {
                iL[i] = new Proposition(this.mOperands[i] + "");
            } else
                continue;
        }
        return iL;
    }

    private Proposition[] dominationLaw()
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        Proposition[] iL = new Proposition[this.mOperands.length];
        for (int i = 0; i < this.mOperands.length; i++) {
            if (this.cE.contains(this.mOperands[i] + "aF")) {
                iL[i] = new Proposition("F");
            } else if (this.cE.contains(this.mOperands[i] + "oT")) {
                iL[i] = new Proposition("T");
            }
            continue;
        }
        return iL;
    }

    private Proposition[] doubleNegationLaw()
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        Proposition[] dNL = new Proposition[this.mOperands.length];
        for (int i = 0; i < this.mOperands.length; i++) {
            if (this.cE.contains("nn" + this.mOperands[i])) {
                dNL[i] = new Proposition(this.mOperands[i] + "");
            } else
                continue;
        }
        return dNL;
    }

    private Proposition[] complementLaw()
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        Proposition[] iL = new Proposition[this.mOperands.length];
        for (int i = 0; i < this.mOperands.length; i++) {
            if (this.cE.contains(this.mOperands[i] + "an" + this.mOperands[i])) {
                iL[i] = new Proposition("F");
            } else if (this.cE.contains(this.mOperands[i] + "on" + this.mOperands[i])) {
                iL[i] = new Proposition("T");
            }
            continue;
        }

        for (int i = 0; i < this.cE.length(); i++) {
            if (this.cE.charAt(i) == 'n' && this.cE.charAt(i + 1) == 'F') {
                iL[i] = new Proposition("F");
            } else if (this.cE.charAt(i) == 'n' && this.cE.charAt(i + 1) == 'T') {
                iL[i] = new Proposition("T");
            } else
                break;
        }
        return iL;
    }

    private Proposition[] deMorgansLaw() {
        Proposition[] iL = new Proposition[this.mOperands.length];
        for (int i = 0; i < this.mOperands.length; i++) {
            if (this.cE.contains(this.mOperands[i] + "a" + this.mOperands[i])
                    || this.cE.contains(this.mOperands[i] + "o" + this.mOperands[i])) {
                iL[i] = new Proposition(this.mOperands[i] + "");
            } else
                continue;
        }
        return iL;
    }

    private Proposition[] absorptionLaw() {
        Proposition[] iL = new Proposition[this.mOperands.length];
        for (int i = 0; i < this.mOperands.length; i++) {
            if (this.cE.contains(this.mOperands[i] + "a" + this.mOperands[i])
                    || this.cE.contains(this.mOperands[i] + "o" + this.mOperands[i])) {
                iL[i] = new Proposition(this.mOperands[i] + "");
            } else
                continue;
        }
        return iL;
    }

    private Proposition[] conditionalIdentity() {
        Proposition[] iL = new Proposition[this.mOperands.length];
        for (int i = 0; i < this.mOperands.length; i++) {
            if (this.cE.contains(this.mOperands[i] + "a" + this.mOperands[i])
                    || this.cE.contains(this.mOperands[i] + "o" + this.mOperands[i])) {
                iL[i] = new Proposition(this.mOperands[i] + "");
            } else
                continue;
        }
        return iL;
    }

    /* Rules of Argument Inference */
    private Argument[] modusPonens() {
        if
    }

    private Argument[] modusTollens() {
        if
    }

    private Argument[] addition() {
        if
    }

    private Argument[] simplification() {
        if
    }

    private Argument[] conjunction() {
        if
    }

    private Argument[] hypotheticalSyllogism() {
        if
    }

    private Argument[] disjunctiveSyllogism() {
        if
    }

    private Argument[] resolution() {
        if
    }
}
