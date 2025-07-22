package software.bananen.gavel.backend.services.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.domain.model.AnalysisStatus;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaProjectEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaProjectRepository;

import java.util.Collection;

import static java.util.Objects.requireNonNull;

/**
 * A service that can be used to interact with projects.
 */
@Service
public class ProjectService {

    private final JpaProjectRepository repository;

    /**
     * Creates a new instance.
     *
     * @param repository The repository that should be used.
     */
    public ProjectService(@Autowired final JpaProjectRepository repository) {
        this.repository = requireNonNull(repository, "The repository may not be null");
    }

    /**
     * Saves the given project entity.
     *
     * @param projectEntity The project entity.
     */
    public void save(final JpaProjectEntity projectEntity) {
        repository.save(projectEntity);
    }

    /**
     * Finds all the projects that are pending for analysis.
     *
     * @return The projects pending analysis.
     */
    public Collection<JpaProjectEntity> findProjectsPendingForAnalysis() {
        return repository.findByAnalysisStatus(AnalysisStatus.PENDING);
    }
}
