package software.bananen.gavel.ports.usecases;

import software.bananen.gavel.domain.model.ProjectNameValueObject;
import software.bananen.gavel.domain.model.WorkspaceIdValueObject;
import software.bananen.gavel.domain.ports.service.LocateProjectsInWorkspaceService;
import software.bananen.gavel.domain.service.WorkspaceService;

import static java.util.Objects.requireNonNull;

/**
 * A use case that can be used to locate projects in a workspace.
 */
public final class LocateProjectsInWorkspaceUseCase {

    private final WorkspaceService workspaceService;
    private final LocateProjectsInWorkspaceService locateProjectsInWorkspaceService;

    /**
     * Creates a new instance.
     *
     * @param workspaceService                 The workspace service.
     * @param locateProjectsInWorkspaceService The locate projects in workspace service.
     */
    public LocateProjectsInWorkspaceUseCase(
            final WorkspaceService workspaceService,
            final LocateProjectsInWorkspaceService locateProjectsInWorkspaceService) {
        this.workspaceService =
                requireNonNull(workspaceService, "The workspace service may not be null");
        this.locateProjectsInWorkspaceService =
                requireNonNull(locateProjectsInWorkspaceService, "The locate projects in workspace service may not be null");
    }

    /**
     * Locates the projects in the given workspace.
     *
     * @param request The request.
     */
    public LocateProjectsInWorkspaceResponseModel execute(final LocateProjectsInWorkspaceRequest request) {
        if (request == null) {
            return new LocateProjectsInWorkspaceResponseModel.Failure("The request may not be null");
        }
        
        if (request.workspaceId() == null) {
            return new LocateProjectsInWorkspaceResponseModel.Failure(
                    "The workspace ID may not be null");
        }

        final var workspace =
                workspaceService.findById(new WorkspaceIdValueObject(request.workspaceId()));

        if (workspace.isPresent()) {
            final var locatedProjects =
                    locateProjectsInWorkspaceService.locateProjectsInWorkspace(workspace.get());

            var updatedWorkspace = workspace.get();

            for (final var locatedProject : locatedProjects) {
                if (updatedWorkspace.doesNotHaveProjectWithName(locatedProject.name())) {
                    updatedWorkspace = updatedWorkspace.addProject(locatedProject.name(), locatedProject.path());
                }
            }

            workspaceService.save(updatedWorkspace);

            if (locatedProjects.isEmpty()) {
                return new LocateProjectsInWorkspaceResponseModel.Failure(
                        "No projects found in the workspace with ID " + request.workspaceId());
            } else {
                return new LocateProjectsInWorkspaceResponseModel.Success(
                        locatedProjects.stream()
                                .map(LocateProjectsInWorkspaceService.LocatedProject::name)
                                .map(ProjectNameValueObject::value)
                                .toList());
            }
        } else {
            return new LocateProjectsInWorkspaceResponseModel.Failure(
                    "The workspace with ID " + request.workspaceId() + " could not be found");
        }
    }
}
