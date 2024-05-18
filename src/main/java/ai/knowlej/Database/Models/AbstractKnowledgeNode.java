package ai.knowlej.Database.Models;

import java.util.Map;

public record AbstractKnowledgeNode(String conceptStatement, String[] conceptLabels, Map<String, String> conceptProperties) {
}
