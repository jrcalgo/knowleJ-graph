package ai.knowlej.PropositionalLogic.Logic;

import java.util.ArrayList;

public record Proof(ArrayList<String> proof) {

    void storeStatement(String statement) {
        proof.add(statement);
    }

    void replaceProof(ArrayList<String> newProof) {
        proof.clear();
        proof.addAll(newProof);
    }
}
