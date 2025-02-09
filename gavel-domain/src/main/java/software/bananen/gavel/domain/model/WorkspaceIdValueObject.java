package software.bananen.gavel.domain.model;

/**
 * A value object that represents the ID of a workspace.
 *
 * @param value The value.
 */
public record WorkspaceIdValueObject(Long value) {

    public WorkspaceIdValueObject {
        if (value == null) {
            throw new IllegalArgumentException("The value may not be null");
        }
    }
}
