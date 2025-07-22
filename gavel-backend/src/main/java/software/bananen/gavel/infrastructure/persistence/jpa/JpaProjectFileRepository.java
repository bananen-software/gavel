package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaProjectFileRepository
        extends JpaRepository<JpaProjectFileEntity, Long> {

    Optional<JpaProjectFileEntity> findByProjectAndPath(JpaProjectEntity project, String path);
}
