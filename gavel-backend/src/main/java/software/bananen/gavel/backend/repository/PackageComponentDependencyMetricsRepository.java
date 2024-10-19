package software.bananen.gavel.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.bananen.gavel.backend.entity.ComponentDependencyMetricEntity;
import software.bananen.gavel.backend.entity.PackageEntity;

import java.util.Optional;

public interface PackageComponentDependencyMetricsRepository
        extends JpaRepository<ComponentDependencyMetricEntity, Long> {

    Optional<ComponentDependencyMetricEntity> findByPackageField(
            final PackageEntity packageField);
}
