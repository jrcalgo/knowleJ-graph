package ai.knowlej.Database.Models;

import java.util.Map;

public record AbstractKnowledgeNode(String concept, String[] conceptLabels, Map<String, String> conceptProperties) {
}
