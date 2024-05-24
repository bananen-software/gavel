package software.bananen.gavel.config.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChangeCouplingConfig(
        @JsonProperty("min_changes_threshold") int mininmalNumberOfChanges,
        @JsonProperty("percentage_threshold") double percentageThreshold) {
}
