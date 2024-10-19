package software.bananen.gavel.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.bananen.gavel.backend.entity.CumulativeComponentDependencyEntity;
import software.bananen.gavel.backend.entity.PackageEntity;

import java.util.Optional;

public interface PackageCumulativeComponentDependencyMetricsRepository
        extends JpaRepository<CumulativeComponentDependencyEntity, Long> {

    Optional<CumulativeComponentDependencyEntity> findByPackageField(final PackageEntity packageEntity);
}
