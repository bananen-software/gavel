package software.bananen.gavel.infrastructure.persistence.jpa;

import gavel.adapter.persistence.jpa.JpaProjectEntity;
import gavel.adapter.persistence.jpa.JpaProjectFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaProjectFileRepository
        extends JpaRepository<JpaProjectFileEntity, Long> {

    Optional<JpaProjectFileEntity> findByProjectAndPath(JpaProjectEntity project, String path);
}
