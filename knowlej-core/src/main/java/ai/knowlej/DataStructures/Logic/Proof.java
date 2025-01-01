package ai.knowlej.DataStructures.Logic;

import ai.knowlej.DataStructures.Graph.DeductionGraphNode;

import java.util.ArrayList;

public record Proof(ArrayList<DeductionGraphNode> proof) {

    void placeStatement(DeductionGraphNode statementNode, int index) {
        proof.set(index, statementNode);
    }

    void appendStatement(DeductionGraphNode statementNode) {
        proof.add(statementNode);
    }

    void replaceProof(ArrayList<DeductionGraphNode> newProof) {
        proof.clear();
        proof.addAll(newProof);
    }
}
