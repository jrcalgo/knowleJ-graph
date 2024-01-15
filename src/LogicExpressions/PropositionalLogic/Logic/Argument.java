package src.LogicExpressions.PropositionalLogic.Logic;

import src.DataStructures.LogicTree;
import src.DataStructures.LogicTreeNode;
import src.Exceptions.InvalidExpressionException;
import src.Exceptions.InvalidLogicOperatorException;
import src.Exceptions.InvalidOperandException;
import src.Interfaces.ModelInterface;
import src.LogicExpressions.PropositionalLogic.Models.*;

public class Argument<M extends Model> {
    private M[] models;
    private M query;
    private char[] modelCharEvaluations;
    private boolean[] modelBooleanEvaluations;
    private char argumentCharEvaluation;
    private boolean argumentBooleanEvaluation;
    private String[] truthTable;
    private Boolean[] tableValues;

    public Argument(Model[] models, Model query) {
        this.models = models;
        this.query = query;
        setTruthTable();
    }

    public Argument(Model[] models, String query) {
        this.models = models;
        this.query = new M("Query", query);
        setTruthTable();
    }
    
    public void checkModels() {
        
    }

    public void setTruthTable() {
        this.truthTable = new String[this.models.length + 1];
        for (int i = 0; i < this.models.length; i++) {
            this.truthTable[i] = this.models[i].getExpression() + " : " + this.models[i].getPredicateEvaluation();
        }
        this.truthTable[this.truthTable.length] = query.getExpression() + " : " + evaluateArgument();
    }

    public boolean evaluateArgument() {
        boolean answer = false;

        return answer;
        // this.modelCharEvaluations = new char[this.models.length];
        // this.modelBooleanEvaluations = new boolean[this.models.length];
        // for (int i = 0; i < this.models.length; i++) {
        //     this.modelCharEvaluations[i] = this.models[i].getPredicateEvaluation() ? 'T' : 'F';
        //     this.modelBooleanEvaluations[i] = this.models[i].getPredicateEvaluation();
        // }
        
        // Boolean modelEvaluation = this.modelBooleanEvaluations[0];
        // for (int i = 1; i < this.models.length; i++)
        //         modelEvaluation = modelEvaluation && this.modelBooleanEvaluations[i];

        // if (modelEvaluation == true && this.query.get)
    }

    public String[] getTruthTable() {
        return this.truthTable;
    }

    public Boolean[] getTableValues() {
        return this.tableValues;
    }

    public Model[] getModels() {
        return this.models;
    }

    public String[] getArgumentExpressions() {
        String[] expressions = new String[this.models.length];
        for (int i = 0; i < this.models.length; i++) {
            expressions[i] = this.models[i].getExpression();
        }
        return expressions;
    }

    public class InferenceLaws<M> {
        private Model model;
        private String mPredicate;
        private Proposition mProposition;
        private char[] mOperands;
        private String e;
        // converted expression
        private String cE;

        public LogicTreeNode<String> checkPropositionLaws(Proposition p)
                throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
            e = p.getExpression();
            cE = p.getConvertedExpression();
        }

        public LogicTreeNode<String> checkArgumentLaws(Argument a) {

        }

        /**
         * Laws of Propositional Logic
         * 
         * @throws InvalidLogicOperatorException
         * @throws InvalidOperandException
         * @throws InvalidExpressionException
         */
        private Proposition idempotentLaw()
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

        private Proposition associativeLaw() throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
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

        private Proposition commutativeLaw()
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

        private Proposition distributiveLaw()
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

        private Proposition identityLaw()
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

        private Proposition dominationLaw()
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

        private Proposition deMorgansLaw() {
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

        private Proposition absorptionLaw() {
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

        private Proposition conditionalIdentity() {
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

}
