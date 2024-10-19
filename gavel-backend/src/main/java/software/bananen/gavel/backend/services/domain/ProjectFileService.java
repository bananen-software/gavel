package software.bananen.gavel.backend.services.domain;

import org.springframework.stereotype.Service;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.backend.entity.ProjectFileEntity;
import software.bananen.gavel.backend.repository.ProjectFileRepository;

import java.util.Optional;

@Service
public class ProjectFileService {

    private final ProjectFileRepository repository;

    public ProjectFileService(final ProjectFileRepository repository) {
        this.repository = repository;
    }

    public ProjectFileEntity saveOrUpdate(final ProjectEntity project,
                                          final String path) {
        final Optional<ProjectFileEntity> matchingFile =
                repository.findByProjectAndPath(project, path);

        if (matchingFile.isPresent()) {
            return matchingFile.get();
        } else {
            final ProjectFileEntity file = new ProjectFileEntity();
            file.setProject(project);
            file.setPath(path);
            return repository.save(file);
        }
    }

    public Optional<ProjectFileEntity> findByPath(final ProjectEntity projectEntity,
                                                  final String oldPath) {
        return repository.findByProjectAndPath(projectEntity, oldPath);
    }
}
