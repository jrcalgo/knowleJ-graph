package src.DataStructures;

import src.Interfaces.Tree;

public class PropositionTree<T extends Comparable<T>> implements Tree<T> {
    private PropositionTreeNode root; // initial expression / root
    private int nodeCount = 0;

    public LogicTree(String expression) {
        this.root = new PropositionTreeNode(expression);
        this.nodeCount++;
    }

    @Override
    public boolean isEmpty() {
        return this.root == null;
    }

    @Override
    public int size() {
        return this.nodeCount;
    }

    @Override
    public void insert(T element) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insert'");
        
    }

    @Override
    public boolean contains(T element) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'contains'");
    }

    @Override
    public T search(T element) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'search'");
    }

    @Override
    public void delete(T element) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public String traverseInOrder() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'traverseInOrder'");
    }

    @Override
    public String traversePreOrder() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'traversePreOrder'");
    }

    @Override
    public String traversePostOrder() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'traversePostOrder'");
    }

}
