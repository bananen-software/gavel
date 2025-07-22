package gavel.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaClassCohesionRepository
        extends JpaRepository<JpaClassCohesionEntity, Long> {

    Optional<JpaClassCohesionEntity> findByClassField(final JpaClassEntity classEntity);
}
