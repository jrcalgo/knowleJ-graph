package ai.knowlej.Database.Models;

import java.util.Map;

import ai.knowlej.PropositionalLogic.Logic.Proposition;

public record LogicNode(String logicSentence, String[] logicLabels, Map<String, String> logicProperties) {
    public LogicNode {
        if (logicSentence == null) {
            throw new IllegalArgumentException("Logic sentence cannot be null");
        }
        // test logical Sentence validity
        try {
            Proposition propositionTest = new Proposition(logicSentence);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid logic sentence");
        }
    }
}
