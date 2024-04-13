package software.bananen.gavel.config.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GavelConfig(
        @JsonProperty("analysis_context") AnalysisContext analysisContext,
        @JsonProperty("output") OutputConfig outputConfig) {
}
