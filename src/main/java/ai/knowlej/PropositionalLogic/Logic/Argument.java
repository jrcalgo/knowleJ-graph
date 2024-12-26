package ai.knowlej.PropositionalLogic.Logic;

import java.util.*;
import java.util.regex.Pattern;

import ai.knowlej.Exceptions.InvalidExpressionException;
import ai.knowlej.Exceptions.InvalidLogicOperatorException;
import ai.knowlej.Exceptions.InvalidOperandException;

import java.util.regex.Matcher;

import ai.knowlej.DataStructures.*;
import ai.knowlej.PropositionalLogic.Models.*;

public class Argument<M extends ModelAbstract> {
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

    public void setKnowledgeBase(M[] knowledgeBase)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        validateKnowledgeBase(knowledgeBase);
        setTruthTable();
    }

    public void addKnowledgeModel(M model) {
        M[] updatedKB = Arrays.copyOf(this.knowledgeBase, this.knowledgeBase.length + 1);
        updatedKB[updatedKB.length-1] = model;
        this.knowledgeBase = updatedKB;
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
    public M getKnowledgeBaseModel(int index) {
        return this.knowledgeBase[index];
    }

    public M[] getKnowledgeBaseModels() {
        return this.knowledgeBase;
    }

    public String getKnowledgeBaseExpression(int index) {
        return this.knowledgeBase[index].getExpression();
    }

    public String[] getKnowledgeBaseExpressions() {
        String[] expressions = new String[this.knowledgeBase.length];
        for (int i = 0; i < this.knowledgeBase.length; i++) {
            expressions[i] = this.knowledgeBase[i].getExpression();
        }
        return expressions;
    }

    public Proposition getKnowledgeBaseProposition(int index) {
        return this.knowledgeBase[index].getProposition();
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

    private void validateKnowledgeBase(M[] kb) throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (kb == null || kb.length == 0)
            throw new IllegalArgumentException("Knowledge base cannot be null or empty.");

        StringBuilder operandString = new StringBuilder();
        try {
            for (M m : kb) {
                char[] modelOperands = m.getOperands();
                for (char modelOperand : modelOperands) {
                    if (operandString.indexOf(String.valueOf(modelOperand)) == -1) {
                        operandString.append(modelOperand);
                    }
                }
            }
        } catch (Exception e) {
            Proposition[] modelPropositions = new Proposition[kb.length];
            for (int j = 0; j < modelPropositions.length; j++) {
                modelPropositions[j] = new Proposition(kb[j].getExpression());
            }
            for (int j = 0; j < modelPropositions.length; j++) {
                char[] modelOperands = new char[modelPropositions.length];
                for (int i = 0; i < modelOperands.length; i++) {
                    DeterministicModel dm = new DeterministicModel("dm", modelPropositions[i]);
                    modelOperands[i] = dm.getOperands()[i];
                    if (operandString.indexOf(String.valueOf(modelOperands[i])) == -1) {
                        operandString.append(modelOperands[i]);
                    }
                }
            }
        } finally {
            if (operandString.isEmpty())
                throw new IllegalArgumentException("Knowledge base cannot be empty.");
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
        ArrayList<String> titleRow = new ArrayList<>(Arrays.asList(this.allTruthTable[0]).subList(0, boolColsCount));

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
        for (boolean modelEvaluation : modelEvaluations) rowAnswer = rowAnswer && modelEvaluation;

        return rowAnswer;
    }

    public String checkAllTTModels(String query)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (query == null || query.isEmpty())
            throw new IllegalArgumentException("String query cannot be null or empty.");
        if (query.contains(",") && !query.startsWith(",") && !query.endsWith(",")) {
            String[] queries = query.split(",");
            StringBuilder answer = new StringBuilder();
            for (String q : queries) {
                answer.append(checkAllTTModels(new Proposition(q))).append(", ");
            }
            return answer.substring(0, answer.length() - 2);
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

    /**
     * in other words, is there a valid argument to be had in the current knowledge
     * base for a query that is also in the current knowledge base
     *
     * @param kbQuery
     * @return
     * @throws InvalidExpressionException
     * @throws InvalidOperandException
     * @throws InvalidLogicOperatorException
     */
    public boolean containsValidArgument(String kbQuery)
            throws Exception, InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (kbQuery == null || kbQuery.isEmpty())
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
            throw new IllegalArgumentException("Query not found within current knowledge base.");

        return validateCurrentArgument(kbQuery);
    }

    // TODO: implement this method
    private boolean validateCurrentArgument(String kbQuery) {
        InferenceLaws<ModelAbstract> inferenceLaws = new InferenceLaws<>();
        EquivalencyLaws equivalencyLaws = new EquivalencyLaws();

        return false;
    }

    public Proof deduce(String query)
            throws Exception, InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        if (query == null || query.isEmpty())
            throw new IllegalArgumentException("String query cannot be null or empty.");
        if (query.contains(","))
            throw new IllegalArgumentException("String query cannot contain commas.");
        return this.deduce(new Proposition(query));
    }

    public Proof deduce(Proposition query)
            throws Exception, InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
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

        Proposition[] kbPropositions = this.getKnowledgeBasePropositions();
        String[] kbConversions = new String[this.knowledgeBase.length];
        for (int i = 0; i < this.knowledgeBase.length; i++) {
            kbConversions[i] = kbPropositions[i].getConvertedExpression();
        }

        HashSet<String> kbExpressionSet = new HashSet<>(Arrays.asList(this.getKnowledgeBaseExpressions()));

        DirectedDeductionGraph dg = new DirectedDeductionGraph(kbExpressionSet, query);

        return new Proof(bidirectionalIterativeKBChaining(dg));
    }

    private ArrayList<DeductionGraphNode> bidirectionalIterativeKBChaining(DirectedDeductionGraph graph)
            throws Exception, InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {

        // Initialize inference and equivalency laws
        InferenceLaws<ModelAbstract> inferenceLaws = new InferenceLaws<>();
        final String[] singleCharacterInferenceLaws = new String[] { "Addition", "Simplification" };
        EquivalencyLaws equivalencyLaws = new EquivalencyLaws();

        // Initialize knowledge histories using HashSet for efficient lookup and to prevent duplicates
        Set<String> forwardKnowledgeHistory = new HashSet<>();
        for (DeductionGraphNode node : graph.getPremiseNodes()) {
            forwardKnowledgeHistory.add(node.getExpression()); // initial node(s)
        }
        Set<String> backwardKnowledgeHistory = new HashSet<>();
        backwardKnowledgeHistory.add(graph.getQuery()); // initial node

        int chainOperations = 1;
        boolean pathExistence = false;

        final int MAX_LOOPS = 100;
        int searchLoopCount = 0;

        final int MAX_ITERATIONS = 2;
        boolean firstIteration = true;
        int iteration = 0;

        List<String[]> kbCombinations;
        String argSentence1;
        String argSentence2;

        List<DeductionGraphNode> currentNodes;
        while (true) {
            switch (chainOperations) {
                case 1: { // Forward chaining
                    // Inference evaluations
                    kbCombinations = combineKBExpressions(new ArrayList<>(forwardKnowledgeHistory));
                    for (String[] kbCombination : kbCombinations) {
                        argSentence1 = kbCombination[0];
                        argSentence2 = kbCombination[1];
                        Map<String, ArrayList<String>> inferenceMap = inferenceLaws.checkInferenceLaws(
                                new Proposition[] { new Proposition(argSentence1), new Proposition(argSentence2) });

                        if (inferenceMap != null) {
                            for (String law : inferenceMap.keySet()) {
                                if (!law.equals("Addition") && !law.equals("Simplification")) {
                                    List<String> inferences = inferenceMap.get(law);
                                    if (inferences != null) {
                                        for (String inference : inferences) {
                                            if (inference != null && !inference.isEmpty() && !forwardKnowledgeHistory.contains(inference)) {
                                                forwardKnowledgeHistory.add(inference);
                                                DeductionGraphNode newInferenceNode = graph.add(inference);
                                                graph.point(graph.getNode(argSentence1), newInferenceNode);
                                                graph.point(graph.getNode(argSentence2), newInferenceNode);
                                                graph.addForwardNode(newInferenceNode);
                                                if (inference.equals(graph.getQuery())) {
                                                    pathExistence = true;
                                                    chainOperations = 3;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (pathExistence) break;

                    // Single-character inference laws
                    currentNodes = graph.getForwardNodes();
                    for (DeductionGraphNode node : currentNodes) {
                        Map<String, ArrayList<String>> inferenceMap = inferenceLaws.checkInferenceLaws(
                                new Proposition[] { new Proposition(node.getExpression()) });

                        if (inferenceMap != null) {
                            for (String law : singleCharacterInferenceLaws) {
                                List<String> inferences = inferenceMap.get(law);
                                if (inferences != null && !inferences.isEmpty()) {
                                    for (String inference : inferences) {
                                        if (inference != null && !inference.isEmpty() && !forwardKnowledgeHistory.contains(inference)) {
                                            forwardKnowledgeHistory.add(inference);
                                            DeductionGraphNode newInferenceNode = graph.add(inference);
                                            graph.point(node, newInferenceNode);
                                            graph.addForwardNode(newInferenceNode);
                                            if (inference.equals(graph.getQuery())) {
                                                pathExistence = true;
                                                chainOperations = 3;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (pathExistence) break;

                    // Equivalency evaluations
                    currentNodes = graph.getForwardNodes();
                    List<DeductionGraphNode> newEquivalencies = new ArrayList<>();
                    for (DeductionGraphNode node : currentNodes) {
                        Map<String, ArrayList<String>> equivalencyMap =
                                equivalencyLaws.checkEquivalencyLaws(new Proposition(node.getExpression()));

                        if (equivalencyMap != null) {
                            for (String law : equivalencyMap.keySet()) {
                                List<String> equivalencies = equivalencyMap.get(law);
                                if (equivalencies != null) {
                                    for (String equivalency : equivalencies) {
                                        if (equivalency != null && !equivalency.isEmpty()
                                                && !forwardKnowledgeHistory.contains(equivalency)) {

                                            forwardKnowledgeHistory.add(equivalency);
                                            DeductionGraphNode newEquivalenceNode = graph.add(equivalency);
                                            graph.point(node, newEquivalenceNode);
                                            newEquivalencies.add(newEquivalenceNode);
                                            if (equivalency.equals(graph.getQuery())) {
                                                pathExistence = true;
                                                chainOperations = 3;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (pathExistence) break;
                    }
                    for (DeductionGraphNode equNode : newEquivalencies) {
                        graph.addForwardNode(equNode);
                    }

                    iteration++;
                    if (iteration > MAX_ITERATIONS) {
                        iteration = 0;
                        chainOperations++;
                    }
                    break;
                }
                case 2: { // Backward chaining
                    if (!firstIteration) {
                        // Inference evaluations
                        kbCombinations = combineKBExpressions(new ArrayList<>(backwardKnowledgeHistory));
                        for (String[] kbCombination : kbCombinations) {
                            argSentence1 = kbCombination[0];
                            argSentence2 = kbCombination[1];
                            Map<String, ArrayList<String>> inferenceMap = inferenceLaws.checkInferenceLaws(
                                    new Proposition[] { new Proposition(argSentence1), new Proposition(argSentence2) });

                            if (inferenceMap != null) {
                                for (String law : inferenceMap.keySet()) {
                                    if (!law.equals("Addition") && !law.equals("Simplification")) {
                                        List<String> inferences = inferenceMap.get(law);
                                        if (inferences != null) {
                                            for (String inference : inferences) {
                                                if (inference != null && !inference.isEmpty() && !backwardKnowledgeHistory.contains(inference)) {
                                                    backwardKnowledgeHistory.add(inference);
                                                    DeductionGraphNode newInferenceNode = graph.add(inference);
                                                    graph.point(graph.getNode(argSentence1), newInferenceNode);
                                                    graph.point(graph.getNode(argSentence2), newInferenceNode);
                                                    graph.addBackwardNode(newInferenceNode);
                                                    if (inference.equals(graph.getQuery())) {
                                                        pathExistence = true;
                                                        chainOperations = 3;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (pathExistence) break;

                        // Single-character inference laws
                        currentNodes = graph.getBackwardNodes();
                        for (DeductionGraphNode node : currentNodes) {
                            Map<String, ArrayList<String>> inferenceMap = inferenceLaws.checkInferenceLaws(
                                    new Proposition[] { new Proposition(node.getExpression()) });

                            if (inferenceMap != null) {
                                for (String law : singleCharacterInferenceLaws) {
                                    List<String> inferences = inferenceMap.get(law);
                                    if (inferences != null && !inferences.isEmpty()) {
                                        for (String inference : inferences) {
                                            if (inference != null && !inference.isEmpty() && !backwardKnowledgeHistory.contains(inference)) {
                                                backwardKnowledgeHistory.add(inference);
                                                DeductionGraphNode newInferenceNode = graph.add(inference);
                                                graph.point(node, newInferenceNode);
                                                graph.addBackwardNode(newInferenceNode);
                                                if (inference.equals(graph.getQuery())) {
                                                    pathExistence = true;
                                                    chainOperations = 3;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (pathExistence) break;

                        // Equivalency evaluations
                        currentNodes = graph.getBackwardNodes();
                        List<DeductionGraphNode> newEquivalencies = new ArrayList<>();
                        for (DeductionGraphNode node : currentNodes) {
                            Map<String, ArrayList<String>> equivalencyMap = equivalencyLaws.checkEquivalencyLaws(
                                    new Proposition(node.getExpression()));

                            if (equivalencyMap != null) {
                                for (String law : equivalencyMap.keySet()) {
                                    List<String> equivalencies = equivalencyMap.get(law);
                                    if (equivalencies != null) {
                                        for (String equivalency : equivalencies) {
                                            if (equivalency != null && !equivalency.isEmpty() && !backwardKnowledgeHistory.contains(equivalency)) {
                                                backwardKnowledgeHistory.add(equivalency);
                                                DeductionGraphNode newEquivalencyNode = graph.add(equivalency);
                                                graph.point(node, newEquivalencyNode);
                                                newEquivalencies.add(newEquivalencyNode);
                                                if (equivalency.equals(graph.getQuery())) {
                                                    pathExistence = true;
                                                    chainOperations = 3;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (pathExistence) break;
                        }
                        for (DeductionGraphNode equNode : newEquivalencies) {
                            graph.addBackwardNode(equNode);
                        }
                        break;
                    } else {
                        // First iteration of backward chaining
                        Map<String, ArrayList<String>> inferenceMap = inferenceLaws.checkInferenceLaws(
                                new Proposition[] { new Proposition(graph.getQuery()) });

                        if (inferenceMap != null) {
                            for (String law : inferenceMap.keySet()) {
                                List<String> inferences = inferenceMap.get(law);
                                if (inferences != null) {
                                    for (String inference : inferences) {
                                        if (inference != null && !inference.isEmpty() && !backwardKnowledgeHistory.contains(inference)) {
                                            backwardKnowledgeHistory.add(inference);
                                            DeductionGraphNode newInferenceNode = graph.add(inference);
                                            graph.point(graph.getQueryNode(), newInferenceNode);
                                            graph.addBackwardNode(newInferenceNode);
                                            if (inference.equals(graph.getQuery())) {
                                                pathExistence = true;
                                                chainOperations = 3;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (pathExistence) break;

                        currentNodes = graph.getBackwardNodes();
                        List<DeductionGraphNode> newEquivalencies = new ArrayList<>();
                        for (DeductionGraphNode node : currentNodes) {
                            Map<String, ArrayList<String>> equivalencyMap = equivalencyLaws.checkEquivalencyLaws(
                                    new Proposition(node.getExpression()));

                            if (equivalencyMap != null) {
                                for (String law : equivalencyMap.keySet()) {
                                    List<String> equivalencies = equivalencyMap.get(law);
                                    if (equivalencies != null) {
                                        for (String equivalency : equivalencies) {
                                            if (equivalency != null && !equivalency.isEmpty() && !backwardKnowledgeHistory.contains(equivalency)) {
                                                backwardKnowledgeHistory.add(equivalency);
                                                DeductionGraphNode newEquivalencyNode = graph.add(equivalency);
                                                graph.point(node, newEquivalencyNode);
                                                newEquivalencies.add(newEquivalencyNode);
                                                if (equivalency.equals(graph.getQuery())) {
                                                    pathExistence = true;
                                                    chainOperations = 3;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (pathExistence) break;
                        }
                        firstIteration = false;
                        for (DeductionGraphNode equNode : newEquivalencies) {
                            graph.addBackwardNode(equNode);
                        }
                    }
                    iteration++;
                    if (iteration >= MAX_ITERATIONS) {
                        iteration = 0;
                        chainOperations++;
                    }
                    break;
                }
                // 3: Search and return the best path found through chaining
                case 3: {
                    // Extract the only/best path using A* search
                    return graph.astarToQuery();
                }
                default:
                    // Should not reach here
                    return null;
            }
            searchLoopCount++;
            if (searchLoopCount > MAX_LOOPS) {
                // Exceeded maximum number of loops
                break;
            }
        }
        return null; // Return null if no path is found within the maximum number of loops
    }


    private ArrayList<String[]> combineKBExpressions(ArrayList<String> kb)
            throws InvalidExpressionException, InvalidOperandException, InvalidLogicOperatorException {
        ArrayList<String[]> kbCombinations = new ArrayList<>();
        for (int i = 0; i < kb.size(); i++) {
            for (int j = i + 1; j < kb.size(); j++) {
                String[] combination = new String[2];
                combination[0] = kb.get(i);
                combination[1] = kb.get(j);
                kbCombinations.add(combination);
            }
        }
        return kbCombinations;
    }

    public void printAllTrueKBModels() {
        for (ArrayList<String> trueKBModel : this.trueKBModels) {
            for (String s : trueKBModel) {
                System.out.print(s + " ");
            }
            System.out.println();
        }
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
            System.out.print(i + " ");
            System.out.print(allTruthTable[0][i] + "\s\s\s");
        }

        for (int i = 1; i < allTruthTable.length; i++) {
            for (int j = fromCol; j < toCol; j++) {
                System.out.print(allTruthTable[i][j] + "\s\s\s\s\s");
            }
            System.out.println();
        }
    }

    /* Used for constructing argumentative inference */
    static class InferenceLaws<M extends ModelAbstract> {

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

        /**
         *
         * @param kbPropositions an Argument object containing given knowledge base and each
         *            successive deduction; knowledge history
         * @return Maps inference law to list of strings, with each string containing
         *         [applied premises] and resulting {conclusion}.
         */
        public Map<String, ArrayList<String>> checkInferenceLaws(Proposition[] kbPropositions) {
            String[] kbConversions = new String[kbPropositions.length];
            for (int i = 0; i < kbPropositions.length; i++) {
                kbConversions[i] = kbPropositions[i].getConvertedExpression();
            }

            Map<String, ArrayList<String>> answerSet = answerTemplate;
            Map<String, ArrayList<Map<Character, String>>> answerEncodings = argumentEncoder(kbConversions);
            if (answerEncodings == null)
                return null;
            for (String law : answerEncodings.keySet()) {
                for (Map<Character, String> encoding : answerEncodings.get(law)) {
                    int i = 0;
                    for (Map.Entry<Character, String> entry : encoding.entrySet()) {
                            kbConversions[i] = kbConversions[i].replace(entry.getValue(), entry.getKey().toString());
                            i++;
                    }
                    ArrayList<String> answerDecodings = new ArrayList<>();
                    switch (law) {
                        case "Modus Ponens": {
                            answerDecodings.add(argumentDecoder(modusPonens(kbConversions), encoding));
                            break;
                        }
                        case "Modus Tollens": {
                            answerDecodings.add(argumentDecoder(modusTollens(kbConversions), encoding));
                            break;
                        }
                        case "Addition": {
                            if (kbConversions.length == 1)
                                answerDecodings.add(argumentDecoder(addition(kbConversions[0]), encoding));
                            break;
                        }
                        case "Simplification": {
                            if (kbConversions.length == 1)
                                answerDecodings.add(argumentDecoder(simplification(kbConversions[0]), encoding));
                            break;
                        }
                        case "Conjunction": {
                            answerDecodings.add(argumentDecoder(conjunction(kbConversions), encoding));
                            break;
                        }
                        case "Hypothetical Syllogism": {
                            answerDecodings.add(argumentDecoder(hypotheticalSyllogism(kbConversions), encoding));
                            break;
                        }
                        case "Disjunctive Syllogism": {
                            answerDecodings.add(argumentDecoder(disjunctiveSyllogism(kbConversions), encoding));
                            break;
                        }
                        case "Resolution": {
                            answerDecodings.add(argumentDecoder(resolution(kbConversions), encoding));
                            break;
                        }
                        default: {
                            break;
                        }
                    }

                    answerSet.put(law, answerDecodings);
                }
            }
            return answerSet;
        }

        private Map<String, ArrayList<Map<Character, String>>> argumentEncoder(String[] kb) {
            Map<String, ArrayList<Map<Character, String>>> encodedLawMap = new HashMap<>();
            for (String law : answerTemplate.keySet()) {
                encodedLawMap.put(law, null);
            }

            final String[] modusPonens = new String[] {
                    "^(.*)$", "^(.*)m(.*)$"
            };
            final String[] modusTollens = new String[] {
                    "^n(.*)", "^(.*)m(.*)$"
            };
            final String simplification = "^(.*)a(.*)$";
            final String[] hypotheticalSyllogism = new String[] {
                    "^(.*)m(.*)$", "^(.*)m(.*)$"
            };
            final String[] disjunctiveSyllogism = new String[] {
                    "^(.*)o(.*)$", "^n(.*)$"
            };
            final String[] resolution = new String[] {
                    "^(.*)o(.*)$", "^n(.*)o(.*)$"
            };

            final Character[] lawOperands = new Character[] {
                    'P', 'Q', 'R'
            };

            for (String law : encodedLawMap.keySet()) {
                ArrayList<Map<Character, String>> encodings = new ArrayList<>();
                if (kb.length == 2 && !Objects.equals(law, "Addition") && !Objects.equals(law, "Simplification")) {
                    switch (law) {
                        case "Modus Ponens": {
                            final String[] premise2Substrings = subdivideExpressionCharacters(kb[1], modusPonens[1]);
                            if (premise2Substrings == null)
                                break;
                            else if (premise2Substrings.length != 2)
                                break;
                            if (kb[0].equals(premise2Substrings[0]) && !kb[0].equals(premise2Substrings[1])) {
                                encodings.add(new HashMap<Character, String>() {
                                    {
                                        put(lawOperands[0], kb[0]);
                                        put(lawOperands[1], premise2Substrings[1]);
                                    }
                                });
                            }
                            break;
                        }
                        case "Modus Tollens": {
                            final String[] premise2Substrings = subdivideExpressionCharacters(kb[1], modusTollens[1]);
                            if (premise2Substrings == null)
                                break;
                            else if (premise2Substrings.length != 2)
                                break;
                            if (kb[0].equals(premise2Substrings[1]) && !kb[0].equals(premise2Substrings[0])) {
                                encodings.add(new HashMap<Character, String>() {
                                    {
                                        put(lawOperands[0], premise2Substrings[0]);
                                        put(lawOperands[1], kb[0]);
                                    }
                                });
                            }
                            break;
                        }
                        case "Simplification": {
                            for (String s : kb) {
                                final String[] premise1Substrings = subdivideExpressionCharacters(s,
                                        simplification);
                                if (premise1Substrings == null)
                                    break;
                                else if (premise1Substrings.length != 2)
                                    break;
                                if (!premise1Substrings[0].equals(premise1Substrings[1])) {
                                    encodings.add(new HashMap<Character, String>() {
                                        {
                                            put(lawOperands[0], premise1Substrings[0]);
                                            put(lawOperands[1], premise1Substrings[1]);
                                        }
                                    });
                                }
                            }
                            break;
                        }
                        case "Conjunction": {
                            if (!kb[0].equals(kb[1])) {
                                encodings.add(new HashMap<Character, String>() {
                                    {
                                        put(lawOperands[0], kb[0]);
                                        put(lawOperands[1], kb[1]);
                                    }
                                });
                            }
                            break;
                        }
                        case "Hypothetical Syllogism": {
                            final String[] premise1Substrings = subdivideExpressionCharacters(kb[0],
                                    hypotheticalSyllogism[0]);
                            final String[] premise2Substrings = subdivideExpressionCharacters(kb[1],
                                    hypotheticalSyllogism[1]);
                            if (premise1Substrings == null || premise2Substrings == null)
                                break;
                            if (!premise1Substrings[0].equals(premise1Substrings[1])
                                    && !premise1Substrings[0].equals(premise2Substrings[1])
                                    && !premise2Substrings[0].equals(premise2Substrings[1])
                                    && premise1Substrings[1].equals(premise2Substrings[0])) {
                                encodings.add(new HashMap<Character, String>() {
                                    {
                                        put(lawOperands[0], premise1Substrings[0]);
                                        put(lawOperands[1], premise1Substrings[1]);
                                        put(lawOperands[2], premise2Substrings[1]);
                                    }
                                });
                            }
                            break;
                        }
                        case "Disjunctive Syllogism": {
                            final String[] premise1Substrings = subdivideExpressionCharacters(kb[0],
                                    disjunctiveSyllogism[0]);
                            final String[] premise2Substrings = subdivideExpressionCharacters(kb[1],
                                    disjunctiveSyllogism[1]);
                            if (premise1Substrings == null || premise2Substrings == null)
                                break;
                            if (!premise1Substrings[0].equals(premise1Substrings[1])
                                    && premise1Substrings[0].equals(premise2Substrings[0])) {
                                encodings.add(new HashMap<Character, String>() {
                                    {
                                        put(lawOperands[0], premise1Substrings[0]);
                                        put(lawOperands[1], premise1Substrings[1]);
                                    }
                                });
                            }
                            break;
                        }
                        case "Resolution": {
                            final String[] premise1Substrings = subdivideExpressionCharacters(kb[0], resolution[0]);
                            final String[] premise2Substrings = subdivideExpressionCharacters(kb[1], resolution[1]);
                            if (premise1Substrings == null || premise2Substrings == null)
                                break;
                            if (!premise1Substrings[0].equals(premise1Substrings[1])
                                    && !premise1Substrings[0].equals(premise2Substrings[1])
                                    && !premise1Substrings[1].equals(premise2Substrings[1])
                                    && premise1Substrings[0].equals(premise2Substrings[0])) {
                                encodings.add(new HashMap<Character, String>() {
                                    {
                                        put(lawOperands[0], premise1Substrings[0]);
                                        put(lawOperands[1], premise1Substrings[1]);
                                        put(lawOperands[2], premise2Substrings[1]);
                                    }
                                });
                            }
                            break;
                        }
                    }
                } else if (kb.length == 1 && (Objects.equals(law, "Addition") || Objects.equals(law, "Simplification"))) { // addition law
                    switch(law) {
                        case "Addition": {

                        }
                        case "Simplification": {

                        }
                    }
                } else
                    continue;

                encodedLawMap.put(law, encodings);
            }
            // cleans up unfound encoded laws
            encodedLawMap.values().removeIf(Objects::isNull);
            if (encodedLawMap.isEmpty())
                return null;

            return encodedLawMap;
        }

        private String argumentDecoder(String kbExpression, Map<Character, String> encoding) {
            if (kbExpression == null || encoding == null)
                return null;
            String decodedExpression = kbExpression;

            for (Map.Entry<Character, String> entry : encoding.entrySet()) {
                String placeholder = Pattern.quote(entry.getKey().toString());
                String actualExpression = entry.getValue();

                if (actualExpression == null) {
                    return null;
                }

                decodedExpression = decodedExpression.replaceAll(placeholder, actualExpression);
            }

            return decodedExpression;
        }

        private String findMatchingSubstringPairs(String cE, String operator) {
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

        private String[] subdivideExpressionCharacters(String kbExpression, String regex) {
            if (kbExpression == null || regex == null)
                return null;

            ArrayList<String> kbExpressionSubstrings = new ArrayList<>();
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(kbExpression);

            while (matcher.find()) {
                for (int i = 0; i <= matcher.groupCount(); i++) {
                    String group = matcher.group(i);
                    if (group != null && !group.equals(kbExpression))
                        kbExpressionSubstrings.add(group);
                }
            }
            if (kbExpressionSubstrings.isEmpty())
                return null;

            return kbExpressionSubstrings.toArray(new String[0]);
        }

        /* Rules of Argument Inference */

        /**
         * Rule: [P], [P->Q] entails {Q}
         *
         * @param premises
         * @return Rule string
         */
        private String modusPonens(String[] premises) {
            String conclusion = null;
            if ((premises[0].equals("P") && premises[1].equals("PmQ"))
                    || (premises[0].equals("PmQ") && premises[1].equals("P")))
                conclusion = "Q";
            else if ((premises[0].equals("Q") && premises[1].equals("QmP"))
                    || (premises[0].equals("QmP") && premises[1].equals("Q")))
                conclusion = "P";

            return conclusion;
        }

        /**
         * Rule: [~Q], [P->Q] entails {~P}
         *
         * @param premises
         * @return Rule string
         */
        private String modusTollens(String[] premises) {
            String conclusion = null;
            if ((premises[0].equals("nQ") && premises[1].equals("PmQ")) ||
                    (premises[0].equals("PmQ") && premises[1].equals("nQ"))) {
                conclusion = "nP";
            } else if ((premises[0].equals("nP") && premises[1].equals("QmP")) ||
                    (premises[0].equals("QmP") && premises[1].equals("nP"))) {
                conclusion = "nQ";
            }

            return conclusion;
        }

        /**
         * Rule: [P] entails {P|Q}
         *
         * @param premise
         * @return Rule string
         */
        private String addition(String premise) {
            String conclusion = null;
            if (premise.equals("P"))
                conclusion = "PoQ";
            else if (premise.equals("Q"))
                conclusion = "QoP";

            return conclusion;
        }

        /**
         * Rule: [P&Q] entails {P}
         *
         * @param premise
         * @return Rule string
         */
        private String simplification(String premise) {
            String conclusion = null;
            if (premise.equals("PaQ"))
                conclusion = "P";
            else if (premise.equals("QaP"))
                conclusion = "Q";

            return conclusion;
        }

        /**
         * Rule: [P], [Q] entails {P&Q}
         *
         * @param premises
         * @return Rule string
         */
        private String conjunction(String[] premises) {
            String conclusion = null;
            if (premises[0].equals("P") && premises[1].equals("Q"))
                conclusion = "PaQ";
            else if (premises[0].equals("Q") && premises[1].equals("P"))
                conclusion = "QaP";

            return conclusion;
        }

        /**
         * Rule: [P->Q], [Q->R] entails {P->R}
         *
         * @param premises
         * @return Rule string
         */
        private String hypotheticalSyllogism(String[] premises) {
            String conclusion = null;
            if ((premises[0].equals("PmQ") && premises[1].equals("QmR"))
                    || (premises[0].equals("QmR") && premises[1].equals("PmQ")))
                conclusion = "PmR";
            else if ((premises[0].equals("QmR") && premises[1].equals("RmP"))
                    || (premises[0].equals("RmP") && premises[1].equals("QmR")))
                conclusion = "QmP";
            else if ((premises[0].equals("RmP") && premises[1].equals("PmQ"))
                    || (premises[0].equals("PmQ") && premises[1].equals("RmP")))
                conclusion = "RmQ";

            return conclusion;
        }

        /**
         * Rule: [P|Q], [~P] entails {Q}
         *
         * @param premises
         * @return Rule string
         */
        private String disjunctiveSyllogism(String[] premises) {
            String conclusion = null;
            if ((premises[0].equals("PoQ") && premises[1].equals("nP")) ||
                    (premises[0].equals("nP") && premises[1].equals("PoQ"))) {
                conclusion = "Q";
            } else if ((premises[0].equals("QoP") && premises[1].equals("nQ")) ||
                    (premises[0].equals("nQ") && premises[1].equals("QoP"))) {
                conclusion = "P";
            }

            return conclusion;
        }

        /**
         * Rule: [P|Q], [~P|R] entails {Q|R}
         * this one is powerful
         *
         * @param premises
         * @return Rule string
         */
        private String resolution(String[] premises) {
            String conclusion = null;
            if ((premises[0].equals("PoQ") && premises[1].equals("nPoR")) ||
                    (premises[0].equals("nPoR") && premises[1].equals("PoQ")))
                conclusion = "QoR";
            else if ((premises[0].equals("QoP") && premises[1].equals("nQoR")) ||
                    (premises[0].equals("nQoR") && premises[1].equals("QoP")))
                conclusion = "RoP";
            else if ((premises[0].equals("PoQ") && premises[1].equals("nRoP")) ||
                    (premises[0].equals("nRoP") && premises[1].equals("PoQ")))
                conclusion = "QoR";

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
            Map<String, ArrayList<String>> answerSet = answerTemplate;
            Map<String, ArrayList<Map<Character, String>>> answerEncodings = expressionEncoder(cE);
            String encoded_cE = cE;
            assert answerEncodings != null;
            for (String law : answerEncodings.keySet()) {
                if (cE.length() > 1) {
                    for (Map<Character, String> encoding : answerEncodings.get(law)) {
                        for (Map.Entry<Character, String> entry : encoding.entrySet()) {
                            encoded_cE = encoded_cE.replace(entry.getValue(), entry.getKey().toString());
                        }
                        ArrayList<String> answerDecodings = new ArrayList<>();
                        switch (law) {
                            case "Idempotent Law": {
                                answerDecodings.add(expressionDecoder(idempotentLaw(encoded_cE), encoding));
                                break;
                            }
                            case "Associative Law": {
                                answerDecodings.add(expressionDecoder(associativeLaw(encoded_cE), encoding));
                                break;
                            }
                            case "Commutative Law": {
                                answerDecodings.add(expressionDecoder(commutativeLaw(encoded_cE), encoding));
                                break;
                            }
                            case "Distributive Law": {
                                answerDecodings.add(expressionDecoder(distributiveLaw(encoded_cE), encoding));
                                break;
                            }
                            case "Identity Law": {
                                answerDecodings.add(expressionDecoder(identityLaw(encoded_cE), encoding));
                                break;
                            }
                            case "Domination Law": {
                                answerDecodings.add(expressionDecoder(dominationLaw(encoded_cE), encoding));
                                break;
                            }
                            case "Double Negation Law": {
                                answerDecodings.add(expressionDecoder(doubleNegationLaw(encoded_cE), encoding));
                                break;
                            }
                            case "Complement Law": {
                                answerDecodings.add(expressionDecoder(complementLaw(encoded_cE), encoding));
                                break;
                            }
                            case "DeMorgan's Law": {
                                answerDecodings.add(expressionDecoder(deMorgansLaw(encoded_cE), encoding));
                                break;
                            }
                            case "Absorption Law": {
                                answerDecodings.add(expressionDecoder(absorptionLaw(encoded_cE), encoding));
                                break;
                            }
                            case "Conditional Identity": {
                                answerDecodings.add(expressionDecoder(conditionalIdentity(encoded_cE), encoding));
                                break;
                            }
                            default: {
                                break;
                            }
                        }

                        answerSet.put(law, answerDecodings);

                    }
                }
            }

            return answerSet;
        }

        private Map<String, ArrayList<Map<Character, String>>> expressionEncoder(String cE) {
            Map<String, ArrayList<Map<Character, String>>> encodedLawMap = new HashMap<>();
            for (String law : answerTemplate.keySet()) {
                encodedLawMap.put(law, null);
            }

            final String[] idempotentLaw = new String[] {
                    "^(.*)o(.*)$", "^(.*)a(.*)$"
            };
            final String[] associativeLaw = new String[] {
                    "^((.*)o(.*))o(.*)$", "^(.*)o((.*)o(.*))$", "^((.*)a(.*))a(.*)$", "^(.*)a((.*)a(.*))$"
            };
            final String[] commutativeLaw = new String[] {
                    "^(.*)o(.*)$", "^(.*)a(.*)$"
            };
            final String[] distributiveLaw = new String[] {
                    "^(.*)o((.*)a(.*))$", "^((.*)o(.*))a((.*)o(.*))$", "^(.*)a((.*)o(.*))$", "^((.*)a(.*))o((.*)a(.*))$"
            };
            final String[] identityLaw = new String[] {
                    "^(.*)oF$", "^(.*)aT$", "^Fo(.*)$", "^Ta(.*)$"
            };
            final String[] dominationLaw = new String[] {
                    "^(.*)aF$", "^(.*)oT$", "^Fa(.*)$", "^To(.*)$"
            };
            final String[] complementLaw = new String[] {
                    "^(.*)an(.*)$", "^(.*)on(.*)$"
            };
            final String[] deMorgansLaw = new String[] {
                    "^n((.*)o(.*))$", "^n(.*)an(.*)$", "^n((.*)a(.*))$", "^n(.*)on(.*)$"
            };
            final String[] absorptionLaw = new String[] {
                    "^(.*)o((.*)a(.*))", "^(.*)a((.*)o(.*))$"
            };
            final String[] conditionalIdentity = new String[] {
                    "^(.*)m(.*)$", "^n(.*)o(.*)$", "^(.*)i(.*)$", "^((.*)m(.*))a((.*)m(.*))$"
            };

            final Character[] lawOperands = new Character[] {
                    'P', 'Q', 'R', 'T', 'F'
            };

            final int j = 0;
            final int sentence = j;
            for (String law : encodedLawMap.keySet()) {
                if (cE.length() > 1) {
                    ArrayList<Map<Character, String>> encodings = new ArrayList<>();
                    switch (law) {
                        case "Idempotent Law": {
                            for (String law_str : idempotentLaw) {
                                if (cE.matches(law_str)) {
                                    final String[] cESubstrings = subdivideExpressionCharacters(cE, law_str);
                                    if (cESubstrings[j].equals(cESubstrings[j + 1])) {
                                        encodings.add(new HashMap<Character, String>() {
                                            {
                                                put(lawOperands[0], cESubstrings[sentence]);
                                            }
                                        });
                                    }
                                }
                            }
                            break;
                        }
                        case "Associative Law": {
                            for (String law_str : associativeLaw) {
                                if (cE.matches(law_str)) {
                                    final String[] cESubstrings = subdivideExpressionCharacters(cE, law_str);
                                    if (!cESubstrings[j].equals(cESubstrings[j + 1])
                                            && !cESubstrings[j].equals(cESubstrings[j + 2])
                                            && !cESubstrings[j + 1].equals(cESubstrings[j + 2])) {
                                        encodings.add(new HashMap<Character, String>() {
                                            {
                                                put(lawOperands[0], cESubstrings[sentence]);
                                                put(lawOperands[1], cESubstrings[sentence + 1]);
                                                put(lawOperands[2], cESubstrings[sentence + 2]);
                                            }
                                        });
                                    }
                                }
                            }
                            break;
                        }
                        case "Commutative Law": {
                            for (String law_str : commutativeLaw) {
                                if (cE.matches(law_str)) {
                                    final String[] cESubstrings = subdivideExpressionCharacters(cE, law_str);
                                    if (!cESubstrings[j].equals(cESubstrings[j + 1])) {
                                        encodings.add(new HashMap<Character, String>() {
                                            {
                                                put(lawOperands[0], cESubstrings[sentence]);
                                                put(lawOperands[1], cESubstrings[sentence + 1]);
                                            }
                                        });
                                    }
                                }
                            }
                            break;
                        }
                        case "Distributive Law": {
                            for (int i = 0; i < distributiveLaw.length; i++) {
                                if (cE.matches(distributiveLaw[i])) {
                                    final String[] cESubstrings = subdivideExpressionCharacters(cE, distributiveLaw[i]);
                                    if (i == 0 || i == 2) {
                                        if (!cESubstrings[j].equals(cESubstrings[j + 1])
                                                && !cESubstrings[j].equals(cESubstrings[j + 2])
                                                && !cESubstrings[j + 1].equals(cESubstrings[j + 2])) {
                                            encodings.add(new HashMap<Character, String>() {
                                                {
                                                    put(lawOperands[0], cESubstrings[sentence]);
                                                    put(lawOperands[1], cESubstrings[sentence + 1]);
                                                    put(lawOperands[2], cESubstrings[sentence + 2]);
                                                }
                                            });
                                        }
                                    } else if ((i == 1 || i == 3) && findMatchingSubstringPairs(cESubstrings)) {
                                        if (cESubstrings[j].equals(cESubstrings[j + 2])
                                                && !cESubstrings[j + 1].equals(cESubstrings[j + 3])) {
                                            encodings.add(new HashMap<Character, String>() {
                                                {
                                                    put(lawOperands[0], cESubstrings[sentence]);
                                                    put(lawOperands[1], cESubstrings[sentence + 1]);
                                                    put(lawOperands[2], cESubstrings[sentence + 3]);
                                                }
                                            });
                                        } else if (cESubstrings[j + 1].equals(cESubstrings[j + 2])
                                                && !cESubstrings[j].equals(cESubstrings[j + 3])) {
                                            encodings.add(new HashMap<Character, String>() {
                                                {
                                                    put(lawOperands[0], cESubstrings[sentence + 1]);
                                                    put(lawOperands[1], cESubstrings[sentence]);
                                                    put(lawOperands[2], cESubstrings[sentence + 3]);
                                                }
                                            });
                                        } else if (cESubstrings[j].equals(cESubstrings[j + 3])
                                                && !cESubstrings[j + 1].equals(cESubstrings[j + 2])) {
                                            encodings.add(new HashMap<Character, String>() {
                                                {
                                                    put(lawOperands[0], cESubstrings[sentence]);
                                                    put(lawOperands[1], cESubstrings[sentence + 1]);
                                                    put(lawOperands[2], cESubstrings[sentence + 2]);
                                                }
                                            });
                                        } else if (cESubstrings[j + 1].equals(cESubstrings[j + 3])
                                                && !cESubstrings[j].equals(cESubstrings[j + 2])) {
                                            encodings.add(new HashMap<Character, String>() {
                                                {
                                                    put(lawOperands[0], cESubstrings[sentence + 1]);
                                                    put(lawOperands[1], cESubstrings[sentence]);
                                                    put(lawOperands[2], cESubstrings[sentence + 2]);
                                                }
                                            });
                                        }

                                    }
                                }
                            }
                            break;
                        }
                        case "Identity Law": {
                            for (String law_str : identityLaw) {
                                if (cE.matches(law_str)) {
                                    final String[] cESubstrings = subdivideExpressionCharacters(cE, law_str);
                                    encodings.add(new HashMap<Character, String>() {
                                        {
                                            put(lawOperands[0], cESubstrings[0]);
                                        }
                                    });
                                }
                            }
                            break;
                        }
                        case "Domination Law": {
                            for (String law_str : dominationLaw) {
                                if (cE.matches(law_str)) {
                                    final String[] cESubstrings = subdivideExpressionCharacters(cE, law_str);
                                    encodings.add(new HashMap<Character, String>() {
                                        {
                                            put(lawOperands[0], cESubstrings[0]);
                                        }
                                    });
                                }
                            }
                            break;
                        }
                        case "Complement Law": {
                            for (String law_str : complementLaw) {
                                if (cE.matches(law_str)) {
                                    final String[] cESubstrings = subdivideExpressionCharacters(cE, law_str);
                                    if (cESubstrings[j].equals(cESubstrings[j + 1])) {
                                        encodings.add(new HashMap<Character, String>() {
                                            {
                                                put(lawOperands[0], cESubstrings[sentence]);
                                            }
                                        });
                                    }
                                }
                            }
                            break;
                        }
                        case "DeMorgan's Law": {
                            for (String law_str : deMorgansLaw) {
                                if (cE.matches(law_str)) {
                                    final String[] cESubstrings = subdivideExpressionCharacters(cE, law_str);
                                    if (!cESubstrings[j].equals(cESubstrings[j + 1])) {
                                        encodings.add(new HashMap<Character, String>() {
                                            {
                                                put(lawOperands[0], cESubstrings[sentence]);
                                                put(lawOperands[1], cESubstrings[sentence + 1]);
                                            }
                                        });
                                    }
                                }
                            }
                            break;
                        }
                        case "Absorption Law": {
                            for (String law_str : absorptionLaw) {
                                if (cE.matches(law_str)) {
                                    final String[] cESubstrings = subdivideExpressionCharacters(cE, law_str);
                                    if (cESubstrings[j].equals(cESubstrings[j + 1])
                                            && !cESubstrings[j + 2].equals(cESubstrings[j])) {
                                        encodings.add(new HashMap<Character, String>() {
                                            {
                                                put(lawOperands[0], cESubstrings[sentence]);
                                                put(lawOperands[1], cESubstrings[sentence + 2]);
                                            }
                                        });
                                    } else if (cESubstrings[j].equals(cESubstrings[j + 2])
                                            && !cESubstrings[j + 1].equals(cESubstrings[j])) {
                                        encodings.add(new HashMap<Character, String>() {
                                            {
                                                put(lawOperands[0], cESubstrings[sentence]);
                                                put(lawOperands[1], cESubstrings[sentence + 1]);
                                            }
                                        });
                                    }

                                }
                            }
                            break;
                        }
                        case "Conditional Identity": {
                            for (int i = 0; i < conditionalIdentity.length; i++) {
                                if (cE.matches(conditionalIdentity[i])) {
                                    final String[] cESubstrings = subdivideExpressionCharacters(cE,
                                            conditionalIdentity[i]);
                                    if (i <= 2) {
                                        if (!cESubstrings[j].equals(cESubstrings[j + 1])) {
                                            encodings.add(new HashMap<Character, String>() {
                                                {
                                                    put(lawOperands[0], cESubstrings[sentence]);
                                                    put(lawOperands[1], cESubstrings[sentence + 1]);
                                                }
                                            });
                                        }
                                    } else {
                                        if (cESubstrings[j].equals(cESubstrings[j + 3])
                                                && cESubstrings[j + 1].equals(cESubstrings[j + 2])
                                                && !cESubstrings[j].equals(cESubstrings[j + 1])) {
                                            encodings.add(new HashMap<Character, String>() {
                                                {
                                                    put(lawOperands[0], cESubstrings[sentence]);
                                                    put(lawOperands[1], cESubstrings[sentence + 1]);
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }
                            break;
                    }
                    encodedLawMap.put(law, encodings);
                }
            }

            encodedLawMap.values().removeIf(Objects::isNull);
            if (encodedLawMap.isEmpty())
                return null;

            return encodedLawMap;
        }

        private String expressionDecoder(String cE, Map<Character, String> encodings) {
            if (cE == null || encodings == null)
                return null;
            String decodedExpression = cE;

            for (Map.Entry<Character, String> entry : encodings.entrySet()) {
                String placeholder = Pattern.quote(entry.getKey().toString());
                String actualExpression = entry.getValue();

                if (actualExpression == null) {
                    return null;
                }

                decodedExpression = decodedExpression.replaceAll(placeholder, actualExpression);
            }

            return decodedExpression;
        }

        private boolean findMatchingSubstringPairs(String[] substrings) {
            if (substrings == null)
                throw new IllegalArgumentException();

            for (int i = 0; i < substrings.length; i++) {
                for (int j = substrings.length-1; j >= 0; j--) {
                    if (j == i) continue;
                    if (substrings[i].equals(substrings[j]))
                        return true;
                }
            }
            return false;
        }

        private static String[] subdivideExpressionCharacters(String cE, String regex) {
            if (cE == null || regex == null)
                return null;

            ArrayList<String> cESubstrings = new ArrayList<>();
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(cE);

            while (matcher.find()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    cESubstrings.add(matcher.group(i));
                }
            }

            return cESubstrings.toArray(new String[cESubstrings.size()]);
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
            if (cE.equals("PoQ") || cE.equals("QoP"))
                law = cE.equals("PoQ") ? "QoP" : "PoQ";
            else if (cE.equals("PaQ") || cE.equals("QaP"))
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
            if (cE.equals("Po(QaR)"))
                law = "(PoQ)a(PoR)";
            else if (cE.equals("Pa(QoR)"))
                law = "(PaQ)o(PaR)";

            if (cE.equals("(PoQ)a(PoR)"))
                law = "Po(QaR)";
            else if (cE.equals("(PaQ)o(PaR)"))
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
            if (cE.length() > 1) {
                return null;
            }

            String[] equivalencies = null;
            switch (law) {
                case "Idempotent Law": {
                    if (cE.equals("P")) {
                        equivalencies = new String[2];
                        equivalencies[0] = "PoP";
                        equivalencies[1] = "PaP";
                    }
                    break;
                }
                case "Identity Law": {
                    if (cE.equals("P")) {
                        equivalencies = new String[2];
                        equivalencies[0] = "PoF";
                        equivalencies[1] = "PaT";
                    }
                    break;
                }
                case "Domination Law": {
                    if (cE.equals("F")) {
                        equivalencies = new String[1];
                        equivalencies[0] = "PaF";
                    } else if (cE.equals("T")) {
                        equivalencies = new String[1];
                        equivalencies[0] = "PoT";
                    }
                    break;
                }
                case "Double Negation Law": {
                    if (cE.equals("P")) {
                        equivalencies = new String[1];
                        equivalencies[0] = "nnP";
                    }
                    break;
                }
                case "Complement Law": {
                    if (cE.equals("F")) {
                        equivalencies = new String[2];
                        equivalencies[0] = "PanP";
                        equivalencies[1] = "nT";
                    } else if (cE.equals("T")) {
                        equivalencies = new String[2];
                        equivalencies[0] = "PonP";
                        equivalencies[1] = "nF";
                    }
                    break;
                }
                case "Absorption Law": {
                    if (cE.equals("P")) {
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
