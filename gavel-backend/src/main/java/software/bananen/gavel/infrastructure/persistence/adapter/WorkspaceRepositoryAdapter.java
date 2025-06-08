package software.bananen.gavel.infrastructure.persistence.adapter;

import org.springframework.stereotype.Service;
import software.bananen.gavel.domain.model.*;
import software.bananen.gavel.domain.ports.driven.WorkspaceRepository;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaWorkspaceRepository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * An adapter that connects the {@link WorkspaceRepository} interface with the
 * {@link JpaWorkspaceRepository}.
 */
@Service
public final class WorkspaceRepositoryAdapter implements WorkspaceRepository {

    private final JpaWorkspaceRepository repository;

    /**
     * Creates a new instance.
     *
     * @param repository The jpa repository that should be used.
     */
    public WorkspaceRepositoryAdapter(final JpaWorkspaceRepository repository) {
        this.repository =
                requireNonNull(repository, "The repository may not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkspaceAggregate save(final WorkspaceAggregate workspace) {
        final Optional<Long> workspaceId =
                Optional.ofNullable(workspace.getAggregateRoot().id())
                        .map(WorkspaceIdValueObject::value);

        final var entity =
                workspaceId.flatMap(repository::findById)
                        .orElse(new software.bananen.gavel.infrastructure.persistence.jpa.WorkspaceEntity());

        entity.setName(workspace.getAggregateRoot().name().value());
        entity.setExcludedPath(workspace.getAggregateRoot()
                .excludedPaths()
                .stream()
                .map(WorkspaceExcludedPathValueObject::value)
                .toList());
        entity.setBasePackage(workspace.getAggregateRoot().basePackage().value());

        for (final var project : workspace.listProjects()) {
            if (project.id() == null) {
                final var newProject = new software.bananen.gavel.infrastructure.persistence.jpa.ProjectEntity();

                newProject.setWorkspace(entity);
                newProject.setName(project.name().value());
                newProject.setPath(project.path().value().toString());
                newProject.setAnalysisStatus(project.analysisStatus());
                newProject.setLastAnalyzed(project.lastAnalyzed());

                entity.getProjects().add(newProject);
            } else {
                entity.getProjects()
                        .stream()
                        .filter(projectEntity -> Objects.equals(project.id().value(), projectEntity.getId()))
                        .findFirst()
                        .ifPresent(projectEntity -> {
                            projectEntity.setWorkspace(entity);
                            projectEntity.setName(project.name().value());
                            projectEntity.setPath(project.path().value().toString());
                            projectEntity.setAnalysisStatus(project.analysisStatus());
                            projectEntity.setLastAnalyzed(project.lastAnalyzed());
                        });
            }
        }

        return toAggregate().apply(repository.save(entity));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<WorkspaceAggregate> findById(final WorkspaceIdValueObject id) {
        return repository.findById(id.value()).map(toAggregate());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<WorkspaceAggregate> findByName(final WorkspaceNameValueObject name) {
        return repository.findByName(name.value()).map(toAggregate());
    }

    /**
     * A function that maps a workspace entity to a workspace aggregate.
     *
     * @return The mapping function.
     */
    private static Function<software.bananen.gavel.infrastructure.persistence.jpa.WorkspaceEntity, WorkspaceAggregate> toAggregate() {
        return ws -> new WorkspaceAggregate(
                new WorkspaceEntity(
                        Optional.ofNullable(ws.getId()).map(WorkspaceIdValueObject::new).orElse(null),
                        new WorkspaceNameValueObject(ws.getName()),
                        new WorkspacePathValueObject(Path.of(ws.getPath())),
                        ws.getExcludedPath().stream()
                                .map(WorkspaceExcludedPathValueObject::new)
                                .toList(),
                        new WorkspaceBasePackageValueObject(ws.getBasePackage())
                ),
                new ArrayList<>()
        );
    }
}
