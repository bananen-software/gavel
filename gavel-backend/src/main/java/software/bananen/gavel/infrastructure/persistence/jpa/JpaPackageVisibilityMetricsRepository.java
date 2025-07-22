package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaPackageVisibilityMetricsRepository
        extends JpaRepository<JpaVisibilityMetricEntity, Long> {

    Optional<JpaVisibilityMetricEntity> findByPackageField(
            JpaPackageEntity packageEntity);
}
