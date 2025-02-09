package software.bananen.gavel.domain.model;

import java.util.List;

/**
 * An entity that represents a workspace.
 *
 * @param id            The ID of the workspace.
 * @param name          The value of the workspace.
 * @param path          The path to the workspace.
 * @param excludedPaths The excluded paths of the workspace.
 * @param basePackage   The base package of the workspace.
 */
public record WorkspaceEntity(WorkspaceIdValueObject id,
                              WorkspaceNameValueObject name,
                              WorkspacePathValueObject path,
                              List<WorkspaceExcludedPathValueObject> excludedPaths,
                              WorkspaceBasePackageValueObject basePackage) {

    public WorkspaceEntity {
        if (name == null) {
            throw new IllegalArgumentException("The value may not be null");
        }

        if (path == null) {
            throw new IllegalArgumentException("The path may not be null");
        }

        if (excludedPaths == null) {
            throw new IllegalArgumentException("The excluded paths may not be null");
        }

        if (basePackage == null) {
            throw new IllegalArgumentException("The base package may not be null");
        }
    }

    /**
     * Renames the workspace.
     *
     * @param newName The new value. This parameter may not be null.
     * @return The renamed workspace.
     */
    public WorkspaceEntity rename(final WorkspaceNameValueObject newName) {
        if (newName == null) {
            throw new IllegalArgumentException("The new value may not be null");
        }

        return new WorkspaceEntity(this.id, newName, this.path, this.excludedPaths, this.basePackage);
    }
}
