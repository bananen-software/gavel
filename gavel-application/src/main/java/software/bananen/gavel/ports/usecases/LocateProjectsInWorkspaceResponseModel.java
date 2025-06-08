package software.bananen.gavel.ports.usecases;

import java.util.Collection;

/**
 * A response model to the locate projects in workspace request.
 */
public sealed interface LocateProjectsInWorkspaceResponseModel
        permits LocateProjectsInWorkspaceResponseModel.Success, LocateProjectsInWorkspaceResponseModel.Failure {

    /**
     * A response model that represents the successful location of projects in a workspace.
     *
     * @param projectNames The names o the located projects.
     */
    record Success(
            Collection<String> projectNames) implements LocateProjectsInWorkspaceResponseModel {
    }

    /**
     * A response model that represents a failure while locating the projects in a workspace.
     *
     * @param errorMessage The message that describes the failure.
     */
    record Failure(
            String errorMessage) implements LocateProjectsInWorkspaceResponseModel {
    }
}
