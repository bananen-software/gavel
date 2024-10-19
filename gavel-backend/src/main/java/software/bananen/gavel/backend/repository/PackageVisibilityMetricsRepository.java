package software.bananen.gavel.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.VisibilityMetricEntity;

import java.util.Optional;

public interface PackageVisibilityMetricsRepository
        extends JpaRepository<VisibilityMetricEntity, Long> {

    Optional<VisibilityMetricEntity> findByPackageField(
            PackageEntity packageEntity);
}
