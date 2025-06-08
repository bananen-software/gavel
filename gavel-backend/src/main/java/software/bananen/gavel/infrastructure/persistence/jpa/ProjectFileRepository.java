package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectFileRepository
        extends JpaRepository<ProjectFileEntity, Long> {

    Optional<ProjectFileEntity> findByProjectAndPath(ProjectEntity project, String path);
}
