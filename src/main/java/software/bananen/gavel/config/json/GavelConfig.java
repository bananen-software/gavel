package software.bananen.gavel.config.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The configuration for the gavel application.
 *
 * @param analysisContext The configuration of the analysis context.
 * @param outputConfig    The configuration for the output.
 */
public record GavelConfig(
        @JsonProperty("analysis_context") AnalysisContext analysisContext,
        @JsonProperty("output") OutputConfig outputConfig) {
}
