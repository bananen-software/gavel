package gavel.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaComponentDependencyMetricsRepository
        extends JpaRepository<JpaComponentDependencyMetricEntity, Long> {

    Optional<JpaComponentDependencyMetricEntity> findByPackageFieldId(long packageId);

    Optional<JpaComponentDependencyMetricEntity> findByPackageField(
            final JpaPackageEntity packageField);
}
