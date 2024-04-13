package software.bananen.gavel.config.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A configuration for the outputs of the gavel application.
 *
 * @param targetDirectory The target directory that the reports will be written
 *                        to.
 */
public record OutputConfig(
        @JsonProperty("target_directory") String targetDirectory) {
}
