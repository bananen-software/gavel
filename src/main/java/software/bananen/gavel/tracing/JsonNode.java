package software.bananen.gavel.tracing;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public record JsonNode(@JsonProperty("name") String name,
                       @JsonProperty("size") Integer size,
                       @JsonProperty("children") Collection<JsonNode> children,
                       @JsonProperty("type") String type) {
}
