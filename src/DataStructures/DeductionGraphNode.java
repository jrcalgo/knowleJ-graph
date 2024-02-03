package src.DataStructures;

import java.util.ArrayList;
import java.util.LinkedList;

public class DeductionTreeNode {
    private String[] expression;
    private int parentExpressionIndex;
    private LinkedList<DeductionTreeNode> children;

    // root node constructor
    public DeductionTreeNode(String[] expression) {
        this.expression = expression;
        this.parentExpressionIndex = -1;
        this.children = null;
    }

    // leaf node constructor
    public DeductionTreeNode(String[] expression, int parentExpressionIndex) {
        this.expression = expression;
        this.parentExpressionIndex = parentExpressionIndex;
        this.children = null;
    }

    // child node constructor
    public DeductionTreeNode(String expression, int parentExpressionIndex, DeductionTreeNode child) {
        this.expression = expression;
        this.parentExpressionIndex = parentExpressionIndex;
        this.children = new LinkedList<DeductionTreeNode>() {
            {
                add(child);
            }
        };
    }

    public DeductionTreeNode(String expression, int parentExpressionStartIndex,
            LinkedList<DeductionTreeNode> children) {
        this.expression = expression;
        this.parentExpressionIndex = parentExpressionStartIndex;
        this.children = children;
    }

    public String getExpression() {
        return this.expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public int getParentExpressionIndex() throws Exception {
        if (this.parentExpressionIndex == -1)
            throw new Exception("No parent; this node is the root.");

        return this.parentExpressionIndex;
    }

    public void setParentExpressionIndex(int parentExpressionIndex) {
        this.parentExpressionIndex = parentExpressionIndex;
    }

    public LinkedList<DeductionTreeNode> getChildren() {
        return this.children;
    }

    public DeductionTreeNode getChild(int index) {
        return this.children.get(index);
    }

    public void addChild(DeductionTreeNode child) {
        if (this.children == null)
            this.children = new LinkedList<DeductionTreeNode>();

        this.children.add(child);
    }

    public void removeChild(DeductionTreeNode child) {
        if (this.children == null)
            return;

        this.children.remove(child);
    }

}
