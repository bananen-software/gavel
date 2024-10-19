package software.bananen.gavel.backend.services.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.backend.entity.ComponentDependencyMetricEntity;
import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.repository.PackageComponentDependencyMetricsRepository;
import software.bananen.gavel.staticanalysis.ComponentDependency;

import java.util.HashSet;
import java.util.Set;

@Service
public class PackageComponentDependencyMetricsService {

    private final PackageComponentDependencyMetricsRepository repository;

    public PackageComponentDependencyMetricsService(
            @Autowired PackageComponentDependencyMetricsRepository repository) {
        this.repository = repository;
    }

    public void createOrUpdate(final PackageEntity packageEntity,
                               final ComponentDependency measurement) {
        final ComponentDependencyMetricEntity matchingEntity =
                repository.findByPackageField(packageEntity)
                        .orElse(new ComponentDependencyMetricEntity());

        matchingEntity.setAbstractness(measurement.abstractness());
        matchingEntity.setInstability(measurement.instability());
        matchingEntity.setDistance(measurement.normalizedDistanceFromMainSequence());
        matchingEntity.setAfferentCoupling(measurement.afferentCoupling());
        matchingEntity.setEfferentCoupling(measurement.efferentCoupling());

        matchingEntity.setPackageField(packageEntity);
        packageEntity.setComponentDependencyMetrics(new HashSet<>(Set.of(matchingEntity)));

        repository.save(matchingEntity);

    }
}
