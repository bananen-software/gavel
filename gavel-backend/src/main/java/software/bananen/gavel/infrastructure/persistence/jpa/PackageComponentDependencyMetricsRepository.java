package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PackageComponentDependencyMetricsRepository
        extends JpaRepository<ComponentDependencyMetricEntity, Long> {

    Optional<ComponentDependencyMetricEntity> findByPackageField(
            final PackageEntity packageField);
}
