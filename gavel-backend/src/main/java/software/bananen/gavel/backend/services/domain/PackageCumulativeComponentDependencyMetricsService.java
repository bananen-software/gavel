package software.bananen.gavel.backend.services.domain;

import org.springframework.stereotype.Service;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaCumulativeComponentDependencyEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaPackageCumulativeComponentDependencyMetricsRepository;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaPackageEntity;
import software.bananen.gavel.staticanalysis.CumulativeComponentDependency;

import java.util.HashSet;
import java.util.List;

@Service
public class PackageCumulativeComponentDependencyMetricsService {

    private final JpaPackageCumulativeComponentDependencyMetricsRepository repository;

    public PackageCumulativeComponentDependencyMetricsService(
            final JpaPackageCumulativeComponentDependencyMetricsRepository repository) {
        this.repository = repository;
    }

    public void createOrUpdate(final JpaPackageEntity packageEntity,
                               final CumulativeComponentDependency measurement) {
        final JpaCumulativeComponentDependencyEntity entity =
                repository.findByPackageField(packageEntity)
                        .orElse(new JpaCumulativeComponentDependencyEntity());

        entity.setCumulative(measurement.cumulative());
        entity.setAverage(measurement.average());
        entity.setNormalized(measurement.normalized());
        entity.setRelativeAverage(measurement.relativeAverage());

        entity.setPackageField(packageEntity);
        packageEntity.setCumulativeComponentDependencies(new HashSet<>(List.of(entity)));

        repository.save(entity);
    }
}
