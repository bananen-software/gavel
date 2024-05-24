package software.bananen.gavel.config.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DepthOfInheritanceConfig(
        @JsonProperty("threshold") int threshold) {
}
