package software.bananen.gavel.infrastructure.persistence.jpa;

import gavel.adapter.persistence.jpa.JpaClassCohesionEntity;
import gavel.adapter.persistence.jpa.JpaClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaClassCohesionRepository
        extends JpaRepository<JpaClassCohesionEntity, Long> {

    Optional<JpaClassCohesionEntity> findByClassField(final JpaClassEntity classEntity);
}
