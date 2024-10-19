package software.bananen.gavel.backend.services.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.backend.repository.ProjectRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class ListPackagesUseCase {

    private final ProjectRepository projectRepository;

    public ListPackagesUseCase(@Autowired final ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Optional<Collection<PackageOverviewResponseModel>> load() {
        Optional<ProjectEntity> project = projectRepository.findAll()
                .stream()
                .findFirst();

        if (project.isEmpty()) {
            return Optional.empty();
        }

        final Collection<PackageOverviewResponseModel> result = new ArrayList<>();

        for (final PackageEntity packageEntity : project.get().getPackages()) {
            result.add(new PackageOverviewResponseModel(
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

        return Optional.of(result);
    }
}
