package software.bananen.gavel.config.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

/**
 * A configuration for the analysis context passed to the application.
 *
 * @param includedPaths      The paths that should be included in the analysis.
 * @param rootPackage        The root package that should be used for analysis.
 * @param resolveSubpackages A flag that indicates whether subpackages should be
 *                           resolved in the analysis.
 * @param exclusionPatterns  A list of patterns that should be excluded from the
 *                           analysis.
 * @param gitConfig          The git configuration.
 */
public record AnalysisContext(
        @JsonProperty("included_paths") Collection<String> includedPaths,
        @JsonProperty("root_package") String rootPackage,
        @JsonProperty("resolve_subpackages") boolean resolveSubpackages,
        @JsonProperty("exclusion_patterns") Collection<String> exclusionPatterns,
        @JsonProperty("git") GitConfig gitConfig) {
}
