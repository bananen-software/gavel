package software.bananen.gavel.domain.model;

import java.nio.file.Path;

/**
 * A value object that can be used to represent a workspace value.
 *
 * @param value The value.
 */
public record WorkspacePathValueObject(Path value) {

    public WorkspacePathValueObject {
        if (value == null) {
            throw new IllegalArgumentException("The value may not be null");
        }

        if (!value.isAbsolute()) {
            throw new IllegalArgumentException("The value must be absolute");
        }
    }

    /**
     * Maps the value object to its string representation.
     *
     * @return The string representation.
     */
    public String asStringValue() {
        return value.toString();
    }
}
