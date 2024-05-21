package ai.knowlej.Database.Models;

import java.util.Map;

public record LogicKBNode(String logicGroupName, String[] logicGroupLabels, Map<String, String> logicGroupProperties, LogicNode[] logicNodes) {}

