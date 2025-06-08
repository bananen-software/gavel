package software.bananen.gavel.ports.usecases;

/**
 * A request that can be used to locate projects in a specific workspace.
 *
 * @param workspaceId The ID of the workspace.
 */
public record LocateProjectsInWorkspaceRequest(Long workspaceId) {
}
