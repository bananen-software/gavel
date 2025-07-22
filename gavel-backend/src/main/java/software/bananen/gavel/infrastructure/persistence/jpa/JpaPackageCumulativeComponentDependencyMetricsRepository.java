package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaPackageCumulativeComponentDependencyMetricsRepository
        extends JpaRepository<JpaCumulativeComponentDependencyEntity, Long> {

    Optional<JpaCumulativeComponentDependencyEntity> findByPackageField(final JpaPackageEntity packageEntity);
}
