package software.bananen.gavel.backend.services.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.bananen.gavel.infrastructure.persistence.jpa.ClassEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaProjectRepository;
import software.bananen.gavel.infrastructure.persistence.jpa.PackageEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.ProjectEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
public class ListClassesInPackageUseCase {

    private final JpaProjectRepository projectRepository;

    public ListClassesInPackageUseCase(
            @Autowired final JpaProjectRepository projectRepository) {
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

        for (final ClassEntity classEntity : matchingPackage.get().getActiveClasses()) {
            result.add(new ClassOverviewResponseModel(
                    matchingPackage.get().getPackageName(),
                    classEntity.getName(),
                    classEntity.getLastModified(),
                    classEntity.getNumberOfChanges(),
                    classEntity.getNumberOfAuthors(),
                    classEntity.getComplexity(),
                    classEntity.getComplexityRating().name(),
                    classEntity.getTotalLinesOfCode(),
                    classEntity.getTotalLinesOfComments(),
                    classEntity.getCommentToCodeRatio(),
                    classEntity.getNumberOfResponsibilities(),
                    classEntity.getTotalNumberOfFindings(),
                    classEntity.getNumberOfHighPriorityFindings(),
                    classEntity.getDefectDensity(),
                    classEntity.getHighDefectDensity()
            ));
        }

        return Optional.of(result);
    }
}
