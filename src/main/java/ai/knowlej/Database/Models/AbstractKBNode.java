package ai.knowlej.Database.Models;

import java.util.Map;

public record AbstractKBNode(String abstractKBName, String[] abstractKBLabels, Map<String, String> abstractKBProperties,
                             AbstractKnowledgeNode[] abstractKnowledgeNodes) {
    public AbstractKBNode {
        if (abstractKBName == null || abstractKBName.isBlank()) {
            throw new IllegalArgumentException("Abstract KB name cannot be null or empty");
        }
        if (abstractKBLabels == null || abstractKBLabels.length == 0) {
            throw new IllegalArgumentException("Abstract KB labels cannot be null or empty");
        }
        if (abstractKBProperties == null || abstractKBProperties.isEmpty()) {
            throw new IllegalArgumentException("Abstract KB properties cannot be null or empty");
        }
        if (abstractKnowledgeNodes == null || abstractKnowledgeNodes.length == 0) {
            throw new IllegalArgumentException("Abstract knowledge nodes cannot be null or empty");
        }
    }
}
