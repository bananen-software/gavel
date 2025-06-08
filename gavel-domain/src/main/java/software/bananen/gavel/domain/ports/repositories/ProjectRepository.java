package software.bananen.gavel.domain.ports.repositories;

import software.bananen.gavel.domain.model.AnalysisStatus;
import software.bananen.gavel.domain.model.ProjectAggregate;
import software.bananen.gavel.domain.model.ProjectIdValueObject;

import java.util.Collection;
import java.util.Optional;

/**
 * A repository that can be used to access projects
 */
public interface ProjectRepository {

    /**
     * Attempts to find a project by its ID.
     *
     * @param id The ID of the project.
     * @return The project or {@link Optional#empty()} if there is none.
     */
    Optional<ProjectAggregate> findById(ProjectIdValueObject id);

    /**
     * Attempts to find all projects that are in a specific analysis status.
     *
     * @param status The status.
     * @return The matching projects.
     */
    Collection<ProjectAggregate> findByAnalysisStatus(AnalysisStatus status);

    /**
     * Saves the given project.
     *
     * @param projectAggregate The project.
     * @return The saved project.
     */
    ProjectAggregate save(ProjectAggregate projectAggregate);
}
