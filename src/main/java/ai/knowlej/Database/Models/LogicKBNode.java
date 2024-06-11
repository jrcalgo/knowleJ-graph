package ai.knowlej.Database.Models;

import java.util.Map;

public record LogicKBNode(String logicKBName, String[] logicKBLabels, Map<String, String> logicKBProperties, LogicNode[] logicNodes) {
    public LogicKBNode {
        if (logicKBName == null || logicKBName.isBlank()) {
            throw new IllegalArgumentException("Logic KB name cannot be null or empty");
        }
        if (logicKBLabels == null || logicKBLabels.length == 0) {
            throw new IllegalArgumentException("Logic KB labels cannot be null or empty");
        }
        if (logicKBProperties == null || logicKBProperties.isEmpty()) {
            throw new IllegalArgumentException("Logic KB properties cannot be null or empty");
        }
        if (logicNodes == null || logicNodes.length == 0) {
            throw new IllegalArgumentException("Logic nodes cannot be null or empty");
        }
    }
}

