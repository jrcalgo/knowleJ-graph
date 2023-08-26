package src.DataStructures;

import src.Interfaces.Tree;
import src.LogicExpressions.PropositionalLogic.Characters.LogicalSyntax;

public class PartitionedParsingTree<T extends Comparable<T>> implements Tree<T> {
    /** root node of tree */
    private TreeNode<T> root;
    /** connected left and right node pointers */
    private TreeNode<T> left, right = null;
    /** total existing elements in tree */
    private int size;

    public PartitionedParsingTree(String expression) {
        this.root = buildTree(expression);
    }

    public boolean isEmpty() {
        return this.size == 0;
    }
    
    public int size() {
        return this.size;
    }

    public void insert(T element) {

    }
    
    public boolean contains(T element) throws Exception {
        
    }

    public T search(T element) {

    }

    private void buildTree(String expression) {
        // TODO: expression parsing logic
        for (Character c : expression.toCharArray()) {
            if ()
    }

    public void traverseInOrder() {
        if (node != null) {
            traverseInOrder(node.left);
            System.out.print(node.value + " ");
            traverseInOrder(node.right);
        }
    }  

    public void traversePreOrder() {
        if (node != null) {
            System.out.print(node.value + " ");
            traversePreOrder(node.right);
            traversePreOrder(node.left);
        }
    }
    
    public void traversePostOrder() {
        if (node != null) {
            traversePostOrder(node.left);
            traversePostOrder(node.right);
            System.out.print(node.value + " ");
        }
    }

    @Override
    public void delete(T element) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
}
