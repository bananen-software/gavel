package software.bananen.gavel.backend.services.domain;

import org.springframework.stereotype.Service;
import software.bananen.gavel.backend.entity.CumulativeComponentDependencyEntity;
import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.repository.PackageCumulativeComponentDependencyMetricsRepository;
import software.bananen.gavel.staticanalysis.CumulativeComponentDependency;

import java.util.HashSet;
import java.util.List;

@Service
public class PackageCumulativeComponentDependencyMetricsService {

    private final PackageCumulativeComponentDependencyMetricsRepository repository;

    public PackageCumulativeComponentDependencyMetricsService(
            final PackageCumulativeComponentDependencyMetricsRepository repository) {
        this.repository = repository;
    }

    public void createOrUpdate(final PackageEntity packageEntity,
                               final CumulativeComponentDependency measurement) {
        final CumulativeComponentDependencyEntity entity =
                repository.findByPackageField(packageEntity)
                        .orElse(new CumulativeComponentDependencyEntity());

        entity.setCumulative(measurement.cumulative());
        entity.setAverage(measurement.average());
        entity.setNormalized(measurement.normalized());
        entity.setRelativeAverage(measurement.relativeAverage());

        entity.setPackageField(packageEntity);
        packageEntity.setCumulativeComponentDependencies(new HashSet<>(List.of(entity)));

        repository.save(entity);
    }
}
