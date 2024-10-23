package gavel.staticanalysis.adapter;

import java.util.Collection;
import java.util.Optional;

/**
 * A model that represents a projects dependency.
 *
 * @param name                 The name of the dependency
 * @param fileName             The file name.
 * @param filePath             The file path.
 * @param license              The license of the dependency.
 * @param vulnerabilitiesCount The number of vulnerabilities.
 * @param findings             The vulnerability findings.
 */
public record ProjectDependency(String name,
                                String fileName,
                                String filePath,
                                Optional<String> license,
                                int vulnerabilitiesCount,
                                Collection<VulnerabilityFinding> findings) {
}
