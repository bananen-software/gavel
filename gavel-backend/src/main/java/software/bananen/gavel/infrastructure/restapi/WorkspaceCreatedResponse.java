package software.bananen.gavel.infrastructure.restapi;

/**
 * A response that can be used to indicate when a workspace has been created.
 *
 * @param id The ID of the created workspace.
 */
public record WorkspaceCreatedResponse(Long id) {
}
