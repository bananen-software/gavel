package software.bananen.gavel.backend.services.domain;

import org.springframework.stereotype.Service;
import software.bananen.gavel.infrastructure.persistence.jpa.PackageEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.PackageVisibilityMetricsRepository;
import software.bananen.gavel.infrastructure.persistence.jpa.VisibilityMetricEntity;
import software.bananen.gavel.staticanalysis.ComponentVisibility;

import java.util.HashSet;
import java.util.List;

@Service
public class PackageVisibilityMetricsService {

    private final PackageVisibilityMetricsRepository repository;

    public PackageVisibilityMetricsService(
            final PackageVisibilityMetricsRepository repository) {
        this.repository = repository;
    }

    public void saveOrUpdate(final PackageEntity packageEntity,
                             ComponentVisibility measurement) {
        final VisibilityMetricEntity entity =
                repository.findByPackageField(packageEntity)
                        .orElse(new VisibilityMetricEntity());

        entity.setAverageRelativeVisibility(measurement.averageRelativeVisibility());
        entity.setGlobalRelativeVisibility(measurement.globalRelativeVisibility());
        entity.setRelativeVisibility(measurement.relativeVisibility());

        entity.setPackageField(packageEntity);
        packageEntity.setVisibilityMetrics(new HashSet<>(List.of(entity)));

        repository.save(entity);
    }
}
