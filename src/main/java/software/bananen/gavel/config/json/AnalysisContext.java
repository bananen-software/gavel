package software.bananen.gavel.config.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public record AnalysisContext(
        @JsonProperty("included_paths") Collection<String> includedPaths,
        @JsonProperty("root_package") String rootPackage,
        @JsonProperty("resolve_subpackages") boolean resolveSubpackages) {
}
