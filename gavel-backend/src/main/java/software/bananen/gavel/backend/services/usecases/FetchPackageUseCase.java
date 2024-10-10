package software.bananen.gavel.backend.services.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.backend.domain.Size;
import software.bananen.gavel.backend.entity.PackageComplexityEntity;
import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.PackageLinesOfCodeEntity;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.backend.repository.ProjectRepository;

import java.util.Objects;
import java.util.Optional;

import static software.bananen.gavel.backend.services.usecases.ListPackagesUseCase.determinePackageComplexity;
import static software.bananen.gavel.backend.services.usecases.ListPackagesUseCase.mapToComplexityRatingResponseModel;

@Service
public class FetchPackageUseCase {

    private final ProjectRepository projectRepository;

    public FetchPackageUseCase(@Autowired final ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Optional<PackageOverviewResponseModel> load(final String packageName) {
        final Optional<ProjectEntity> project =
                projectRepository.findAll()
                        .stream()
                        .findFirst();

        if (project.isEmpty()) {
            return Optional.empty();
        }

        Optional<PackageEntity> matchingPackage = project.get()
                .getPackages()
                .stream().filter(pkg -> Objects.equals(pkg.getPackageName(), packageName))
                .findFirst();

        return matchingPackage.map(packageEntity -> {
            final ClassComplexityRatingResponseModel ratingResponseModel =
                    mapToComplexityRatingResponseModel(packageEntity);

            return new PackageOverviewResponseModel(
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
                    mapToComplexityRatingResponseModel(packageEntity)
            );
        });
    }
}
