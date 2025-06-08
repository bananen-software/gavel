package software.bananen.gavel.domain.model;

import java.util.Collection;

/**
 * A model that represents a projects' dependency.
 *
 * @param name                 The name of the dependency
 * @param fileName             The file name.
 * @param filePath             The file path.
 * @param vulnerabilitiesCount The number of vulnerabilities.
 * @param findings             The vulnerability findings.
 */
public record VulnerableDependency(String name,
                                   String fileName,
                                   String filePath,
                                   int vulnerabilitiesCount,
                                   Collection<VulnerabilityFinding> findings) {
}
