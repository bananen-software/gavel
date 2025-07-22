package gavel.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaPackageRelationalCohesionMetricsRepository
        extends JpaRepository<JpaRelationalCohesionMetricEntity, Long> {

    Optional<JpaRelationalCohesionMetricEntity> findByPackageField(JpaPackageEntity packageEntity);
}
