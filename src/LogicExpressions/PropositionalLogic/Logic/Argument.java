package src.LogicExpressions.PropositionalLogic.Logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import src.DataStructures.DeductionTree;
import src.DataStructures.DeductionTreeNode;
import src.Exceptions.InvalidExpressionException;
import src.Exceptions.InvalidLogicOperatorException;
import src.Exceptions.InvalidOperandException;
import src.LogicExpressions.PropositionalLogic.Models.*;

public class Argument<M extends Model> {
    private M[] knowledgeBase;
    private ArrayList<ArrayList<String>> trueKBModels;

    private char[] operands;
    private byte operandCount;

    private String[][] allTruthTable;
    private Boolean[][] allTruthValues;

    // private String[][] currentTruthTable;
    // private Boolean[][] currentTruthValues;

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
        this.operandCount = (byte) operandString.length();
        this.knowledgeBase = kb;
    }

    private void setTruthTable() throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        int boolRowsCount = (int) Math.pow(2, this.operandCount);
        int boolColsCount = this.operandCount + this.knowledgeBase.length + 1;

        // set all truth table
        TruthTableBuilder ttb = new TruthTableBuilder(this.operands, boolRowsCount, boolColsCount);
        this.allTruthTable = ttb.getTruthTable();
        this.allTruthValues = ttb.getValueTable();
        ttb.close();

        for (int i = operandCount, j = 0; j < this.knowledgeBase.length; i++, j++)
            this.allTruthTable[0][i] = this.knowledgeBase[j].getExpression();
        
        this.allTruthTable[0][boolColsCount-1] = "KB";

        this.trueKBModels = new ArrayList<>();
        ArrayList<String> titleRow = new ArrayList<>();
        for (int i = 0; i < boolColsCount; i++)
            titleRow.add(this.allTruthTable[0][i]);

        this.trueKBModels.add(titleRow);
        titleRow = null;
        HashMap<Character, Character> valueMap = new HashMap<>();
        boolean[] modelEvaluations = new boolean[this.knowledgeBase.length];
        ArrayList<String> trueKBModelPlaceholder;
        for (int rows = 0; rows < boolRowsCount; rows++) {
            // setting base table values
            for (int i = 0; i < operandCount; i++)
                valueMap.put(operands[i], this.allTruthTable[rows + 1][i].charAt(0));

            for (int i = 0; i < this.knowledgeBase.length; i++) {
                Proposition p = new Proposition(this.knowledgeBase[i].getExpression());
                this.allTruthValues[rows][operandCount+i] = p.evaluateExpression(valueMap);
                this.allTruthTable[rows+1][operandCount+i] = this.allTruthValues[rows][operandCount+i] ? "T" : "F";
            }
            valueMap.clear();
            
            // setting KB table values, including KB evaluation(s)
            int i = 0;
            while (operandCount+i < allTruthValues[rows].length-1) {
                modelEvaluations[i] = this.allTruthValues[rows][operandCount+i];
                i++;
            }
            this.allTruthValues[rows][boolColsCount-1] = evaluateKnowledgeBase(modelEvaluations);
            this.allTruthTable[rows+1][boolColsCount-1] = this.allTruthValues[rows][boolColsCount-1] ? "T" : "F";

            if (allTruthValues[rows][boolColsCount-1]) {
                trueKBModelPlaceholder = new ArrayList<>();
                for (int j = 0; j < boolColsCount; j++)
                    trueKBModelPlaceholder.add(this.allTruthValues[rows][j] ? "T" : "F");

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

    public String checkAllTTModels(String query) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (query == null || query.length() == 0)
            throw new IllegalArgumentException("String query cannot be null or empty.");

        return checkAllTTModels(new Proposition(query));
    }

    public String checkAllTTModels(Proposition query) throws InvalidExpressionException {
        if (query == null)
            throw new IllegalArgumentException("Proposition query cannot be null or empty.");

        ArrayList<String> qOperands = query.getSentences(0, query.getOperandCount()-1);
        boolean commonOperand = false;
        for (String qOp : qOperands) {
            for (char op : this.operands) {
                if (qOp.charAt(0) == op) {
                    commonOperand = true;
                    continue;
                }
            }
            if (commonOperand)
                continue;
        }
        if (!commonOperand)
            throw new IllegalArgumentException("No common operand found in query when compared with knowledge base.");
        
        String answer = null;
        ArrayList<Boolean> queryValues = new ArrayList<>();
        HashMap<Character, Character> valueMap = new HashMap<>();
        int valueRows = 1;
        do {
            for (int i = 0; i < operandCount; i++) {
                valueMap.put(this.operands[i], this.trueKBModels.get(valueRows).get(i).charAt(0));
            }
            queryValues.add(query.evaluateExpression(valueMap));
            if (queryValues.get(valueRows-1))
                answer = "True";
            else
                answer = "False";
            
            if (valueRows > 1 && queryValues.get(valueRows-1) != queryValues.get(valueRows-2)) {
                return "Uncertain";
            }
            valueRows++;
        } while (valueRows <= this.trueKBModels.size()-1);

        return answer;
    }


    // public String checkCurrentTTModels(String query) {
    //     if (query == null)
    //         throw new IllegalArgumentException("String query cannot be null or empty.");

    //     return checkCurrentTTModels(new Proposition(query));
    // }

    // public String checkCurrentTTModels(Proposition query) {
    //     if (query == null)
    //         throw new IllegalArgumentException("Proposition query cannot be null");
        
    //     ArrayList<String> qOperands = query.getSentences(0, query.getOperandCount()-1);
    //     boolean commonOperand = false;
    //     for (String qOp : qOperands) {
    //         for (char op : this.operands) {
    //             if (qOp.charAt(0) == op) {
    //                 commonOperand = true;
    //                 continue;
    //             }
    //         }
    //         if (commonOperand)
    //             continue;
    //     }
    //     if (!commonOperand)
    //         throw new IllegalArgumentException("No common operand found in query when compared with knowledge base.");

    // }

    public String deduce(String query) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (query == null || query.length() == 0)
            throw new IllegalArgumentException("String query cannot be null or empty.");
        
        return deduce(new Proposition(query));
    }

    public String deduce(Proposition query) {
        if (query == null)
            throw new IllegalArgumentException("Proposition query cannot be null or empty.");
        
        ArrayList<String> qOperands = query.getSentences(0, query.getOperandCount()-1);
        boolean commonOperand = false;
        for (String qOp : qOperands) {
            for (char op : this.operands) {
                if (qOp.charAt(0) == op) {
                    commonOperand = true;
                    continue;
                }
            }
            if (commonOperand)
                continue;
        }
        if (!commonOperand)
            throw new IllegalArgumentException("No common operand found in query when compared with knowledge base.");

        String[] knowledgeExpressions = new String[this.knowledgeBase.length];
        for (int i = 0; i < this.knowledgeBase.length; i++) {
            knowledgeExpressions[i] = this.knowledgeBase[i].getExpression();
        }

        for ()
        DeductionTree<String> dt = new DeductionTree<String>(knowledgeExpressions, query);
        return dt.search(query.getExpression());

    }

    public String[][] getAllTruthTable() {
        return this.allTruthTable;
    }

    public Boolean[][] getAllTableValues() {
        return this.allTruthValues;
    }

    public void printTruthTable() {
        System.out.println();
        for (int i = 0; i < allTruthTable[i].length; i++) {
            System.out.print(allTruthTable[0][i] + "\s\s\s");
        }
        System.out.println();
        for (int i = 1; i < allTruthTable.length; i++) {
            for (int j = 0; j < allTruthTable[i].length; j++) {
                System.out.print(allTruthTable[i][j] + "\s\s\s\s\s");
            }
            System.out.println();
        }
    }

    public void printTruthTable(int fromCol, int toCol) {
        if (fromCol > toCol)
            throw new IndexOutOfBoundsException(fromCol + " is out of bounds.");

        for (int i = 0; i < allTruthTable[i].length; i++) {
            System.out.print(i + ".\s");
            System.out.print(allTruthTable[0][i] + "\s\s\s");
        }

        for (int i = 1; i < allTruthTable.length; i++) {
            for (int j = fromCol; j < toCol; j++) {
                System.out.print(allTruthTable[i][j] + "\s\s\s\s\s");
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

    static class InferenceLaws<M> {
        private Model model;
        private String mPredicate;
        private Proposition mProposition;
        private char[] mOperands;
        private String e;
        // converted expression
        private String cE;

        public String checkInferenceLaws(Proposition p)
                throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
            e = p.getExpression();
            cE = p.getConvertedExpression();
        }

        /* Rules of Argument Inference */
        private String modusPonens() {
            if
        }

        private String modusTollens() {
            if
        }

        private String addition() {
            if
        }

        private String simplification() {
            if
        }

        private String conjunction() {
            if
        }

        private String hypotheticalSyllogism() {
            if
        }

        private String disjunctiveSyllogism() {
            if
        }

        private String resolution() {
            if
        }
    }

    /**
     * Popular logic equivalencies used for inference and argumentation.
     */
    static class LogicalEquivalencyLaws {

        public String checkEquivalencyLaws(Proposition p) {

        }

                /**
         * 
         * @throws InvalidLogicOperatorException
         * @throws InvalidOperandException
         * @throws InvalidExpressionException
         */
        private String idempotentLaw()
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

        private String associativeLaw() throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
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

        private String commutativeLaw()
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

        private String distributiveLaw()
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

        private String identityLaw()
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

        private String dominationLaw()
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

        private String doubleNegationLaw()
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

        private String complementLaw()
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

        private String deMorgansLaw() {
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

        private String absorptionLaw() {
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

        private String conditionalIdentity() {
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
    }

    public class LogicalFallacies {

    }

}
