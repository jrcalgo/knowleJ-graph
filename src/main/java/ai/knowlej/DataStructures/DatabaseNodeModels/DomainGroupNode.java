package ai.knowlej.DataStructures.DatabaseNodeModels;

import java.util.Map;

public record DomainGroupNode(String domainName, String[] domainLabels, Map<String, String> domainProperties) {
    public DomainGroupNode {
        if (domainName == null || domainName.isBlank()) {
            throw new IllegalArgumentException("Domain name cannot be null or empty");
        }
        if (domainLabels == null || domainLabels.length == 0) {
            throw new IllegalArgumentException("Domain labels cannot be null or empty");
        }
        if (domainProperties == null || domainProperties.isEmpty()) {
            throw new IllegalArgumentException("Domain properties cannot be null or empty");
        }
    }
}
