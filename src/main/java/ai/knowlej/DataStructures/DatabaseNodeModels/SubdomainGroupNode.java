package ai.knowlej.DataStructures.DatabaseNodeModels;

import java.util.Map;

public record SubdomainGroupNode(String subdomainName, String[] subdomainLabels, Map<String, String> subdomainProperties) {
    public SubdomainGroupNode {
        if (subdomainName == null || subdomainName.isBlank()) {
            throw new IllegalArgumentException("Subdomain name cannot be null or empty");
        }
        if (subdomainLabels == null || subdomainLabels.length == 0) {
            throw new IllegalArgumentException("Subdomain labels cannot be null or empty");
        }
        if (subdomainProperties == null || subdomainProperties.isEmpty()) {
            throw new IllegalArgumentException("Subdomain properties cannot be null or empty");
        }
    }
}
