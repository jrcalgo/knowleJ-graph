package src.DataStructures;

import java.util.ArrayList;
import java.util.LinkedList;

import src.Interfaces.Tree;
import src.LogicExpressions.PropositionalLogic.Logic.Proposition;
import src.LogicExpressions.PropositionalLogic.Models.Model;

public class DeductionGraph<T extends Comparable<T>> {
    private LinkedList<DeductionGraphNode> givenNodes; // initial expression / root
    private LinkedList<LinkedList<DeductionGraphNode>> branches; // tree of expressions
    private int nodeCount = 0;

    String[] knowledgeBase;
    Proposition query;

    public DeductionGraph(String[] knowledgeBase, Proposition query) {
        this.knowledgeBase = knowledgeBase;
        this.query = query;

        // determine if a 
        for (int i = 0; i < knowledgeBase.length; i++) {
            this.givenNodes.add(new DeductionGraphNode(knowledgeBase[i], null));
            this.nodeCount++;
        }
    }

    public void parseRootChildren() {

    }

    public boolean isEmpty() {
        return this.root == null;
    }

    public int size() {
        return this.nodeCount;
    }

    public void insert(T element) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insert'");
        
    }

    public boolean contains(T element) throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'contains'");
    }

    /**
     * implements best-first search methodology
     */
    public T search(T element) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'search'");
    }

    public void delete(T element) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    public String traverseInOrder() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'traverseInOrder'");
    }

    public String traversePreOrder() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'traversePreOrder'");
    }

    public String traversePostOrder() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'traversePostOrder'");
    }

}
