package ai.knowlej.Database.Models;

import java.util.Map;

public record SubdomainNode(String subdomainName, String[] subdomainLabels, Map<String, String> subdomainProperties) {
}
