package software.bananen.gavel.config.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OutputConfig(
        @JsonProperty("target_directory") String targetDirectory) {
}
