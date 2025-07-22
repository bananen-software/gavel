package software.bananen.gavel.infrastructure.persistence.jpa;

import gavel.adapter.persistence.jpa.JpaCumulativeComponentDependencyEntity;
import gavel.adapter.persistence.jpa.JpaPackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaPackageCumulativeComponentDependencyMetricsRepository
        extends JpaRepository<JpaCumulativeComponentDependencyEntity, Long> {

    Optional<JpaCumulativeComponentDependencyEntity> findByPackageField(final JpaPackageEntity packageEntity);
}
