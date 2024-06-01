package ai.knowlej.DataStructures;

import ai.knowlej.PropositionalLogic.Logic.Proposition;

public record StateDomain(String[] logicSentence) {
    public StateDomain {
        for (String sentence : logicSentence) {
            try {
                Proposition propTest = new Proposition(sentence);
            } catch (Exception e) {
                throw new RuntimeException("Invalid statement included in logicalStatements");
            }
        }
        System.gc();
    }
}

