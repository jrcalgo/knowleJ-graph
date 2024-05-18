package ai.knowlej.Database.Models;

import java.util.Map;

public record DomainNode(String domainName, String[] domainLabels, Map<String, String> domainProperties) {
}
