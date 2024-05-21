package ai.knowlej.Database.Models;

import java.util.Map;

public record SubdomainGroupNode(String subdomainName, String[] subdomainLabels, Map<String, String> subdomainProperties) {
}
