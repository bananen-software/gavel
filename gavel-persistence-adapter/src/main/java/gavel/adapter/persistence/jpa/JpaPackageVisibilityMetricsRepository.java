package gavel.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaPackageVisibilityMetricsRepository
        extends JpaRepository<JpaVisibilityMetricEntity, Long> {

    Optional<JpaVisibilityMetricEntity> findByPackageField(
            JpaPackageEntity packageEntity);
}
