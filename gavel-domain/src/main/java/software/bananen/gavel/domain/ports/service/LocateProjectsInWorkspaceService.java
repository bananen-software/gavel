package software.bananen.gavel.domain.ports.service;

import software.bananen.gavel.domain.model.ProjectNameValueObject;
import software.bananen.gavel.domain.model.ProjectPathValueObject;
import software.bananen.gavel.domain.model.WorkspaceAggregate;

import java.util.Collection;

/**
 * A service that locates projects in a given workspace.
 */
public interface LocateProjectsInWorkspaceService {

    /**
     * Locates the projects contained in the given workspace.
     *
     * @param workspace The workspace that the projects should be located in.
     * @return The located projects.
     */
    Collection<LocatedProject> locateProjectsInWorkspace(WorkspaceAggregate workspace);

    /**
     * A representation of located projects.
     *
     * @param name The value of the project.
     * @param path The path to the project.
     */
    record LocatedProject(ProjectNameValueObject name,
                          ProjectPathValueObject path) {

    }
}
