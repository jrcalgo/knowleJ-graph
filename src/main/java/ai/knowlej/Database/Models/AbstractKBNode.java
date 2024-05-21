package ai.knowlej.Database.Models;

import java.util.Map;

public record AbstractKBNode(String abstractGroupName, String[] abstractGroupLabels, Map<String, String> abstractGroupProperties, AbstractKnowledgeNode[] abstractKnowledgeNodes) {}
