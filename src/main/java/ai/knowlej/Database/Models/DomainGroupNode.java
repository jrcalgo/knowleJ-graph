package ai.knowlej.Database.Models;

import java.util.Map;

public record DomainGroupNode(String domainName, String[] domainLabels, Map<String, String> domainProperties) {
}
