package software.bananen.gavel.domain.model;

/**
 * A value object that can be used to represent a workspaces base package.
 *
 * @param value The value.
 */
public record WorkspaceBasePackageValueObject(String value) {

    // Regex pattern to match a valid Java package value
    private static final String PACKAGE_NAME_PATTERN =
            "^[a-zA-Z_]([a-zA-Z\\d_]*\\.)*[a-zA-Z\\d_]*$";

    public WorkspaceBasePackageValueObject {
        if (value == null) {
            throw new IllegalArgumentException("The base package may not be null");
        }

        if (value.isEmpty()) {
            throw new IllegalArgumentException("The base package may not be empty");
        }

        if (!isValidPackageName(value)) {
            throw new IllegalArgumentException("Invalid base package " + value);
        }
    }

    /**
     * Checks whether the given package value is value.
     *
     * @param packageName The package value.
     * @return True if the given package value is valid, otherwise false.
     */
    private static boolean isValidPackageName(final String packageName) {
        return packageName != null && packageName.matches(PACKAGE_NAME_PATTERN);
    }
}
