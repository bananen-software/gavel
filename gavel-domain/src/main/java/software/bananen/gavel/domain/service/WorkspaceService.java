package software.bananen.gavel.domain.service;

import software.bananen.gavel.domain.model.*;
import software.bananen.gavel.domain.ports.repositories.WorkspaceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * A service that can be used to work with workspaces.
 */
public final class WorkspaceService {

    private final WorkspaceRepository repository;

    /**
     * Creates a new instance.
     *
     * @param repository The repository that should be used by this service.
     */
    public WorkspaceService(final WorkspaceRepository repository) {
        this.repository = requireNonNull(repository, "The repository may not be null");
    }

    /**
     * Attempts to find an existing workspace by its value or creates a new one.
     *
     * @param name          The value of the workspace.
     * @param path          The path to the workspace.
     * @param excludedPaths The excluded paths.
     * @param basePackage   The base package.
     * @return The workspace.
     */
    public WorkspaceAggregate findOrCreateWorkspace(final WorkspaceNameValueObject name,
                                                    final WorkspacePathValueObject path,
                                                    final List<WorkspaceExcludedPathValueObject> excludedPaths,
                                                    final WorkspaceBasePackageValueObject basePackage) {
        return repository.findByName(name)
                .orElse(new WorkspaceAggregate(new WorkspaceEntity(
                        null,
                        name,
                        path,
                        excludedPaths,
                        basePackage
                ), new ArrayList<>()));
    }

    /**
     * Attempts to find a workspace by its ID.
     *
     * @param id The ID of the workspace.
     * @return The workspace with the given ID or {@link Optional#empty()} if
     * there is none.
     */
    public Optional<WorkspaceAggregate> findById(final WorkspaceIdValueObject id) {
        return repository.findById(id);
    }

    /**
     * Saves the given workspace.
     *
     * @param workspace The workspace that should be saved.
     * @return The workspace.
     */
    public WorkspaceAggregate save(final WorkspaceAggregate workspace) {
        return repository.save(workspace);
    }
}
