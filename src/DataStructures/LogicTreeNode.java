package src.DataStructures;

import java.util.ArrayList;
import java.util.List;

public class LogicTreeNode {
    private String expression;
    private int parentExpressionIndex;
    private List<LogicTreeNode> children;

    public LogicTreeNode(String expression) {
        this.expression = expression;
        this.parentExpressionIndex = -1;
        this.children = null;
    }

    public LogicTreeNode(String expression, int parentExpressionStartIndex) {
        this.expression = expression;
        this.parentExpressionIndex = parentExpressionStartIndex;
        this.children = null;
    }

    public LogicTreeNode(String expression, int parentExpressionStartIndex, LogicTreeNode child) {
        this.expression = expression;
        this.parentExpressionIndex = parentExpressionStartIndex;
        this.children = new ArrayList<LogicTreeNode>() {
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

    public List<LogicTreeNode> getChildren() {
        return this.children;
    }

    public LogicTreeNode getChild(int index) {
        return this.children.get(index);
    }

    public void addChild(LogicTreeNode child) {
        if (this.children == null)
            this.children = new ArrayList<LogicTreeNode>();

        this.children.add(child);
    }

    public void removeChild(LogicTreeNode child) {
        this.children.remove(child);
    }

}
