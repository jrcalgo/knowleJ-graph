package src.DataStructures;

import src.LogicExpressions.PropositionalLogic.Characters.LogicalCharacters;

public class PartitionedParsingTree {
    private TreeNode root;

    public PartitionedParsingTree(String expression) {
        this.root = buildTree(expression);
    }

    private Node buildTree(String expression) {
        // TODO: expression parsing logic
    }



    public void traverseInOrder() {
        traverseInOrder(root);
    }

    public void traverseInOrder(Node node) {
        if (node != null) {
            traverseInOrder(node.left);
            System.out.print(node.value + " ");
            traverseInOrder(node.right);
        }
    }  

    public void traversePreOrder(Node node) {
        if (node != null) {
            System.out.print(node.value + " ");
            traversePreOrder(node.right);
            traversePreOrder(node.left);
        }
    }
    
    public void traversePostOrder(Node node) {
        if (node != null) {
            traversePostOrder(node.left);
            traversePostOrder(node.right);
            System.out.print(node.value + " ");
        }
    }


    
    private static class TreeNode {
        private String value;
        private TreeNode left, right;

        public TreeNode(String value) {
            this.value = value;
            this.left = null;
            this.right = null;
        }

        public TreeNode(String value, Node left, Node right) {
            this.value = value;
            this.left = left;
            this.right = right;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Node getNodeLeft() {
            return this.left;
        }

        public void setNodeLeft(Node left) {
            this.left = left;
        }

        public Node getNodeRight() {
            return this.right;
        }

        public void setNodeRight(Node right) {
            this.right = right;
        }

    }
}
