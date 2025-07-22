package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaPackageRelationalCohesionMetricsRepository
        extends JpaRepository<JpaRelationalCohesionMetricEntity, Long> {

    Optional<JpaRelationalCohesionMetricEntity> findByPackageField(JpaPackageEntity packageEntity);
}
