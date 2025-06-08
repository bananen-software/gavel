package software.bananen.gavel.infrastructure.restapi;

import java.util.Collection;

/**
 * A command that can be used to create a workspace.
 *
 * @param name          The value of the workspace.
 * @param path          The path of the workspace.
 * @param excludedPaths The list of excluded paths.
 * @param basePackage   The base package.
 */
public record CreateWorkspaceRequest(String name,
                                     String path,
                                     Collection<String> excludedPaths,
                                     String basePackage) {
}
