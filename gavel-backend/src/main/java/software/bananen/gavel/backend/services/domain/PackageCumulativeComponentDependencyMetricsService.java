package software.bananen.gavel.backend.services.domain;

import gavel.adapter.persistence.jpa.JpaCumulativeComponentDependencyEntity;
import gavel.adapter.persistence.jpa.JpaPackageEntity;
import org.springframework.stereotype.Service;
import gavel.adapter.persistence.jpa.JpaPackageCumulativeComponentDependencyMetricsRepository;
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
