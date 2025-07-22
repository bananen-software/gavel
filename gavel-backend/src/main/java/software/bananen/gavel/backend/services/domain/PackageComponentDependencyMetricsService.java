package software.bananen.gavel.backend.services.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaComponentDependencyMetricEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaComponentDependencyMetricsRepository;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaPackageEntity;
import software.bananen.gavel.staticanalysis.ComponentDependency;

import java.util.HashSet;
import java.util.Set;

@Service
public class PackageComponentDependencyMetricsService {

    private final JpaComponentDependencyMetricsRepository repository;

    public PackageComponentDependencyMetricsService(
            @Autowired JpaComponentDependencyMetricsRepository repository) {
        this.repository = repository;
    }

    public void createOrUpdate(final JpaPackageEntity packageEntity,
                               final ComponentDependency measurement) {
        final JpaComponentDependencyMetricEntity matchingEntity =
                repository.findByPackageField(packageEntity)
                        .orElse(new JpaComponentDependencyMetricEntity());

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
