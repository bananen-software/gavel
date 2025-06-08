package software.bananen.gavel.ports.usecases;

import software.bananen.gavel.domain.model.*;
import software.bananen.gavel.domain.ports.repositories.WorkspaceRepository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * A use case that can be used to create a new workspace.
 */
public final class CreateWorkspaceUseCase {

    private final WorkspaceRepository repository;

    /**
     * Creates a new instance.
     *
     * @param repository The repository that should be used.
     */
    public CreateWorkspaceUseCase(final WorkspaceRepository repository) {
        this.repository = requireNonNull(repository, "The repository may not be null");
    }

    /**
     * Creates a new workspace.
     *
     * @param request The request that should be executed.
     * @return The response.
     */
    public CreateWorkspaceResponseModel createWorkspace(final CreateWorkspaceRequestModel request) {
        try {
            if (request == null) {
                throw new IllegalArgumentException("The request may not be null");
            }

            final var name = new WorkspaceNameValueObject(request.name());
            final var path = new WorkspacePathValueObject(Path.of(request.path()));
            final var basePackage = new WorkspaceBasePackageValueObject(request.basePackage());
            final var excludedPaths =
                    request.excludedPaths()
                            .stream()
                            .map(WorkspaceExcludedPathValueObject::new)
                            .toList();

            final Optional<WorkspaceAggregate> existingWorkspace = repository.findByName(name);

            //TODO: Check if the workspace directory exists

            if (existingWorkspace.isPresent()) {
                return new CreateWorkspaceResponseModel.Failure("The workspace already exists");
            } else {
                final var createdWorkspace = repository.save(
                        new WorkspaceAggregate(new WorkspaceEntity(
                                null, name, path, excludedPaths, basePackage
                        ), new ArrayList<>())
                );

                return new CreateWorkspaceResponseModel.Success(createdWorkspace.getAggregateRoot().id().value());
            }
        } catch (final IllegalArgumentException e) {
            return new CreateWorkspaceResponseModel.Failure(e.getMessage());
        }
    }
}
