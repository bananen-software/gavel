package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PackageVisibilityMetricsRepository
        extends JpaRepository<VisibilityMetricEntity, Long> {

    Optional<VisibilityMetricEntity> findByPackageField(
            PackageEntity packageEntity);
}
