package software.bananen.gavel.ports.usecases;

import java.util.Collection;

/**
 * A request model that can be used to create a new workspace.
 *
 * @param name          The value of the workspace.
 * @param path          The value to the workspace.
 * @param excludedPaths Paths that should be excluded from the workspace.
 * @param basePackage   The base package that should be analyzed.
 */
public record CreateWorkspaceRequestModel(
        String name,
        String path,
        Collection<String> excludedPaths,
        String basePackage) {
}
