package software.bananen.gavel.backend.services.domain;

import gavel.adapter.persistence.jpa.JpaPackageEntity;
import gavel.adapter.persistence.jpa.JpaVisibilityMetricEntity;
import org.springframework.stereotype.Service;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaPackageVisibilityMetricsRepository;
import software.bananen.gavel.staticanalysis.ComponentVisibility;

import java.util.HashSet;
import java.util.List;

@Service
public class PackageVisibilityMetricsService {

    private final JpaPackageVisibilityMetricsRepository repository;

    public PackageVisibilityMetricsService(
            final JpaPackageVisibilityMetricsRepository repository) {
        this.repository = repository;
    }

    public void saveOrUpdate(final JpaPackageEntity packageEntity,
                             ComponentVisibility measurement) {
        final JpaVisibilityMetricEntity entity =
                repository.findByPackageField(packageEntity)
                        .orElse(new JpaVisibilityMetricEntity());

        entity.setAverageRelativeVisibility(measurement.averageRelativeVisibility());
        entity.setGlobalRelativeVisibility(measurement.globalRelativeVisibility());
        entity.setRelativeVisibility(measurement.relativeVisibility());

        entity.setPackageField(packageEntity);
        packageEntity.setVisibilityMetrics(new HashSet<>(List.of(entity)));

        repository.save(entity);
    }
}
