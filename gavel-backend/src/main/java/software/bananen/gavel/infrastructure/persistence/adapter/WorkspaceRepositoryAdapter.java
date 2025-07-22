package software.bananen.gavel.infrastructure.persistence.adapter;

import org.springframework.stereotype.Service;
import software.bananen.gavel.domain.model.*;
import software.bananen.gavel.domain.ports.driven.WorkspaceRepository;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaProjectEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaProjectRepository;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaWorkspaceEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaWorkspaceRepository;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * An adapter that connects the {@link WorkspaceRepository} interface with the
 * {@link JpaWorkspaceRepository}.
 */
@Service
public final class WorkspaceRepositoryAdapter implements WorkspaceRepository {

    private final JpaWorkspaceRepository repository;
    private final JpaProjectRepository projectRepository;

    /**
     * Creates a new instance.
     *
     * @param repository The jpa repository that should be used.
     */
    public WorkspaceRepositoryAdapter(final JpaWorkspaceRepository repository,
                                      final JpaProjectRepository projectRepository) {
        this.repository =
                requireNonNull(repository, "The repository may not be null");
        this.projectRepository = projectRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkspaceAggregate save(final WorkspaceAggregate workspace) {
        final Optional<Long> workspaceId = Optional.ofNullable(workspace.getAggregateRoot().id())
                .map(WorkspaceIdValueObject::value);

        final var jpaWorkspaceEntity = workspaceId.flatMap(repository::findById).orElse(new JpaWorkspaceEntity());

        jpaWorkspaceEntity.setPath(workspace.getAggregateRoot().path().asStringValue());
        jpaWorkspaceEntity.setName(workspace.getAggregateRoot().name().value());
        jpaWorkspaceEntity.setExcludedPath(workspace.getAggregateRoot()
                .excludedPaths()
                .stream()
                .map(WorkspaceExcludedPathValueObject::value)
                .toList());
        jpaWorkspaceEntity.setBasePackage(workspace.getAggregateRoot().basePackage().value());

        for (final var projectEntity : workspace.listProjects()) {
            final var jpaProjectEntity = jpaWorkspaceEntity.getProjects()
                    .stream()
                    .filter(existingProjectEntity -> Objects.equals(projectEntity.id().value(), existingProjectEntity.getId()))
                    .findFirst()
                    .orElse(new JpaProjectEntity());

            jpaProjectEntity.setWorkspace(jpaWorkspaceEntity);
            jpaProjectEntity.setName(projectEntity.name().value());
            jpaProjectEntity.setPath(projectEntity.path().value().toString());
            jpaProjectEntity.setAnalysisStatus(projectEntity.analysisStatus());
            jpaProjectEntity.setLastAnalyzed(projectEntity.lastAnalyzed());

            if (jpaProjectEntity.getId() == null) {
                jpaWorkspaceEntity.getProjects().add(jpaProjectEntity);
            }
        }

        projectRepository.saveAllAndFlush(jpaWorkspaceEntity.getProjects());
        return toAggregate().apply(repository.saveAndFlush(jpaWorkspaceEntity));
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
    private static Function<JpaWorkspaceEntity, WorkspaceAggregate> toAggregate() {
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
                ws.getProjects()
                        .stream()
                        .map(p -> new ProjectEntity(
                                new ProjectIdValueObject(p.getId()),
                                new ProjectNameValueObject(p.getName()),
                                new ProjectPathValueObject(Paths.get(p.getPath())),
                                p.getAnalysisStatus(),
                                p.getLastAnalyzed()
                        ))
                        .collect(Collectors.toSet())
        );
    }
}
