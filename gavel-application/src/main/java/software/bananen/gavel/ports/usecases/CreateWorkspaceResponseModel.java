package software.bananen.gavel.ports.usecases;

/**
 * A response model for the creation of workspaces.
 */
public sealed interface CreateWorkspaceResponseModel
        permits CreateWorkspaceResponseModel.Success,
        CreateWorkspaceResponseModel.Failure {

    /**
     * A response model that indicates that a workspace has been created successfully.
     *
     * @param id The ID of the created workspace.
     */
    record Success(Long id) implements CreateWorkspaceResponseModel {
    }

    /**
     * A response model that can be used to indicate that the workspace creation failed.
     *
     * @param message The message.
     */
    record Failure(String message)
            implements CreateWorkspaceResponseModel {
    }
}
