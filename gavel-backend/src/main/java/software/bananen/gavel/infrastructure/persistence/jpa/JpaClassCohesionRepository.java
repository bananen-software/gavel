package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaClassCohesionRepository
        extends JpaRepository<JpaClassCohesionEntity, Long> {

    Optional<JpaClassCohesionEntity> findByClassField(final JpaClassEntity classEntity);
}
