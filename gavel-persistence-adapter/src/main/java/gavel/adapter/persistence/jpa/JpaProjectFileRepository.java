package gavel.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaProjectFileRepository
        extends JpaRepository<JpaProjectFileEntity, Long> {

    Optional<JpaProjectFileEntity> findByProjectAndPath(JpaProjectEntity project, String path);
}
