package software.bananen.gavel.domain.model;

import java.nio.file.Path;

/**
 * A value object that can be used to represent an excluded value.
 *
 * @param value The value.
 */
public record WorkspaceExcludedPathValueObject(String value) {

    public WorkspaceExcludedPathValueObject {
        if (value == null) {
            throw new IllegalArgumentException("The value may not be null");
        }

        if (value.isEmpty()) {
            throw new IllegalArgumentException("The value may not be empty");
        }

        try {
            Path.of(value);
            //TODO: What exception is actually thrown?
        } catch (final Exception e) {
            throw new IllegalArgumentException("Invalid value " + value, e);
        }
    }
}
