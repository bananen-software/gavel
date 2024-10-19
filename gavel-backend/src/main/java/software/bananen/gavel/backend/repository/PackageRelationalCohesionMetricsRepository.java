package software.bananen.gavel.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.RelationalCohesionMetricEntity;

import java.util.Optional;

public interface PackageRelationalCohesionMetricsRepository
        extends JpaRepository<RelationalCohesionMetricEntity, Long> {
    
    Optional<RelationalCohesionMetricEntity> findByPackageField(PackageEntity packageEntity);
}
