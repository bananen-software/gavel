package software.bananen.gavel.backend.services.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.backend.domain.ComplexityRating;
import software.bananen.gavel.backend.entity.*;
import software.bananen.gavel.backend.repository.ProjectRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ListClassesInPackageUseCase {

    private final ProjectRepository projectRepository;

    public ListClassesInPackageUseCase(
            @Autowired final ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Optional<Collection<ClassOverviewResponseModel>> list(final String packageName) {
        final Optional<ProjectEntity> project =
                projectRepository.findAll().stream().findFirst();

        if (project.isEmpty()) {
            return Optional.empty();
        }

        final Optional<PackageEntity> matchingPackage = project.get()
                .getPackages()
                .stream()
                .filter(pkg -> Objects.equals(pkg.getPackageName(), packageName))
                .findFirst();

        if (matchingPackage.isEmpty()) {
            return Optional.empty();
        }

        final Collection<ClassOverviewResponseModel> result = new ArrayList<>();

        for (final ClassEntity classEntity : matchingPackage.get().getClasses()) {
            result.add(new ClassOverviewResponseModel(
                    matchingPackage.get().getPackageName(),
                    classEntity.getName(),
                    classEntity.getLastModified(),
                    classEntity.getClassContributions().size(),
                    classEntity.getClassContributions()
                            .stream()
                            .map(ClassContributionEntity::getAuthor)
                            .collect(Collectors.toSet())
                            .size(),
                    classEntity.getClassContributions()
                            .stream()
                            .max(Comparator.comparing(ClassContributionEntity::getTimestamp))
                            .map(ClassContributionEntity::getClassComplexities)
                            .flatMap(c -> c.stream().findFirst())
                            .map(ClassComplexityEntity::getComplexity)
                            .orElse(0),
                    classEntity.getClassContributions()
                            .stream()
                            .max(Comparator.comparing(ClassContributionEntity::getTimestamp))
                            .map(ClassContributionEntity::getClassComplexities)
                            .flatMap(c -> c.stream().findFirst())
                            .map(ClassComplexityEntity::getComplexityRating)
                            .orElse(ComplexityRating.UNKNOWN)
            ));
        }

        return Optional.of(result);
    }
}
