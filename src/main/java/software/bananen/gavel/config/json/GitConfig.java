package software.bananen.gavel.config.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public record GitConfig(
        @JsonProperty("included_file_extensions") Collection<String> includedFileExtensions) {
}
