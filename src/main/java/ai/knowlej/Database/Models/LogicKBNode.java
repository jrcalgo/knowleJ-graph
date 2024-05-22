package ai.knowlej.Database.Models;

import java.util.Map;

public record LogicKBNode(String logicKBName, String[] logicKBLabels, Map<String, String> logicKBProperties, LogicNode[] logicNodes) {}

