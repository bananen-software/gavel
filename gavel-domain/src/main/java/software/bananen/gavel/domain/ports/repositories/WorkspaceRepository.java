package software.bananen.gavel.domain.ports.repositories;

import software.bananen.gavel.domain.model.WorkspaceAggregate;
import software.bananen.gavel.domain.model.WorkspaceIdValueObject;
import software.bananen.gavel.domain.model.WorkspaceNameValueObject;

import java.util.Optional;

/**
 * A repository that can be used to access workspaces.
 */
public interface WorkspaceRepository {

    /**
     * Saves the given workspace.
     *
     * @param workspace The workspace that should be saved.
     * @return The saved workspace.
     */
    WorkspaceAggregate save(final WorkspaceAggregate workspace);

    /**
     * Attempts to look up the workspace by its ID.
     *
     * @param id The ID of the workspace.
     * @return The workspace for the given ID or {@link Optional#empty()} if
     * there is none.
     */
    Optional<WorkspaceAggregate> findById(final WorkspaceIdValueObject id);

    /**
     * Attempts to find a workspace by its value.
     *
     * @param name The value of the workspace.
     * @return The workspace with the given value or {@link Optional#empty()} if
     * there is none.
     */
    Optional<WorkspaceAggregate> findByName(final WorkspaceNameValueObject name);
}
