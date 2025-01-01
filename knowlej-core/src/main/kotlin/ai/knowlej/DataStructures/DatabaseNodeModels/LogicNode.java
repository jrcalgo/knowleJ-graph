package ai.knowlej.DataStructures.DatabaseNodeModels;

import java.util.Map;

import ai.knowlej.PropositionalLogic.Logic.Proposition;

public record LogicNode(String logicSentence, String[] logicLabels, Map<String, String> logicProperties) {
    public LogicNode {
        if (logicSentence == null || logicSentence.isBlank()) {
            throw new IllegalArgumentException("Logic sentence cannot be null or empty");
        }
        if (logicLabels == null || logicLabels.length == 0) {
            throw new IllegalArgumentException("Logic labels cannot be null or empty");
        }
        if (logicProperties == null || logicProperties.isEmpty()) {
            throw new IllegalArgumentException("Logic properties cannot be null or empty");
        }
        // test logical Sentence validity
        try {
            Proposition propositionTest = new Proposition(logicSentence);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid logic sentence");
        }
    }
}
