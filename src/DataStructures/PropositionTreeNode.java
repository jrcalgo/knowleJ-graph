package src.DataStructures;

import java.util.ArrayList;
import java.util.List;

public class PropositionTreeNode {
    private String expression;
    private int parentExpressionIndex;
    private List<PropositionTreeNode> children;

    public PropositionTreeNode(String expression) {
        this.expression = expression;
        this.parentExpressionIndex = -1;
        this.children = null;
    }

    public PropositionTreeNode(String expression, int parentExpressionStartIndex) {
        this.expression = expression;
        this.parentExpressionIndex = parentExpressionStartIndex;
        this.children = null;
    }

    public PropositionTreeNode(String expression, int parentExpressionStartIndex, PropositionTreeNode child) {
        this.expression = expression;
        this.parentExpressionIndex = parentExpressionStartIndex;
        this.children = new ArrayList<PropositionTreeNode>() {
            {
                add(child);
            }
        };
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

    public List<PropositionTreeNode> getChildren() {
        return this.children;
    }

    public PropositionTreeNode getChild(int index) {
        return this.children.get(index);
    }

    public void addChild(PropositionTreeNode child) {
        if (this.children == null)
            this.children = new ArrayList<PropositionTreeNode>();

        this.children.add(child);
    }

    public void removeChild(PropositionTreeNode child) {
        this.children.remove(child);
    }

}
