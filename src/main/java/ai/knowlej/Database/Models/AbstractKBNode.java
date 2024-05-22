package ai.knowlej.Database.Models;

import java.util.Map;

public record AbstractKBNode(String abstractKBName, String[] abstractKBLabels, Map<String, String> abstractKBProperties, AbstractKnowledgeNode[] abstractKnowledgeNodes) {}
