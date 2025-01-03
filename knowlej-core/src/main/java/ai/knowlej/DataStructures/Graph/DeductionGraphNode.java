package ai.knowlej.DataStructures.Graph;

import java.util.LinkedList;

public class DeductionGraphNode {
    private String expression;
    private LinkedList<DeductionGraphNode> outNodes;
    private LinkedList<String> outEdgeLaws;

    public DeductionGraphNode(String expression) {
        this.expression = expression;
        this.outNodes = null;
    }

    // leaf node constructor
    public DeductionGraphNode(String expression, DeductionGraphNode outNode) {
        this.expression = expression;

        if (outNode != null) {
            this.outNodes = new LinkedList<DeductionGraphNode>() {
                {
                    add(outNode);
                }
            };
        }
    }

    // child node constructor
    public DeductionGraphNode(String expression, LinkedList<DeductionGraphNode> outNodes) {
        this.expression = expression;
        this.outNodes = outNodes;
    }

    public String getExpression() {
        return this.expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getLaw() {
        return "";
    }

    public void setLaw(String law) {

    }

    public LinkedList<DeductionGraphNode> getOutNodes() throws Exception {
        if (this.outNodes == null)
            throw new Exception("No out edges for this node");

        return this.outNodes;
    }

    public void setOutNode(DeductionGraphNode outNode) {
        this.outNodes = null;
        this.outNodes = new LinkedList<DeductionGraphNode>() {
            {
                add(outNode);
            }
        };
    }

    public void addOutNode(DeductionGraphNode outNode) {
        if (this.outNodes == null)
            this.outNodes = new LinkedList<DeductionGraphNode>();
        boolean outNodeContains = this.outNodes.contains(outNode);
        if (!outNodeContains)
            this.outNodes.add(outNode);
    }

    public void removeOutNode(DeductionGraphNode outNode) {
        if (this.outNodes == null) {
            System.out.println("No out edges for this node");
        }
        else this.outNodes.remove(outNode);
    }

    public void removeOutNodes(LinkedList<DeductionGraphNode> outNodes) {
        if (this.outNodes == null) {

        }
        else if (this.outNodes.containsAll(outNodes))
            this.outNodes.removeAll(outNodes);
        else {

        }
    }
}
