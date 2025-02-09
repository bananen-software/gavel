package software.bananen.gavel.domain.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * An aggregate that represents a workspace.
 */
public record WorkspaceAggregate(WorkspaceEntity aggregateRoot,
                                 Collection<ProjectEntity> projects) {

    /**
     * Creates a new instance.
     *
     * @param aggregateRoot The aggregate root.
     */
    public WorkspaceAggregate {
        requireNonNull(aggregateRoot, "The aggregate root may not be null");
        requireNonNull(projects, "The projects may not be null");
    }

    /**
     * Retrieves the aggregate root.
     *
     * @return The aggregate root.
     */
    public WorkspaceEntity getAggregateRoot() {
        return aggregateRoot;
    }

    /**
     * Renames the aggregate root.
     *
     * @param newName The new value.
     */
    public WorkspaceAggregate rename(final WorkspaceNameValueObject newName) {
        return new WorkspaceAggregate(aggregateRoot.rename(newName), projects);
    }

    /**
     * Lists the projects contained in the workspace.
     *
     * @return The projects.
     */
    public Collection<ProjectEntity> listProjects() {
        return new ArrayList<>(projects);
    }

    /**
     * Checks whether the workspace currently does not have a project with the given name.
     *
     * @param name The name to check.
     * @return True if the workspace does not have a project with the given name, otherwise false.
     */
    public boolean doesNotHaveProjectWithName(final ProjectNameValueObject name) {
        return listProjects().stream().noneMatch(p -> Objects.equals(p.name(), name));
    }

    /**
     * Adds the given project to the workspace.
     *
     * @param name The name of the project.
     * @param path The path to the project.
     * @return The updated workspace.
     */
    public WorkspaceAggregate addProject(final ProjectNameValueObject name,
                                         final ProjectPathValueObject path) {
        final var updatedProjects = new ArrayList<>(projects);

        updatedProjects.add(new ProjectEntity(null, name, path, AnalysisStatus.NOT_RUN, null));

        return new WorkspaceAggregate(aggregateRoot, updatedProjects);
    }
}
