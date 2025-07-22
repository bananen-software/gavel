package gavel.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaPackageCumulativeComponentDependencyMetricsRepository
        extends JpaRepository<JpaCumulativeComponentDependencyEntity, Long> {

    Optional<JpaCumulativeComponentDependencyEntity> findByPackageField(final JpaPackageEntity packageEntity);
}
