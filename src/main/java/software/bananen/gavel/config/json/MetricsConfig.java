package software.bananen.gavel.config.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MetricsConfig(
        @JsonProperty("depth_of_inheritance") DepthOfInheritanceConfig depthOfInheritanceConfig,
        @JsonProperty("change_coupling") ChangeCouplingConfig changeCouplingConfig) {
}
