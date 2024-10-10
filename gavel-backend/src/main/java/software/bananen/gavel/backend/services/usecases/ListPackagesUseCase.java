package software.bananen.gavel.backend.services.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.backend.domain.ComplexityRating;
import software.bananen.gavel.backend.domain.Size;
import software.bananen.gavel.backend.entity.*;
import software.bananen.gavel.backend.repository.ProjectRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ListPackagesUseCase {

    private final ProjectRepository projectRepository;

    public ListPackagesUseCase(@Autowired final ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Optional<Collection<PackageOverviewResponseModel>> load() {
        Optional<ProjectEntity> project = projectRepository.findAll()
                .stream()
                .findFirst();

        if (project.isEmpty()) {
            return Optional.empty();
        }

        final Collection<PackageOverviewResponseModel> result = new ArrayList<>();

        for (final PackageEntity packageEntity : project.get().getPackages()) {
            final ClassComplexityRatingResponseModel ratingResponseModel =
                    mapToComplexityRatingResponseModel(packageEntity);

            result.add(new PackageOverviewResponseModel(
                    packageEntity.getPackageName(),
                    packageEntity.getPackageComplexityEntities()
                            .stream()
                            .findFirst()
                            .map(PackageComplexityEntity::getComplexity)
                            .orElse(0),
                    packageEntity.getPackageLinesOfCodeEntities()
                            .stream()
                            .findFirst()
                            .map(PackageLinesOfCodeEntity::getTotalLinesOfCode)
                            .orElse(0),
                    packageEntity.getPackageLinesOfCodeEntities()
                            .stream()
                            .findFirst()
                            .map(PackageLinesOfCodeEntity::getTotalLinesOfComment)
                            .orElse(0),
                    packageEntity.getPackageLinesOfCodeEntities()
                            .stream()
                            .findFirst()
                            .map(PackageLinesOfCodeEntity::getCommentToCodeRatio)
                            .orElse(0.0),
                    packageEntity.getPackageLinesOfCodeEntities()
                            .stream()
                            .findFirst()
                            .map(PackageLinesOfCodeEntity::getPackageSize)
                            .orElse(Size.UNKNOWN),
                    determinePackageComplexity(ratingResponseModel), packageEntity.getClasses().size(),
                    ratingResponseModel
            ));
        }

        return Optional.of(result);
    }

    public static ClassComplexityRatingResponseModel mapToComplexityRatingResponseModel(PackageEntity packageEntity) {
        final Map<ComplexityRating, Integer> counter = new ConcurrentHashMap<>();

        for (final ClassEntity classEntity : packageEntity.getClasses()) {
            classEntity.getClassContributions()
                    .stream()
                    .max(Comparator.comparing(ClassContributionEntity::getTimestamp))
                    .flatMap(classContributionEntity -> classContributionEntity.getClassComplexities()
                            .stream()
                            .findFirst())
                    .ifPresent(classComplexityEntity -> {
                        counter.put(classComplexityEntity.getComplexityRating(),
                                counter.getOrDefault(classComplexityEntity.getComplexityRating(), 0) + 1);
                    });
        }


        final Map<ComplexityRating, Double> percentages = new ConcurrentHashMap<>();

        for (final ComplexityRating key : ComplexityRating.values()) {
            final double percentage = packageEntity.getClasses().isEmpty()
                    ? 0
                    : counter.getOrDefault(key, 0) / (double) packageEntity.getClasses().size();

            percentages.put(key, percentage);
        }

        return new ClassComplexityRatingResponseModel(
                percentages.getOrDefault(ComplexityRating.LOW, 0.0),
                percentages.getOrDefault(ComplexityRating.MEDIUM, 0.0),
                percentages.getOrDefault(ComplexityRating.HIGH, 0.0),
                percentages.getOrDefault(ComplexityRating.VERY_HIGH, 0.0)
        );
    }

    public static PackageComplexity determinePackageComplexity(
            final ClassComplexityRatingResponseModel rating) {
        if (rating.lowComplexityPercentage() + rating.mediumComplexityPercentage() > 0.7) {
            return PackageComplexity.MOSTLY_SIMPLE;
        } else if (rating.highComplexityPercentage() + rating.veryHighComplexityPercentage() > 0.3) {
            return PackageComplexity.COMPLEX;
        } else if (rating.highComplexityPercentage() + rating.veryHighComplexityPercentage() > 0.5) {
            return PackageComplexity.HIGHLY_COMPLEX;
        }

        return PackageComplexity.BALANCED;
    }
}
