package software.bananen.gavel.backend.services.domain;

import gavel.adapter.persistence.jpa.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.domain.model.ClassComplexityRating;
import software.bananen.gavel.domain.model.ClassStatus;
import software.bananen.gavel.domain.service.RatePackageComplexityService;
import gavel.adapter.persistence.jpa.JpaPackageComplexityRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PackageComplexityService {

    private final JpaPackageComplexityRepository repository;

    public PackageComplexityService(@Autowired final JpaPackageComplexityRepository repository) {
        this.repository = repository;
    }

    public void createOrUpdate(final JpaPackageEntity packageEntity) {
        final JpaPackageComplexityEntity packageComplexityEntity =
                packageEntity.getPackageComplexityEntities()
                        .stream()
                        .findFirst()
                        .orElse(new JpaPackageComplexityEntity());

        final Map<ClassComplexityRating, Integer> complexityTypes =
                measureComplexityTypes(packageEntity);

        complexityTypes.values()
                .stream()
                .reduce(Integer::sum)
                .ifPresent(packageEntity::setNumberOfTypes);

        packageEntity.setNumberOfLowComplexityTypes(
                complexityTypes.getOrDefault(ClassComplexityRating.LOW, 0));
        packageEntity.setNumberOfMediumComplexityTypes(
                complexityTypes.getOrDefault(ClassComplexityRating.MEDIUM, 0));
        packageEntity.setNumberOfHighComplexityTypes(
                complexityTypes.getOrDefault(ClassComplexityRating.HIGH, 0));
        packageEntity.setNumberOfVeryHighComplexityTypes(
                complexityTypes.getOrDefault(ClassComplexityRating.VERY_HIGH, 0));

        final int packageComplexity = measurePackageComplexity(packageEntity);

        packageEntity.setComplexity(packageComplexity);
        packageComplexityEntity.setComplexity(packageComplexity);
        packageComplexityEntity.setPackageField(packageEntity);

        packageEntity.setPackageComplexityEntities(
                new LinkedHashSet<>(Set.of(packageComplexityEntity))
        );
        packageEntity.setComplexityRating(
                new RatePackageComplexityService().rate(
                        packageEntity.getNumberOfLowComplexityTypes(),
                        packageEntity.getNumberOfMediumComplexityTypes(),
                        packageEntity.getNumberOfHighComplexityTypes(),
                        packageEntity.getNumberOfVeryHighComplexityTypes()
                )
        );

        repository.save(packageComplexityEntity);
    }

    private int measurePackageComplexity(final JpaPackageEntity packageEntity) {
        int packageComplexity = 0;

        for (final JpaClassEntity classEntity : packageEntity.getClasses()) {
            packageComplexity += classEntity.getClassContributions()
                    .stream()
                    .max(Comparator.comparing(JpaClassContributionEntity::getTimestamp))
                    .flatMap(c -> c.getClassComplexities().stream().findFirst())
                    .map(JpaClassComplexityEntity::getComplexity)
                    .orElse(0);
        }

        return packageComplexity;
    }

    private Map<ClassComplexityRating, Integer> measureComplexityTypes(final JpaPackageEntity packageEntity) {
        final Map<ClassComplexityRating, Integer> result = new ConcurrentHashMap<>();

        for (final JpaClassEntity classEntity :
                packageEntity.getClasses().stream().filter(e -> Objects
                        .equals(e.getStatus(), ClassStatus.ACTIVE)).toList()) {
            classEntity.getClassContributions()
                    .stream()
                    .max(Comparator.comparing(JpaClassContributionEntity::getTimestamp))
                    .flatMap(c -> c.getClassComplexities().stream().findFirst())
                    .map(JpaClassComplexityEntity::getComplexityRating)
                    .ifPresent(rating -> result.merge(rating, 1, Integer::sum));
        }

        return result;
    }
}
