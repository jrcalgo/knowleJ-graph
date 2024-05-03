package ai.knowlej.DataStructures;

import java.util.ArrayList;
import java.util.LinkedList;

import ai.knowlej.PropositionalLogic.Logic.Proposition;

public class DirectedDeductionGraph {
    private ArrayList<DeductionGraphNode> nodes; // all graph nodes
    private ArrayList<DeductionGraphNode> premiseNodes; // initial expression(s) / root(s)
    private DeductionGraphNode queryNode; // query node
    private ArrayList<DeductionGraphNode> forwardNodes; // nodes from premise(s) to query
    private ArrayList<DeductionGraphNode> backwardNodes; // nodes from query to premise(s)
    private int nodeCount = -1;
    private int premiseCount = -1;

    private String[] knowledgeBase;
    private Proposition query;

    public DirectedDeductionGraph(String[] knowledgeBase, Proposition query) {
        this.knowledgeBase = knowledgeBase;
        this.query = query;

        // store knowledge base expressions as given nodes and as root nodes for subgraphs in this.nodes
        nodes = new ArrayList<DeductionGraphNode>();
        premiseNodes = new ArrayList<DeductionGraphNode>();
        for (int i = 0; i < knowledgeBase.length; i++) {
            this.premiseNodes.add(new DeductionGraphNode(knowledgeBase[i]));
            this.nodes.add(this.premiseNodes.get(i));
            this.nodeCount++;
            this.premiseCount++;
        }
        // points knowledge base expressions to each other (signifies mutual relationship/traversal)
        if (knowledgeBase.length > 1) {
            for (int i = 0; i < knowledgeBase.length; i++) {
                for (int j = knowledgeBase.length-1; j > 0; j--) {
                    if (i != j) {
                        this.mutuallyPoint(this.premiseNodes.get(i), this.premiseNodes.get(j));
                    } else
                        break;
                }
            }
        }

        // adds a detached query node to the graph
        this.queryNode = new DeductionGraphNode(query.getExpression());
        this.nodes.add(this.queryNode);
        this.nodeCount++;
    }

    @SuppressWarnings("null")
    public void point(DeductionGraphNode outVertex, DeductionGraphNode inVertex) {
        if (outVertex == null || inVertex == null) {
            throw new IllegalArgumentException("Both out and in nodes must be non-null");
        } else if (!this.nodes.contains(outVertex) || !this.nodes.contains(inVertex)) {
            throw new IllegalArgumentException("Both out and in nodes must be in the graph");
        }
        outVertex.addOutNode(inVertex);
    }

    private void mutuallyPoint(DeductionGraphNode outVertex, DeductionGraphNode inVertex) {
        outVertex.addOutNode(inVertex);
        inVertex.addOutNode(outVertex);
    }

    public DeductionGraphNode add(String expression) throws Exception {
        this.nodes.add(new DeductionGraphNode(expression));
        this.nodeCount++;

        return this.getNode(expression);
    }

    public void delete(String expression) {
        for (DeductionGraphNode node : this.nodes) {
            if (node.getExpression().equals(expression)) {
                this.nodes.remove(node);
                this.nodeCount--;
            }
        }
    }

    public void delete(DeductionGraphNode vertex) {
        if (this.nodes.contains(vertex)) {
            this.nodes.remove(vertex);
            this.nodeCount--;
        }
    }
    public boolean isEmpty() {
        return this.nodeCount == -1;
    }

    public int size() {
        return this.nodeCount;
    }

    public boolean contains(String expression) throws Exception {
        for (DeductionGraphNode node : this.nodes) {
            if (node.getExpression().equals(expression)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<DeductionGraphNode> astarToQuery() {
        ArrayList<DeductionGraphNode> paths = null;

        
        return paths;
    }

    public DeductionGraphNode getNode(String expression) throws Exception {
        for (DeductionGraphNode node : this.nodes) {
            if (node.getExpression().equals(expression)) {
                return node;
            }
        }
        throw new Exception("Node not found");
    }

    public int findNodeIndex(String expression) throws Exception {
        try {
            Proposition prop = new Proposition(expression);
        } catch (Exception e) {
            throw new Exception("Invalid expression");
        }
        
        for (int i = 0; i < this.nodes.size(); i++) {
            if (this.nodes.get(i).getExpression().equals(expression)) {
                return i;
            }
        }
        return -1;
    }

    public String[] getKnowledgeBase() {
        return this.knowledgeBase;
    }

    public String getQuery() {
        return this.query.getExpression();
    }

    public ArrayList<DeductionGraphNode> getNodes() {
        return this.nodes;
    }

    public ArrayList<DeductionGraphNode> getLeafNodes() throws Exception {
        ArrayList<DeductionGraphNode> leafs = new ArrayList<>();
        for (DeductionGraphNode node : this.nodes) {
            if (node.getOutNodes().size() == 0 && !this.isQueryNode(node)) {
                leafs.add(node);
            }
        }
        return leafs;
    }

    public ArrayList<DeductionGraphNode> getForwardNodes() {
        return this.forwardNodes;
    }

    public ArrayList<DeductionGraphNode> getBackwardNodes() {
        return this.backwardNodes;
    }

    public ArrayList<DeductionGraphNode> getPremiseNodes() {
        return this.premiseNodes;
    }

    public DeductionGraphNode getQueryNode() {
        return this.queryNode;
    }

    public boolean isNode(DeductionGraphNode vertex) {
        return this.nodes.contains(vertex);
    }

    public boolean isLeafNode(DeductionGraphNode vertex) throws Exception {
        return vertex.getOutNodes().size() == 0 && !this.isQueryNode(vertex);
    }

    public boolean isPremiseNode(DeductionGraphNode vertex) {
        return this.premiseNodes.contains(vertex);
    }

    public boolean isQueryNode(DeductionGraphNode vertex) {
        return vertex.getExpression().equals(this.query.getExpression());
    }

    public boolean isPointing(DeductionGraphNode outVertex, DeductionGraphNode inVertex) throws Exception {
        return outVertex.getOutNodes().contains(inVertex);
    }
}
