package software.bananen.gavel.backend.services.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.backend.repository.ProjectRepository;

import java.util.Objects;
import java.util.Optional;

@Service
public class FetchPackageUseCase {

    private final ProjectRepository projectRepository;

    public FetchPackageUseCase(@Autowired final ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Optional<PackageOverviewResponseModel> load(final String packageName) {
        final Optional<ProjectEntity> project =
                projectRepository.findAll()
                        .stream()
                        .findFirst();

        if (project.isEmpty()) {
            return Optional.empty();
        }

        final Optional<PackageEntity> matchingPackage = project.get()
                .getPackages()
                .stream().filter(pkg -> Objects.equals(pkg.getPackageName(), packageName))
                .findFirst();

        return matchingPackage.map(packageEntity ->
                new PackageOverviewResponseModel(
                        packageEntity.getPackageName(),
                        packageEntity.getComplexity(),
                        packageEntity.getLinesOfCode(),
                        packageEntity.getLinesOfComments(),
                        packageEntity.getCommentToCodeRatio(),
                        packageEntity.getSize(),
                        packageEntity.getComplexityRating(),
                        packageEntity.getComplexityRating().ordinal(),
                        packageEntity.getNumberOfTypes(),
                        new ClassComplexityRatingResponseModel(
                                packageEntity.getNumberOfLowComplexityTypes() / (double) packageEntity.getNumberOfTypes(),
                                packageEntity.getNumberOfMediumComplexityTypes() / (double) packageEntity.getNumberOfTypes(),
                                packageEntity.getNumberOfHighComplexityTypes() / (double) packageEntity.getNumberOfTypes(),
                                packageEntity.getNumberOfVeryHighComplexityTypes() / (double) packageEntity.getNumberOfTypes()
                        )
                ));
    }
}
