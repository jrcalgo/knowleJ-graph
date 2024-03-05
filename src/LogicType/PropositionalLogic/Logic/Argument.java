package src.LogicType.PropositionalLogic.Logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import src.DataStructures.DirectedDeductionGraph;
import src.DataStructures.DeductionGraphNode;

import src.Exceptions.InvalidExpressionException;
import src.Exceptions.InvalidLogicOperatorException;
import src.Exceptions.InvalidOperandException;
import src.LogicType.PropositionalLogic.Models.*;

public class Argument<M extends Model> {
    private M[] knowledgeBase;
    private ArrayList<ArrayList<String>> trueKBModels;

    private char[] operands;
    private byte operandCount;

    private String[][] allTruthTable;
    private Boolean[][] allTruthValues;

    // private String[][] currentTruthTable;
    // private Boolean[][] currentTruthValues;

    public Argument(M[] knowledgeBase)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
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

    private void setTruthTable()
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        int boolRowsCount = (int) Math.pow(2, this.operandCount);
        int boolColsCount = this.operandCount + this.knowledgeBase.length + 1;

        // set all truth table
        TruthTableBuilder ttb = new TruthTableBuilder(this.operands, boolRowsCount, boolColsCount);
        this.allTruthTable = ttb.getTruthTable();
        this.allTruthValues = ttb.getValueTable();
        ttb.close();

        for (int i = operandCount, j = 0; j < this.knowledgeBase.length; i++, j++)
            this.allTruthTable[0][i] = this.knowledgeBase[j].getExpression();

        this.allTruthTable[0][boolColsCount - 1] = "KB";

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
                this.allTruthValues[rows][operandCount + i] = p.evaluateExpression(valueMap);
                this.allTruthTable[rows + 1][operandCount + i] = this.allTruthValues[rows][operandCount + i] ? "T"
                        : "F";
            }
            valueMap.clear();

            // setting KB table values, including KB evaluation(s)
            int i = 0;
            while (operandCount + i < allTruthValues[rows].length - 1) {
                modelEvaluations[i] = this.allTruthValues[rows][operandCount + i];
                i++;
            }
            this.allTruthValues[rows][boolColsCount - 1] = evaluateKnowledgeBase(modelEvaluations);
            this.allTruthTable[rows + 1][boolColsCount - 1] = this.allTruthValues[rows][boolColsCount - 1] ? "T" : "F";

            if (allTruthValues[rows][boolColsCount - 1]) {
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

    public String checkAllTTModels(String query)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (query == null || query.length() == 0)
            throw new IllegalArgumentException("String query cannot be null or empty.");
        if (query.contains(",") && !query.startsWith(",") && !query.endsWith(",")) {
            String[] queries = query.split(",");
            StringBuilder answer = new StringBuilder();
            for (String q : queries) {
                answer.append(checkAllTTModels(new Proposition(q)) + ", ");
            }
            return answer.toString().substring(0, answer.length() - 2);
        }
        return checkAllTTModels(new Proposition(query));
    }

    public String checkAllTTModels(Proposition query) throws InvalidExpressionException {
        if (query == null)
            throw new IllegalArgumentException("Proposition query cannot be null or empty.");

        ArrayList<String> qOperands = query.getSentences(0, query.getOperandCount() - 1);
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
            if (queryValues.get(valueRows - 1))
                answer = "True";
            else
                answer = "False";

            if (valueRows > 1 && queryValues.get(valueRows - 1) != queryValues.get(valueRows - 2)) {
                return "Uncertain";
            }
            valueRows++;
        } while (valueRows <= this.trueKBModels.size() - 1);

        return answer;
    }

    // public String checkCurrentTTModels(String query) {
    // if (query == null)
    // throw new IllegalArgumentException("String query cannot be null or empty.");

    // return checkCurrentTTModels(new Proposition(query));
    // }

    // public String checkCurrentTTModels(Proposition query) {
    // if (query == null)
    // throw new IllegalArgumentException("Proposition query cannot be null");

    // ArrayList<String> qOperands = query.getSentences(0,
    // query.getOperandCount()-1);
    // boolean commonOperand = false;
    // for (String qOp : qOperands) {
    // for (char op : this.operands) {
    // if (qOp.charAt(0) == op) {
    // commonOperand = true;
    // continue;
    // }
    // }
    // if (commonOperand)
    // continue;
    // }
    // if (!commonOperand)
    // throw new IllegalArgumentException("No common operand found in query when
    // compared with knowledge base.");

    // }

    public boolean isValidArgument(String kbQuery) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (kbQuery == null || kbQuery.length() == 0)
            throw new IllegalArgumentException("String query cannot be null or empty.");
        
        boolean foundQuery = false;
        String[] kbExpressions = this.getKnowledgeBaseExpressions();
        for (String premise : kbExpressions) {
            if (premise.equals(kbQuery)) {
                foundQuery = true;
                continue;
            } else
                break;
        }
        if (!foundQuery)
            throw new IllegalArgumentException("No common operand found in query when compared with knowledge base.");

        DirectedDeductionGraph dg = new DirectedDeductionGraph(kbExpressions, new Proposition(kbQuery));
        return bidirectionalIterativeDeepeningSearch(dg, false);
    }

    public <G> G deduce(String query, G returnType)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (query == null || query.length() == 0)
            throw new IllegalArgumentException("String query cannot be null or empty.");
        if (query.contains(",") && !query.startsWith(",") && !query.endsWith(",")) {
            String[] queries = query.split(",");
            ArrayList<ArrayList<String>> answer = new ArrayList<>();
            for (String q : queries) {
                answer.addAll(deduce(new Proposition(q)));
            }
            return answer;
        }
        return deduce(new Proposition(query));
    }

    public <G> G deduce(Proposition query, G returnType) {
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

        Proposition[] kbPropositions = this.getKnowledgeBasePropositions();
        String[] kbConversions = new String[this.knowledgeBase.length];
        for (int i = 0; i < this.knowledgeBase.length; i++) {
            kbConversions[i] = kbPropositions[i].getConvertedExpression();
        }

        ArrayList<ArrayList<String>> deductionPaths = new ArrayList<>();
        InferenceLaws inferences = new InferenceLaws();
        EquivalencyLaws equivalencies = new EquivalencyLaws();
        
        DirectedDeductionGraph dg = new DirectedDeductionGraph(this.getKnowledgeBaseExpressions(), query);

        return bidirectionalIterativeDeepeningSearch(dg);

    }

    private <G> G bidirectionalIterativeDeepeningSearch(DirectedDeductionGraph graph, G returnType) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        InferenceLaws inferences = new InferenceLaws();
        EquivalencyLaws equivalencies = new EquivalencyLaws();

        Argument<Model> knowledgeHistory = new Argument<>(this.knowledgeBase); // serves as knowledge history container
        ArrayList<DeductionGraphNode> leafs = graph.getNodes();
        ArrayList<ArrayList<String>> optimalPaths;
        int depth = 2;
        while (true) {
            Map<String, ArrayList<String>> inferenceMap = new HashMap<>();
            Map<String, ArrayList<String>> equivalencyMap = new HashMap<>();
            for (DeductionGraphNode leaf : leafs) {
                inferenceMap = inferences.checkInferenceLaws(knowledgeHistory);
                equivalencyMap = equivalencies.checkEquivalencyLaws(new Proposition(leaf.getExpression()));

                for (String law : equivalencyMap.keySet()) {
                    if (equivalencyMap.get(law) == null) {
                        break;
                    } else {
                        String[] premises = equivalencyMap.get(law)
                        String conversion = equivalencyMap.get(law)
                    }
                }
            }
            for (DeductionGraphNode node : graph.getNodes()) {
                if (knowledgeHistory)
            }
            leafs = graph.getLeafNodes();
            System.gc();
        }
        /**
         * 
            1. Initialize the `DeductionGraph` with root nodes from the knowledge base and a detached node for the query.
            2. Initialize a variable `depth` to 1.
            3. Initialize a variable `mostRelevantLeaf` to null.
            4. While a path from a root node to the query node has not been found:
            1. For each node in the graph at the current `depth`:
                1. Apply all applicable inference laws, considering the given roots and the current children nodes stored in the graph.
                2. Apply all applicable equivalency laws to the node.
                3. For each resulting sentence from the inference and equivalency laws:
                    1. Create a new node for the sentence and add it as a child of the current node.
                    2. If the sentence is an argumentative inference, draw a pointer from the used nodes to the new node.
                    3. If the new node is more relevant than `mostRelevantLeaf` (according to some relevance metric), update `mostRelevantLeaf`.
            2. If no new nodes were added in the last iteration, wait for 10 seconds and then continue. If still no new nodes are added, increment 
            the `depth` by 1 and return to `mostRelevantLeaf` before moving onto other nodes.
            5. If a path has been found, return the path. If not, return an indication that no path could be found.
            */
        return deductionReturnType(returnType);
    }

    private <G> G deductionReturnType(G type) {
        if (type == null)
            throw new IllegalArgumentException("Type cannot be null.");
        else if (!(type instanceof DirectedDeductionGraph) || !(type instanceof ArrayList))
            throw new IllegalArgumentException(
                    "Type must be a DirectedDeductionGraph or an ArrayList<ArrayList<String>>.");
        else if (type instanceof ArrayList) {
            ArrayList<?> list = (ArrayList<?>) type;
            if (!list.isEmpty()) {
                Object item = list.get(0);
                if (item instanceof ArrayList) {
                    ArrayList<?> innerList = (ArrayList<?>) item;
                    if (!innerList.isEmpty()) {
                        Object innerItem = innerList.get(0);
                        if (!(innerItem instanceof String)) {
                            throw new IllegalArgumentException(
                                    "Type must be a DirectedDeductionGraph or an ArrayList<ArrayList<String>>.");
                        } else {
                            System.gc();
                        }
                    }
                } else {
                    throw new IllegalArgumentException(
                            "Type must be a DirectedDeductionGraph or an ArrayList<ArrayList<String>>.");
                }
            }
        }

        G returnType = type;
        return returnType;
    }

    public void setKnowledgeBase(M[] knowledgeBase)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        validateKnowledgeBase(knowledgeBase);
        setTruthTable();
    }

    public void addKnowledgeModel(M model) {
        Object[] newKB = new Object[this.knowledgeBase.length + 1];
        newKB = getKnowledgeBaseModels();
        newKB[this.knowledgeBase.length + 1] = model;
        this.knowledgeBase = (M[]) newKB;
    }

    public int getKnowledgeBaseSize() {
        return this.knowledgeBase.length;
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

    public M[] getKnowledgeBaseModels() {
        return this.knowledgeBase;
    }

    public String[] getKnowledgeBaseExpressions() {
        String[] expressions = new String[this.knowledgeBase.length];
        for (int i = 0; i < this.knowledgeBase.length; i++) {
            expressions[i] = this.knowledgeBase[i].getExpression();
        }
        return expressions;
    }

    public Proposition[] getKnowledgeBasePropositions() {
        Proposition[] propositions = new Proposition[this.knowledgeBase.length];
        for (int i = 0; i < this.knowledgeBase.length; i++) {
            propositions[i] = this.knowledgeBase[i].getProposition();
        }
        return propositions;
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

    /* Used for constructing argumentative inference */
    static class InferenceLaws {

        private static final Map<String, ArrayList<String>> answerTemplate = new HashMap<>() {
            {
                put("Modus Ponens", null);
                put("Modus Tollens", null);
                put("Addition", null);
                put("Simplification", null);
                put("Conjunction", null);
                put("Hypothetical Syllogism", null);
                put("Disjunctive Syllogism", null);
                put("Resolution", null);
            }
        };

        public InferenceLaws() {
            super();
        }

        /**
         * 
         * @param arg an Argument object containing given knowledge base and each successive deduction; knowledge history
         * @return Maps inference law to list of strings, with each string containing [applied premises] and resulting {conclusion}.
         */
        public Map<String, ArrayList<String>> checkInferenceLaws(Argument<Model> a) {
            Proposition[] kbPropositions = a.getKnowledgeBasePropositions();
            String[] kbConversions = new String[kbPropositions.length]
            for (int i = 0; i < kbPropositions.length; i++) {
                kbConversions[i] = kbPropositions[i].getConvertedExpression();
            }

            Map<String, ArrayList<String>> answerSet = answerTemplate;
            Map<Character, String> subExpressions = new HashMap<>();
            ArrayList<String> returnedLawExpressions = new ArrayList<>();
            boolean foundLaw = false;
            for (String law : answerSet.keySet()) {
                subExpressions.clear();
                returnedLawExpressions.clear();
                foundLaw = false;
            }
            
            return answerSet;
        }

        private ArrayList<Map<Character, String>> subexpressionAbstraction(String law, String[] cE) {
            Character[] lawOperators;
            Character[] lawOperands;
            Character[][] premiseOperands;
            ArrayList<Map<Character, String>> abstractions = null;
            switch (law) {
                case "Modus Ponens": {
                    for (String conversion : kbConversions) {
                        if (conversion.contains("m")) {
                            foundLaw = true;
                            break;
                        }
                    }
                    if (foundLaw) {

                    }
                }
                case "Modus Tollens": {

                }
                case "Addition": {

                }
                case "Simplification": {

                }
                case "Conjunction": {

                }
                case "Hypothetical Syllogism": {

                }
                case "Disjunctive Syllogism": {

                }
                case "Resolution": {

                }
                default: {
                    return null;
                }
            }
            return abstractions;
        }

        private String findMatchingSubstring(String cE, String operator) {
            String[] cESubstrings = cE.split(operator);

            if (cESubstrings.length < 2) {
                return null;
            }

            for (int i = 0; i < cESubstrings.length; i++) {
                for (int j = cESubstrings.length; j > 0; j--) {
                    if (j == i)
                        break;
                    if (cESubstrings[i].equals(cESubstrings[j]))
                        return cESubstrings[i];
                }
            }

            return null;
        }

        /* Rules of Argument Inference */

        /**
         * Rule: [P], [P->Q] entails {Q}
         * 
         * @param cE
         * @return Rule string
         */
        private String modusPonens(String[] premises) {
            String conclusion = null;
            if ((premises[0].contains("PmQ") && premises[1].contains("QmR")) ||
                    (premises[0].contains("QmR") && premises[1].contains("PmQ"))) {
                conclusion = "PmR";
            }

            return conclusion;
        }

        /**
         * Rule: [~Q], [P->Q] entails {~P}
         * 
         * @param cE
         * @return Rule string
         */
        private String modusTollens(String[] premises) {
            String conclusion = null;
            if ((premises[0].contains("nQ") && premises[1].contains("PmQ")) ||
                    (premises[0].contains("PmQ") && premises[1].contains("nQ"))) {
                conclusion = "nP";
            }

            return conclusion;
        }

        /**
         * Rule: [P] entails {P|Q}
         * 
         * @param cE
         * @return Rule string
         */
        private String addition(String premise) {
            String conclusion = null;
            if (premise == "P") 
                conclusion = "PoQ";
            else if (premise == "Q")
                conclusion = "QoP";

            return conclusion;
        }

        /**
         * Rule: [P&Q] entails {P}
         * 
         * @param cE
         * @return Rule string
         */
        private String simplification(String[] premises) {
            String conclusion = null;
            if (premises[0] == "PaQ")
                conclusion = "P";
            else if (premises[0] == "QaP") 
                conclusion = "Q";

            return conclusion;
        }

        /**
         * Rule: [P], [Q] entails {P&Q}
         * 
         * @param cE
         * @return Rule string
         */
        private String conjunction(String[] premises) {
            String conclusion = null;
            if (premises[0] == "P" && premises[1] == "Q")
                conclusion = "PaQ";
            else if (premises[0] == "Q" && premises[1] == "P")
                conclusion = "QaP";

            return conclusion;
        }

        /**
         * Rule: [P->Q], [Q->R] entails {P->R}
         * 
         * @param cE
         * @return Rule string
         */
        private String hypotheticalSyllogism(String[] premises) {
            String conclusion = null;
            if ((premises[0] == "PmQ" && premises[1] == "QmR") || (premises[0] == "QmR" && premises[1] == "PmQ"))
                conclusion = "PmR";
            else if ((premises[0] == "QmR" && premises[1] == "RmP") || (premises[0] == "RmP" && premises[1] == "QmR"))
                conclusion = "QmP";
            else if ((premises[0] == "RmP" && premises[1] == "PmQ") || (premises[0] == "PmQ" && premises[1] == "RmP"))
                conclusion = "RmQ";

            return conclusion;
        }

        /**
         * Rule: [P|Q], [~P] entails {Q}
         * 
         * @param cE
         * @return Rule string
         */
        private String disjunctiveSyllogism(String[] premises) {
            String conclusion = null;
            if ((premises[0] == "PoQ" && premises[1] == "nP") ||
                    (premises[0] == "nP" && premises[1] == "PoQ")) {
                conclusion = "Q";
            } else if ((premises[0] == "QoP" && premises[1] == "nQ") ||
                    (premises[0] == "nQ" && premises[1] == "QoP")) {
                conclusion = "P";
            }

            return conclusion;
        }

        /**
         * Rule: [P|Q], [~P|R] entails {Q|R}
         * this one is powerful
         * 
         * @param cE
         * @return Rule string
         */
        private String resolution(String[] premises) {
            String conclusion = null;
            if ((premises[0].contains("PoQ") && premises[1].contains("nPoR")) ||
                    (premises[0].contains("nPoR") && premises[1].contains("PoQ"))) {
                conclusion = "QoR";
            }

            return conclusion;
        }
    }

    /**
     * Popular logic equivalencies used for inference and argumentation;
     * propositional logic inference
     */
    static class EquivalencyLaws {

        private static final Map<String, ArrayList<String>> answerTemplate = new HashMap<>() {
            {
                put("Idempotent Law", null);
                put("Associative Law", null);
                put("Commutative Law", null);
                put("Distributive Law", null);
                put("Identity Law", null);
                put("Dominant Law", null);
                put("Double Negation Law", null);
                put("Complement Law", null);
                put("DeMorgan's Law", null);
                put("Absorption Law", null);
                put("Conditional Identity", null);
            }
        };

        public EquivalencyLaws() {
            super();
        }

        /**
         * 
         * @param p A single propositional expression
         * @return Maps equivalency to list strings, with each string containing
         *         [applied expression] and resulting {conversion}.
         */
        public Map<String, ArrayList<String>> checkEquivalencyLaws(Proposition p) {
            // parse cE and store similar substrings (if any) into variables to then
            // construct applicable rules. This will use key-value mapping, and then
            // using those mapped values to evaluate applicable rules, i.e. P|Q is P,
            // therefore (P|Q)|(P|Q) => P|Q or, alternatively, P|P => P.
            // also check if there are any operand propositional equivalencies as well.

            /*
             * With Propoposition p as the root/reference value and For each law,
             * First, cycle through cE and check if there are any operand equivalencies. If
             * so, evaluate and store result in applicableLaws.
             * Second, cycle through cE and check for similar substrings, and map them to
             * operand if found (relative to law operand format).
             * Third, new_cE = cE, and replace similar substrings in new_cE with mapped
             * operands.
             * Fourth, evaluate and store result in answerSet.
             * Repeat steps until possible evaluations is exhausted.
             * Fifth, return answerSet.
             * 
             * ## REMEMBER: Do not store repeated evaluations in answerSet, i.e.
             */
            String cE = p.getConvertedExpression(); // easier to work with
            Map<String, ArrayList<Map<Character, String>>> answerAbstractions = subexpressionAbstraction(cE);
            for (String key : answerTemplate.keySet())
                answerAbstractions.put(key, null);
            // This will have to be refactored; for each law check for each abstraction is flawed logically
            for (String law : answerAbstractions.keySet()) {
                if (cE.length() == 1) {

                } else {
                        for (Map<Character, String> abstraction : answerAbstractions.values()) {
                            String abstracted_cE = cE;
                            for (Map.Entry<Character, String> entry : abstraction.entrySet()) {
                                abstracted_cE = abstracted_cE.replace(entry.getValue(), entry.getKey().toString());
                            }
                            ArrayList<String> new_cE = new ArrayList<>();
                            switch () {
                                case "Idempotent Law": {
                                    new_cE.add(idempotentLaw(abstracted_cE).replace("P", abstraction.get('P')));
                                }
                                case "Associative Law": {
                                    new_cE.add(associativeLaw(abstracted_cE).replace("P", abstraction.get('P'))
                                            .replace("Q", abstraction.get('Q'))
                                            .replace("R", abstraction.get('R')));
                                }
                                case "Commutative Law": {
                                    new_cE.add(commutativeLaw(abstracted_cE).replace("P", abstraction.get('P'))
                                            .replace("Q", abstraction.get('Q')));
                                }
                                case "Distributive Law": {
                                    new_cE.add(distributiveLaw(abstracted_cE).replace("P", abstraction.get('P'))
                                            .replace("Q", abstraction.get('Q'))
                                            .replace("R", abstraction.get('R')));
                                }
                                case "Identity Law": {
                                    new_cE.add(identityLaw(abstracted_cE).replace("P", abstraction.get('P'))
                                            .replace("T", abstraction.get('T'))
                                            .replace("F", abstraction.get('F')));
                                }
                                case "Domination Law": {
                                    new_cE.add(dominationLaw(abstracted_cE).replace("P", abstraction.get('P'))
                                            .replace("T", abstraction.get('T'))
                                            .replace("F", abstraction.get('F')));
                                }
                                case "Double Negation Law": {
                                    new_cE.add(doubleNegationLaw(abstracted_cE).replace("P", abstraction.get('P')));
                                }
                                case "Complement Law": {
                                    new_cE.add(complementLaw(abstracted_cE).replace("P", abstraction.get('P'))
                                            .replace("T", abstraction.get('T'))
                                            .replace("F", abstraction.get('F')));
                                }
                                case "DeMorgan's Law": {
                                    new_cE.add(deMorgansLaw(abstracted_cE).replace("P", abstraction.get('P'))
                                            .replace("Q", abstraction.get('Q')));
                                }
                                case "Absorption Law": {
                                    new_cE.add(absorptionLaw(abstracted_cE).replace("P", abstraction.get('P'))
                                            .replace("Q", abstraction.get('Q')));
                                }
                                case "Conditional Identity": {
                                    new_cE.add(conditionalIdentity(abstracted_cE).replace("P", abstraction.get('P'))
                                            .replace("Q", abstraction.get('Q')));
                                }
                            }
                            for (String law_cE : new_cE) {
                                if (law_cE == null) {
                                    continue;
                                } else {

                                }
                            }
                            abstractions.remove(abstraction);
                            if (abstractions.isEmpty()) {
                                answerSet.put(law, law_cE_collection);
                                System.gc();
                            }
                        }

                }
                Map<String, ArrayList<String>> answerSet = answerTemplate;

            return answerSet;
        }

        private Map<String, ArrayList<Map<Character, String>>> subexpressionAbstraction(String cE) {
            Map<String, ArrayList<Map<Character, String>>> abstractionLawMap = new HashMap<>();
            for (String law : answerTemplate.keySet()) 
                abstractionLawMap.put(law, null);

            Character[] lawOperators = new Character[5];
            lawOperators[0] = 'o';
            lawOperators[1] = 'a';
            lawOperators[2] = 'n';
            lawOperators[3] = 'm';
            lawOperators[4] = 'i';
            Character[] lawOperands = new Character[5];
            lawOperands[0] = 'P';
            lawOperands[1] = 'Q';
            lawOperands[2] = 'R';
            lawOperands[3] = 'T';
            lawOperands[4] = 'F';
            Character[] expressionOperands = findOperands(cE);

            for (String law : abstractionLawMap.keySet()) {
                if (cE.length() > 1) {
                    ArrayList<Map<Character, String>> abstractions = null;
                    switch (law) {
                        case "Idempotent Law": {
                            if (cE.contains("o")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[0]);
                                if (matchingSubstring != null) {

                                }
                            }
                            if (cE.contains("a")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[1]);
                                if (matchingSubstring != null) {

                                }
                            }
                        }
                        case "Associative Law": {
                            if (cE.contains("o")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[0]);
                                if (matchingSubstring != null) {

                                }
                            }
                            if (cE.contains("a")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[1]);
                                if (matchingSubstring != null) {

                                }
                            }
                            break;
                        }
                        case "Commutative Law": {
                            if (cE.contains("o")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[0]);
                                if (matchingSubstring != null) {

                                }
                            }
                            if (cE.contains("a")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[1]);
                                if (matchingSubstring != null) {

                                }
                            }
                            break;
                        }
                        case "Distributive Law": {
                            if (cE.contains("o")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[0]);
                                if (matchingSubstring != null) {

                                }
                            }
                            if (cE.contains("a")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[1]);
                                if (matchingSubstring != null) {

                                }
                            }
                            break;
                        }
                        case "Identity Law": {
                            if (cE.contains("o")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[0]);
                                if (matchingSubstring != null) {

                                }
                            }
                            if (cE.contains("a")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[1]);
                                if (matchingSubstring != null) {

                                }
                            }
                            break;
                        }
                        case "Dominant Law": {
                            if (cE.contains("o")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[0]);
                                if (matchingSubstring != null) {

                                }
                            }
                            if (cE.contains("a")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[1]);
                                if (matchingSubstring != null) {

                                }
                            }
                            break;
                        }
                        case "Double Negation Law": {
                            if (cE.contains("nn")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[0]);
                                if (matchingSubstring != null) {

                                }
                            }
                            break;
                        }
                        case "Complement Law": {
                            if (cE.contains("o") && cE.contains("n")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[0]);
                                if (matchingSubstring != null) {

                                }
                            }
                            if (cE.contains("a") && cE.contains("n")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[1]);
                                if (matchingSubstring != null) {

                                }
                            }
                            if (cE.contains("n")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[2]);
                                if (matchingSubstring != null) {

                                }
                            }
                            break;
                        }
                        case "DeMorgan's Law": {
                            if (cE.contains("o")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[0]);
                                if (matchingSubstring != null) {

                                }
                            }
                            if (cE.contains("a")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[1]);
                                if (matchingSubstring != null) {

                                }
                            }
                            if (cE.contains("n")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[2]);
                                if (matchingSubstring != null) {

                                }
                            }
                            break;
                        }
                        case "Absorption Law": {
                            if (cE.contains("o")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[0]);
                                if (matchingSubstring != null) {

                                }
                            }
                            if (cE.contains("a")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[1]);
                                if (matchingSubstring != null) {

                                }
                            }
                            break;
                        }
                        case "Conditional Identity": {
                            if (cE.contains("o")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[0]);
                                if (matchingSubstring != null) {

                                }
                            }
                            if (cE.contains("a")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[1]);
                                if (matchingSubstring != null) {

                                }
                            }
                            if (cE.contains("n")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[2]);
                                if (matchingSubstring != null) {

                                }
                            }
                            if (cE.contains("m")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[3]);
                                if (matchingSubstring != null) {

                                }
                            }
                            if (cE.contains("i")) {
                                String matchingSubstring = findMatchingSubstring(cE, lawOperators[4]);
                                if (matchingSubstring != null) {

                                }
                            }
                            break;
                        }
                        default: {
                            return null;
                        }
                    }
                }
            }
            return abstractionLawMap;
        }

        private static String findMatchingSubstring(String cE, Character operator) {
            String[] cESubstrings = cE.split(operator.toString());

            if (cESubstrings.length < 2) {
                return null;
            }

            for (int i = 0; i < cESubstrings.length; i++) {
                for (int j = cESubstrings.length; j > 0; j--) {
                    if (j == i)
                        break;
                    if (cESubstrings[i].equals(cESubstrings[j]))
                        return cESubstrings[i];
                }
            }
            return null;
        }

        private static Character[] findOperands(String e) {
            ArrayList<Character> operands = new ArrayList<>();
            for (int i = 0; i < e.length(); i++) {
                if (Character.isUpperCase(e.charAt(i))) {
                    operands.add(e.charAt(i));
                }
            }

            return operands.toArray(new Character[operands.size()]);
        }

        /**
         * Rule: [P|P] == {P} OR [P&P] == {P}
         * 
         */
        private String idempotentLaw(String cE) {
            String law = null;
            if (cE.equals("PaP") || cE.equals("PoP"))
                law = "P";

            return law;
        }

        /**
         * Rule: [(P|Q)|R] == {P|(Q|R)} OR [(P&Q)&R] == {P&(Q&R)}
         * 
         * @param cE
         * @return
         */
        private String associativeLaw(String cE) {
            String law = null;
            if (cE.equals("(PoQ)oR"))
                law = "Po(QoR)";
            else if (cE.equals("(PaQ)aR"))
                law = "Pa(QaR)";

            if (cE.equals("Po(QoR)"))
                law = "(PoQ)oR";
            else if (cE.equals("Pa(QaR)"))
                law = "(PaQ)aR";

            return law;
        }

        /**
         * Rule: [P|Q] == {Q|P} OR [P&Q] == {Q&P}
         * 
         * @param cE
         * @return
         */
        private String commutativeLaw(String cE) {
            String law = null;
            if (cE == "PoQ" || cE == "QoP")
                law = cE.equals("PoQ") ? "QoP" : "PoQ";
            else if (cE == "PaQ" || cE == "QaP")
                law = cE.equals("PaQ") ? "QaP" : "PaQ";

            return law;
        }

        /**
         * Rule: [P|(Q&R)] == {(P|Q)&(P|R)} OR [P&(Q|R)] == {(P&Q)|(P&R)}
         * 
         * @param cE
         * @return
         */
        private String distributiveLaw(String cE) {
            String law = null;
            if (cE == "Po(QaR)")
                law = "(PoQ)a(PoR)";
            else if (cE == "Pa(QoR)")
                law = "(PaQ)o(PaR)";

            if (cE == "(PoQ)a(PoR)")
                law = "Po(QaR)";
            else if (cE == "(PaQ)o(PaR)")
                law = "Pa(QoR)";

            return law;
        }

        /**
         * Rule: [P|F] == {P} OR [P&T] == {P}
         * 
         * @param cE
         * @return
         */
        private String identityLaw(String cE) {
            String law = null;
            if (cE.equals("PoF") || cE.equals("PaT"))
                law = "P";

            return law;
        }

        /**
         * Rule: [P&F] == {F} OR [P|T] == {T}
         * 
         * @param cE
         * @return
         */
        private String dominationLaw(String cE) {
            String law = null;
            if (cE.equals("PaF"))
                law = "F";
            else if (cE.equals("PoT"))
                law = "T";

            return law;
        }

        /**
         * Rule: [~~P] == {P}
         * 
         * @param cE
         * @return
         */
        private String doubleNegationLaw(String cE) {
            String law = null;
            if (cE.equals("nnP"))
                law = "P";

            return law;
        }

        /**
         * Rule: [P&~P] == {F}, [~T] == {F} OR [P|~P] == {T}, [~F] == {T}
         * 
         * @param cE
         * @return
         */
        private String complementLaw(String cE) {
            String law = null;
            if (cE.equals("PanP") || cE.equals("nPaP"))
                law = "F";
            else if (cE.equals("nT"))
                law = "F";
            else if (cE.equals("PonP") || cE.equals("nPoP"))
                law = "T";
            else if (cE.equals("nF"))
                law = "T";

            return law;
        }

        /**
         * Rule: [~(P|Q)] == {~P&~Q} OR [~(P&Q)] == {~P|~Q}
         * 
         * @param cE
         * @return
         */
        private String deMorgansLaw(String cE) {
            String law = null;
            if (cE.equals("n(PoQ)"))
                law = "nPanQ";
            else if (cE.equals("n(PaQ)"))
                law = "nPonQ";

            if (cE.equals("nPanQ"))
                law = "n(PoQ)";
            else if (cE.equals("nPonQ"))
                law = "n(PaQ)";

            return law;
        }

        /**
         * Rule: [P|(P&Q)] == {P} OR [P&(P|Q)] == {P}
         * 
         * @param cE
         * @return
         */
        private String absorptionLaw(String cE) {
            String law = null;
            if (cE.equals("Po(PaQ)") || cE.equals("Pa(PoQ)"))
                law = "P";

            return law;
        }

        /**
         * Rule: [P->Q] == {~P|Q} OR [P<>Q] == {(P->Q)&(Q->P)}
         * 
         * @param cE
         * @return
         */
        private String conditionalIdentity(String cE) {
            String law = null;
            if (cE.equals("PmQ"))
                law = "nPoQ";
            else if (cE.equals("PiQ"))
                law = "(PmQ)a(QmP)";

            if (cE.equals("nPoQ"))
                law = "PmQ";
            else if (cE.equals("(PmQ)a(QmP)"))
                law = "PiQ";

            return law;
        }

        private String[] singleOperandEquivalencies(String law, String cE) {
            String[] equivalencies = null;
            switch (law) {
                case "Idempotent Law": {
                    if (cE == "P") {
                        equivalencies = new String[2];
                        equivalencies[0] = "PoP";
                        equivalencies[1] = "PaP";
                    }
                    break;
                }
                case "Identity Law": {
                    if (cE == "P") {
                        equivalencies = new String[2];
                        equivalencies[0] = "PoF";
                        equivalencies[1] = "PaT";
                    }
                    break;
                }
                case "Domination Law": {
                    if (cE == "F") {
                        equivalencies = new String[1];
                        equivalencies[0] = "PaF";
                    } else if (cE == "T") {
                        equivalencies = new String[1];
                        equivalencies[0] = "PoT";
                    }
                    break;
                }
                case "Double Negation Law": {
                    if (cE == "P") {
                        equivalencies = new String[1];
                        equivalencies[0] = "nnP";
                    }
                    break;
                }
                case "Complement Law": {
                    if (cE == "F") {
                        equivalencies = new String[2];
                        equivalencies[0] = "PanP";
                        equivalencies[1] = "nT";
                    } else if (cE == "T") {
                        equivalencies = new String[2];
                        equivalencies[0] = "PonP";
                        equivalencies[1] = "nF";
                    }
                    break;
                }
                case "Absorption Law": {
                    if (cE == "P") {
                        equivalencies = new String[2];
                        equivalencies[0] = "Po(PaQ)";
                        equivalencies[1] = "Pa(PoQ)";
                    }
                    break;
                }
                default: {
                    return null;
                }
            }
            return equivalencies;
        }
    }
}
