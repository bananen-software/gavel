package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaRelationalCohesionRepository
        extends JpaRepository<JpaRelationalCohesionMetricEntity, Long> {

    Optional<JpaRelationalCohesionMetricEntity> findByPackageFieldId(long packageId);
}
