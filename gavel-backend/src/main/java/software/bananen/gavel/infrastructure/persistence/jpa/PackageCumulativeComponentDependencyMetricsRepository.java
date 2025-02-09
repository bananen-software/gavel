package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PackageCumulativeComponentDependencyMetricsRepository
        extends JpaRepository<CumulativeComponentDependencyEntity, Long> {

    Optional<CumulativeComponentDependencyEntity> findByPackageField(final PackageEntity packageEntity);
}
