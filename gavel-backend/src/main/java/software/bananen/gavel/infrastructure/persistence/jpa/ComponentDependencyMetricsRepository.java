package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComponentDependencyMetricsRepository
        extends JpaRepository<ComponentDependencyMetricEntity, Long> {

    Optional<ComponentDependencyMetricEntity> findByPackageFieldId(long packageId);

    Optional<ComponentDependencyMetricEntity> findByPackageField(
            final PackageEntity packageField);
}
