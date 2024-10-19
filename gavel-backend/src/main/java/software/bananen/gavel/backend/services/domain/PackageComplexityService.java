package software.bananen.gavel.backend.services.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.backend.domain.ClassStatus;
import software.bananen.gavel.backend.domain.ComplexityRating;
import software.bananen.gavel.backend.domain.PackageComplexity;
import software.bananen.gavel.backend.entity.*;
import software.bananen.gavel.backend.repository.PackageComplexityRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PackageComplexityService {

    private final PackageComplexityRepository repository;

    public PackageComplexityService(@Autowired final PackageComplexityRepository repository) {
        this.repository = repository;
    }

    public void createOrUpdate(final PackageEntity packageEntity) {
        final PackageComplexityEntity packageComplexityEntity =
                packageEntity.getPackageComplexityEntities()
                        .stream()
                        .findFirst()
                        .orElse(new PackageComplexityEntity());

        final Map<ComplexityRating, Integer> complexityTypes =
                measureComplexityTypes(packageEntity);

        complexityTypes.values()
                .stream()
                .reduce(Integer::sum)
                .ifPresent(packageEntity::setNumberOfTypes);

        packageEntity.setNumberOfLowComplexityTypes(
                complexityTypes.getOrDefault(ComplexityRating.LOW, 0));
        packageEntity.setNumberOfMediumComplexityTypes(
                complexityTypes.getOrDefault(ComplexityRating.MEDIUM, 0));
        packageEntity.setNumberOfHighComplexityTypes(
                complexityTypes.getOrDefault(ComplexityRating.HIGH, 0));
        packageEntity.setNumberOfVeryHighComplexityTypes(
                complexityTypes.getOrDefault(ComplexityRating.VERY_HIGH, 0));

        final int packageComplexity = measurePackageComplexity(packageEntity);

        packageEntity.setComplexity(packageComplexity);
        packageComplexityEntity.setComplexity(packageComplexity);
        packageComplexityEntity.setPackageField(packageEntity);

        packageEntity.setPackageComplexityEntities(
                new LinkedHashSet<>(Set.of(packageComplexityEntity))
        );
        packageEntity.setComplexityRating(
                PackageComplexity.determine(
                        packageEntity.getNumberOfLowComplexityTypes(),
                        packageEntity.getNumberOfMediumComplexityTypes(),
                        packageEntity.getNumberOfHighComplexityTypes(),
                        packageEntity.getNumberOfVeryHighComplexityTypes()
                )
        );

        repository.save(packageComplexityEntity);
    }

    private int measurePackageComplexity(final PackageEntity packageEntity) {
        int packageComplexity = 0;

        for (final ClassEntity classEntity : packageEntity.getClasses()) {
            packageComplexity += classEntity.getClassContributions()
                    .stream()
                    .max(Comparator.comparing(ClassContributionEntity::getTimestamp))
                    .flatMap(c -> c.getClassComplexities().stream().findFirst())
                    .map(ClassComplexityEntity::getComplexity)
                    .orElse(0);
        }

        return packageComplexity;
    }

    private Map<ComplexityRating, Integer> measureComplexityTypes(final PackageEntity packageEntity) {
        final Map<ComplexityRating, Integer> result = new ConcurrentHashMap<>();

        for (final ClassEntity classEntity :
                packageEntity.getClasses().stream().filter(e -> Objects
                        .equals(e.getStatus(), ClassStatus.ACTIVE)).toList()) {
            classEntity.getClassContributions()
                    .stream()
                    .max(Comparator.comparing(ClassContributionEntity::getTimestamp))
                    .flatMap(c -> c.getClassComplexities().stream().findFirst())
                    .map(ClassComplexityEntity::getComplexityRating)
                    .ifPresent(rating -> result.merge(rating, 1, Integer::sum));
        }

        return result;
    }
}
