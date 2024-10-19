package software.bananen.gavel.backend.services.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.bananen.gavel.backend.domain.ClassStatus;
import software.bananen.gavel.backend.entity.ClassEntity;
import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.backend.repository.ProjectRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
public class ListClassesInPackageUseCase {

    private final ProjectRepository projectRepository;

    public ListClassesInPackageUseCase(
            @Autowired final ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
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

        for (final ClassEntity classEntity :
                matchingPackage.get().getClasses().stream().filter(e -> ClassStatus.ACTIVE.equals(e.getStatus())).toList()) {
            result.add(new ClassOverviewResponseModel(
                    matchingPackage.get().getPackageName(),
                    classEntity.getName(),
                    classEntity.getLastModified(),
                    classEntity.getNumberOfChanges(),
                    classEntity.getNumberOfAuthors(),
                    classEntity.getComplexity(),
                    classEntity.getComplexityRating(),
                    classEntity.getTotalLinesOfCode(),
                    classEntity.getTotalLinesOfComments(),
                    classEntity.getCommentToCodeRatio(),
                    classEntity.getNumberOfResponsibilities()
            ));
        }

        return Optional.of(result);
    }
}
