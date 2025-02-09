package software.bananen.gavel.domain.model;

/**
 * A value object that represents the value of a workspace.
 *
 * @param value The value.
 */
public record WorkspaceNameValueObject(String value) {

    public WorkspaceNameValueObject {
        if (value == null) {
            throw new IllegalArgumentException("The value may not be null");
        }

        if (value.isEmpty()) {
            throw new IllegalArgumentException("The value may not be empty");
        }
    }
}
