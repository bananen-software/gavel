package software.bananen.gavel.infrastructure.persistence.jpa;

import gavel.adapter.persistence.jpa.JpaComponentDependencyMetricEntity;
import gavel.adapter.persistence.jpa.JpaPackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaComponentDependencyMetricsRepository
        extends JpaRepository<JpaComponentDependencyMetricEntity, Long> {

    Optional<JpaComponentDependencyMetricEntity> findByPackageFieldId(long packageId);

    Optional<JpaComponentDependencyMetricEntity> findByPackageField(
            final JpaPackageEntity packageField);
}
