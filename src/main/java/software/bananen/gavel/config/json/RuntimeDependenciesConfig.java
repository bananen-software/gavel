package software.bananen.gavel.config.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public record RuntimeDependenciesConfig(
        @JsonProperty("exclusions") Collection<String> exclusions) {
}
