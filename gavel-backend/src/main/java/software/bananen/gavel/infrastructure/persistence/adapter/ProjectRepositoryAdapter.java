package software.bananen.gavel.infrastructure.persistence.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.domain.model.AnalysisStatus;
import software.bananen.gavel.domain.model.ProjectAggregate;
import software.bananen.gavel.domain.model.ProjectEntity;
import software.bananen.gavel.domain.model.ProjectIdValueObject;
import software.bananen.gavel.domain.model.ProjectNameValueObject;
import software.bananen.gavel.domain.model.ProjectPathValueObject;
import software.bananen.gavel.domain.ports.repositories.ProjectRepository;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaProjectRepository;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * An adapter that bridges the {@link ProjectRepository} and the
 * {@link JpaProjectRepository}
 */
@Service
public final class ProjectRepositoryAdapter implements ProjectRepository {

    private final JpaProjectRepository repository;

    /**
     * Creates a new instance.
     *
     * @param repository The JPA repository.
     */
    public ProjectRepositoryAdapter(@Autowired JpaProjectRepository repository) {
        this.repository = requireNonNull(repository, "The repository may not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ProjectAggregate> findById(final ProjectIdValueObject id) {
        return repository.findById(id.value())
                .map(toAggregate());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ProjectAggregate> findByAnalysisStatus(final AnalysisStatus status) {
        return repository.findByAnalysisStatus(status)
                .stream()
                .map(toAggregate())
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectAggregate save(final ProjectAggregate projectAggregate) {
        final var entity =
                Optional.ofNullable(projectAggregate.aggregateRoot().id())
                        .map(ProjectIdValueObject::value)
                        .flatMap(repository::findById)
                        .orElse(new software.bananen.gavel.infrastructure.persistence.jpa.ProjectEntity());

        entity.setName(projectAggregate.aggregateRoot().name().value());
        entity.setPath(projectAggregate.aggregateRoot().path().value().toString());
        entity.setAnalysisStatus(projectAggregate.aggregateRoot().analysisStatus());
        entity.setLastAnalyzed(projectAggregate.aggregateRoot().lastAnalyzed());

        return toAggregate().apply(repository.save(entity));
    }

    /**
     * A mapping function that maps the JPA entity to its aggregate representation.
     *
     * @return The mapping function.
     */
    private static Function<software.bananen.gavel.infrastructure.persistence.jpa.ProjectEntity, ProjectAggregate> toAggregate() {
        return p -> new ProjectAggregate(
                new ProjectEntity(
                        new ProjectIdValueObject(p.getId()),
                        new ProjectNameValueObject(p.getName()),
                        new ProjectPathValueObject(Path.of(p.getPath())),
                        p.getAnalysisStatus(),
                        p.getLastAnalyzed()
                )
        );
    }
}
