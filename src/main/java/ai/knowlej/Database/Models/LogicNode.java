package ai.knowlej.Database.Models;

import java.util.Map;

public record LogicNode(String logicSentence, String[] logicLabels, Map<String, String> logicProperties) {
}
