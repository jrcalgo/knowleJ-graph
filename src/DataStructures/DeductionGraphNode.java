package src.DataStructures;

import java.util.LinkedList;

public class DeductionGraphNode {
    private String expression;
    private LinkedList<DeductionGraphNode> outNodes;

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
        
        if (!this.outNodes.contains(outNode))
            this.outNodes.add(outNode);
        else
            return;
    }

    public void removeOutNode(DeductionGraphNode outNode) {
        if (this.outNodes == null)
            return;
        else
            if (this.outNodes.contains(outNode)) 
                this.outNodes.remove(outNode);
    }

    public void removeOutNodes(LinkedList<DeductionGraphNode> outNodes) {
        if (this.outNodes == null)
            return;
        else
            if (this.outNodes.containsAll(outNodes))
                this.outNodes.removeAll(outNodes);
            else
                return;
                
    }


}
