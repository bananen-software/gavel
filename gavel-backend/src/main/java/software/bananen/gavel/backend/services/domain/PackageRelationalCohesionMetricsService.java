package software.bananen.gavel.backend.services.domain;

import org.springframework.stereotype.Service;
import software.bananen.gavel.domain.service.RateRelationalCohesionService;
import software.bananen.gavel.infrastructure.persistence.jpa.PackageEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.PackageRelationalCohesionMetricsRepository;
import software.bananen.gavel.infrastructure.persistence.jpa.RelationalCohesionMetricEntity;
import software.bananen.gavel.staticanalysis.RelationalCohesion;

import java.util.HashSet;
import java.util.List;

@Service
public class PackageRelationalCohesionMetricsService {

    private final PackageRelationalCohesionMetricsRepository repository;

    public PackageRelationalCohesionMetricsService(final PackageRelationalCohesionMetricsRepository repository) {
        this.repository = repository;
    }

    public void createOrUpdate(final PackageEntity packageEntity,
                               final RelationalCohesion measurement) {
        RelationalCohesionMetricEntity entity = repository.findByPackageField(packageEntity)
                .orElse(new RelationalCohesionMetricEntity());

        entity.setRating(new RateRelationalCohesionService().rate(measurement.relationalCohesion()));
        entity.setRelationalCohesion(measurement.relationalCohesion());
        entity.setNumberOfInternalRelationships(measurement.numberOfInternalRelationships());
        entity.setNumberOfTypes(measurement.numberOfTypes());

        entity.setPackageField(packageEntity);

        packageEntity.setRelationalCohesionMetrics(new HashSet<>(List.of(entity)));

        repository.save(entity);
    }
}
