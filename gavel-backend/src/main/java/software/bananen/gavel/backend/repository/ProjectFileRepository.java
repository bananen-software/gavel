package software.bananen.gavel.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.backend.entity.ProjectFileEntity;

import java.util.Optional;

public interface ProjectFileRepository
        extends JpaRepository<ProjectFileEntity, Long> {

    Optional<ProjectFileEntity> findByProjectAndPath(ProjectEntity project, String path);
}
