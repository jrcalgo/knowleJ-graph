package ai.knowlej.DataStructures.DatabaseNodeModels;

import java.util.Map;

public record AbstractKnowledgeNode(String concept, String[] conceptLabels, Map<String, String> conceptProperties) {
    public AbstractKnowledgeNode {
        if (concept == null || concept.isBlank()) {
            throw new IllegalArgumentException("Concept name cannot be null or empty");
        }
        if (conceptLabels == null || conceptLabels.length == 0) {
            throw new IllegalArgumentException("Concept labels cannot be null or empty");
        }
        if (conceptProperties == null || conceptProperties.isEmpty()) {
            throw new IllegalArgumentException("Concept properties cannot be null or empty");
        }
    }
}
