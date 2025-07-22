package software.bananen.gavel.backend.services.domain;

import org.springframework.stereotype.Service;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaProjectEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaProjectFileEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaProjectFileRepository;

import java.util.Optional;

@Service
public class ProjectFileService {

    private final JpaProjectFileRepository repository;

    public ProjectFileService(final JpaProjectFileRepository repository) {
        this.repository = repository;
    }

    public JpaProjectFileEntity saveOrUpdate(final JpaProjectEntity project,
                                             final String path) {
        final Optional<JpaProjectFileEntity> matchingFile =
                repository.findByProjectAndPath(project, path);

        if (matchingFile.isPresent()) {
            return matchingFile.get();
        } else {
            final JpaProjectFileEntity file = new JpaProjectFileEntity();
            file.setProject(project);
            file.setPath(path);
            return repository.save(file);
        }
    }

    public Optional<JpaProjectFileEntity> findByPath(final JpaProjectEntity projectEntity,
                                                     final String oldPath) {
        return repository.findByProjectAndPath(projectEntity, oldPath);
    }
}
