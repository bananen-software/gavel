package software.bananen.gavel.backend.services.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.domain.model.PackageComplexityRating;
import software.bananen.gavel.domain.model.Size;
import software.bananen.gavel.infrastructure.persistence.jpa.PackageEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.PackageRepository;
import software.bananen.gavel.infrastructure.persistence.jpa.ProjectEntity;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Service
public class PackageService {

    private final PackageRepository repository;

    public PackageService(@Autowired final PackageRepository repository) {
        this.repository = requireNonNull(repository, "The repository may not be null");
    }

    public PackageEntity findOrCreatePackage(final ProjectEntity project,
                                             final String packageName) {
        final Optional<PackageEntity> matchingPackage =
                repository.findByProjectAndPackageName(project, packageName);

        return matchingPackage.orElseGet(
                () -> repository.save(mapToEntity(packageName, project)));
    }

    /**
     * Maps the given measurement and packages to a {@link PackageEntity}
     *
     * @param packageName The package value
     * @param project     The project entity.
     * @return The mapping function.
     */
    private PackageEntity mapToEntity(
            final String packageName,
            final ProjectEntity project) {
        final PackageEntity pkg = new PackageEntity();

        pkg.setPackageName(packageName);
        pkg.setLinesOfCode(0);
        pkg.setLinesOfComments(0);
        pkg.setCommentToCodeRatio(0.0);
        pkg.setNumberOfTypes(0);
        pkg.setComplexity(0);
        pkg.setNumberOfLowComplexityTypes(0);
        pkg.setNumberOfMediumComplexityTypes(0);
        pkg.setNumberOfHighComplexityTypes(0);
        pkg.setNumberOfVeryHighComplexityTypes(0);
        pkg.setSize(Size.UNKNOWN);
        pkg.setComplexityRating(PackageComplexityRating.EMPTY);
        pkg.setTotalNumberOfFindings(0);
        pkg.setNumberOfHighPriorityFindings(0);
        pkg.setDefectDensity(0.0);
        pkg.setHighDefectDensity(0.0);

        pkg.setProject(project);
        project.getPackages().add(pkg);

        return pkg;
    }

    public void save(final PackageEntity packageEntity) {
        repository.save(packageEntity);
    }
}
