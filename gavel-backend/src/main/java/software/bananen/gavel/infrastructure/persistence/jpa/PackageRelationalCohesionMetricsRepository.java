package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PackageRelationalCohesionMetricsRepository
        extends JpaRepository<RelationalCohesionMetricEntity, Long> {

    Optional<RelationalCohesionMetricEntity> findByPackageField(PackageEntity packageEntity);
}
