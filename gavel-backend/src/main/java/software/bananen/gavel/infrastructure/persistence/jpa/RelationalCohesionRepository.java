package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RelationalCohesionRepository
        extends JpaRepository<RelationalCohesionMetricEntity, Long> {

    Optional<RelationalCohesionMetricEntity> findByPackageFieldId(long packageId);
}
