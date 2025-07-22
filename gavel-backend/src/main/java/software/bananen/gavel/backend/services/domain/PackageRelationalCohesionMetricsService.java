package software.bananen.gavel.backend.services.domain;

import gavel.adapter.persistence.jpa.JpaPackageEntity;
import gavel.adapter.persistence.jpa.JpaRelationalCohesionMetricEntity;
import org.springframework.stereotype.Service;
import software.bananen.gavel.domain.service.RateRelationalCohesionService;
import gavel.adapter.persistence.jpa.JpaPackageRelationalCohesionMetricsRepository;
import software.bananen.gavel.staticanalysis.RelationalCohesion;

import java.util.HashSet;
import java.util.List;

@Service
public class PackageRelationalCohesionMetricsService {

    private final JpaPackageRelationalCohesionMetricsRepository repository;

    public PackageRelationalCohesionMetricsService(final JpaPackageRelationalCohesionMetricsRepository repository) {
        this.repository = repository;
    }

    public void createOrUpdate(final JpaPackageEntity packageEntity,
                               final RelationalCohesion measurement) {
        JpaRelationalCohesionMetricEntity entity = repository.findByPackageField(packageEntity)
                .orElse(new JpaRelationalCohesionMetricEntity());

        entity.setRating(new RateRelationalCohesionService().rate(measurement.relationalCohesion()));
        entity.setRelationalCohesion(measurement.relationalCohesion());
        entity.setNumberOfInternalRelationships(measurement.numberOfInternalRelationships());
        entity.setNumberOfTypes(measurement.numberOfTypes());

        entity.setPackageField(packageEntity);

        packageEntity.setRelationalCohesionMetrics(new HashSet<>(List.of(entity)));

        repository.save(entity);
    }
}
