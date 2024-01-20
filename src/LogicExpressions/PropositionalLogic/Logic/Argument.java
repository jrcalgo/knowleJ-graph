package src.LogicExpressions.PropositionalLogic.Logic;

import java.util.ArrayList;
import java.util.HashMap;

import src.DataStructures.PropositionTree;
import src.DataStructures.PropositionTreeNode;
import src.Exceptions.InvalidExpressionException;
import src.Exceptions.InvalidLogicOperatorException;
import src.Exceptions.InvalidOperandException;
import src.LogicExpressions.PropositionalLogic.Models.*;

public class Argument<M extends Model> {
    private M[] knowledgeBase;
    private ArrayList<ArrayList<String>> trueKBModels;

    private char[] operands;
    private int operandCount;

    private String[][] truthTable;
    private Boolean[][] tableValues;

    public Argument(M[] knowledgeBase) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        validateKnowledgeBase(knowledgeBase);
        setTruthTable();
    }

    private void validateKnowledgeBase(M[] kb) {
        if (kb == null || kb.length == 0)
            throw new IllegalArgumentException("Knowledge base cannot be null or empty.");

        StringBuilder operandString = new StringBuilder();
        for (M m : kb) {
            char[] modelOperands = m.getOperands();
            for (int j = 0; j < modelOperands.length; j++) {
                if (operandString.indexOf(String.valueOf(modelOperands[j])) == -1) {
                    operandString.append(modelOperands[j]);
                }
            }
        }
        if (operandString.length() > 15)
            throw new IllegalArgumentException("Too many total operands in knowledge base; only 15 total allowed.");

        this.operands = operandString.toString().toCharArray();
        this.operandCount = operandString.length();
        this.knowledgeBase = kb;
    }

    private void setTruthTable() throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        int boolRowsCount = (int) Math.pow(2, this.operandCount);
        int boolColsCount = this.operandCount + this.knowledgeBase.length + 1;

        TruthTableBuilder ttb = new TruthTableBuilder(this.operands, boolRowsCount, boolColsCount);
        this.truthTable = ttb.getTruthTable();
        this.tableValues = ttb.getValueTable();
        ttb.close();

        for (int i = operandCount, j = 0; j < this.knowledgeBase.length; i++, j++)
            this.truthTable[0][i] = this.knowledgeBase[j].getExpression();
        
        this.truthTable[0][boolColsCount-1] = "KB";

        this.trueKBModels = new ArrayList<>();
        ArrayList<String> titleRow = new ArrayList<>();
        for (int i = 0; i < boolColsCount; i++)
            titleRow.add(this.truthTable[0][i]);

        this.trueKBModels.add(titleRow);
        titleRow = null;

        HashMap<Character, Character> valueMap = new HashMap<>();
        boolean[] modelEvaluations = new boolean[this.knowledgeBase.length];
        ArrayList<String> trueKBModelPlaceholder;
        int trueKBRows = 0;
        for (int rows = 0; rows < boolRowsCount; rows++) {
            for (int i = 0; i < operandCount; i++)
                valueMap.put(operands[i], this.truthTable[rows + 1][i].charAt(0));

            for (int i = 0; i < this.knowledgeBase.length; i++) {
                Proposition p = new Proposition(this.knowledgeBase[i].getExpression());
                this.tableValues[rows][operandCount+i] = p.evaluateExpression(valueMap);
                this.truthTable[rows+1][operandCount+i] = this.tableValues[rows][operandCount+i] ? "T" : "F";
            }
            valueMap.clear();

            int i = 0;
            while (operandCount+i < tableValues[rows].length-1) {
                modelEvaluations[i] = this.tableValues[rows][operandCount+i];
                i++;
            }
            this.tableValues[rows][boolColsCount-1] = evaluateKnowledgeBase(modelEvaluations);
            this.truthTable[rows+1][boolColsCount-1] = this.tableValues[rows][boolColsCount-1] ? "T" : "F";

            if (tableValues[rows][boolColsCount-1]) {
                trueKBModelPlaceholder = new ArrayList<>();
                for (int j = 0; j < boolColsCount; j++)
                    trueKBModelPlaceholder.add(this.tableValues[rows][j] ? "T" : "F");

                this.trueKBModels.add(trueKBModelPlaceholder);
            }
        }
    }

    private boolean evaluateKnowledgeBase(boolean[] modelEvaluations) {
        boolean rowAnswer = true;
        for (int i = 0; i < modelEvaluations.length; i++) 
            rowAnswer = rowAnswer && modelEvaluations[i];
        
        return rowAnswer;
    }

    public void checkAllModels(String query) {
        if (query == null || query.length() == 0)
            throw new IllegalArgumentException("String query cannot be null or empty.");
    }

    public void checkAllModels(Proposition query) {
        if (query == null)
            throw new IllegalArgumentException("Proposition query cannot be null or empty.");
    }


    public void checkPresentModels(String query) {
        if (query == null)
            throw new IllegalArgumentException("String query cannot be null or empty.");
    }

    public void checkPresentModels(Proposition query) {
        if (query == null)
            throw new IllegalArgumentException("Proposition query cannot be null");
    }

    public String[][] getTruthTable() {
        return this.truthTable;
    }

    public Boolean[][] getTableValues() {
        return this.tableValues;
    }

    public void printTruthTable() {
        System.out.println();
        for (int i = 0; i < truthTable[i].length; i++) {
            System.out.print(truthTable[0][i] + "\s\s\s");
        }
        System.out.println();
        for (int i = 1; i < truthTable.length; i++) {
            for (int j = 0; j < truthTable[i].length; j++) {
                System.out.print(truthTable[i][j] + "\s\s\s\s\s");
            }
            System.out.println();
        }
    }

    public void printTruthTable(int fromCol, int toCol) {
        if (fromCol > toCol)
            throw new IndexOutOfBoundsException(fromCol + " is out of bounds.");

        for (int i = 0; i < truthTable[i].length; i++) {
            System.out.print(i + ".\s");
            System.out.print(truthTable[0][i] + "\s\s\s");
        }

        for (int i = 1; i < truthTable.length; i++) {
            for (int j = fromCol; j < toCol; j++) {
                System.out.print(truthTable[i][j] + "\s\s\s\s\s");
            }
            System.out.println();
        }
    }

    public M[] getKnowledgeBase() {
        return this.knowledgeBase;
    }

    public String[] getKnowledgeBaseExpressions() {
        String[] expressions = new String[this.knowledgeBase.length];
        for (int i = 0; i < this.knowledgeBase.length; i++) {
            expressions[i] = this.knowledgeBase[i].getExpression();
        }
        return expressions;
    }

    public ArrayList<ArrayList<String>> getAllTrueKBModels() {
        return this.trueKBModels;
    }

    public void printAllTrueKBModels() {
        for (int i = 0; i < this.trueKBModels.size(); i++) {
            for (int j = 0; j < this.trueKBModels.get(i).size(); j++) {
                System.out.print(this.trueKBModels.get(i).get(j) + "\s");
            }
            System.out.println();
        }
    }

    public class InferenceLaws<M> {
        private Model model;
        private String mPredicate;
        private Proposition mProposition;
        private char[] mOperands;
        private String e;
        // converted expression
        private String cE;

        public LogicTreeNode checkPropositionLaws(Proposition p)
                throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
            e = p.getExpression();
            cE = p.getConvertedExpression();
        }

        public LogicTreeNode checkArgumentLaws(Argument a) {

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
