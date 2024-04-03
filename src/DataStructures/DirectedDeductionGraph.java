package src.DataStructures;

import java.util.ArrayList;
import java.util.LinkedList;

import src.LogicType.PropositionalLogic.Logic.Proposition;

public class DirectedDeductionGraph {
    private ArrayList<DeductionGraphNode> nodes; // all graph nodes
    private ArrayList<DeductionGraphNode> premiseNodes; // initial expression(s) / root(s)
    private DeductionGraphNode queryNode; // query node
    private int nodeCount = -1;

    private String[] knowledgeBase;
    private Proposition query;

    public DirectedDeductionGraph(String[] knowledgeBase, Proposition query) {
        this.knowledgeBase = knowledgeBase;
        this.query = query;

        // store knowledge base expressions as given nodes and as root nodes for subgraphs in this.nodes
        for (int i = 0; i < knowledgeBase.length; i++) {
            this.premiseNodes.add(new DeductionGraphNode(knowledgeBase[i]));
            this.nodes.add(this.premiseNodes.get(i));
            this.nodeCount++;
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

    public void add(String expression) {
        this.nodes.add(new DeductionGraphNode(expression));
        this.nodeCount++;
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

    public boolean contains(DeductionGraphNode vertex) {
        return this.nodes.contains(vertex);
    }

    public boolean containsQuery(DeductionGraphNode vertex) {
        return vertex.getExpression().equals(this.query.getExpression());
    }

    public ArrayList<DeductionGraphNode> getNodes() {
        return this.nodes;
    }

    public ArrayList<DeductionGraphNode> getLeafNodes() throws Exception {
        ArrayList<DeductionGraphNode> leafs = new ArrayList<>();
        for (DeductionGraphNode node : this.nodes) {
            if (node.getOutNodes().size() == 0 && !this.containsQuery(node)) {
                leafs.add(node);
            }
        }
        return leafs;
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
        return vertex.getOutNodes().size() == 0 && !this.containsQuery(vertex);
    }

    public boolean isPremiseNode(DeductionGraphNode vertex) {
        return this.premiseNodes.contains(vertex);
    }

    public boolean isQueryNode(DeductionGraphNode vertex) {
        return vertex.getExpression().equals(this.query.getExpression());
    }
}
