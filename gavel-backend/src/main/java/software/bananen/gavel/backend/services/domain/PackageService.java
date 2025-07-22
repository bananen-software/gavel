package software.bananen.gavel.backend.services.domain;

import gavel.adapter.persistence.jpa.JpaPackageEntity;
import gavel.adapter.persistence.jpa.JpaProjectEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.domain.model.CommentToCodeRating;
import software.bananen.gavel.domain.model.PackageComplexityRating;
import software.bananen.gavel.domain.model.Size;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaPackageRepository;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Service
public class PackageService {

    private final JpaPackageRepository repository;

    public PackageService(@Autowired final JpaPackageRepository repository) {
        this.repository = requireNonNull(repository, "The repository may not be null");
    }

    public JpaPackageEntity findOrCreatePackage(final JpaProjectEntity project,
                                                final String packageName) {
        final Optional<JpaPackageEntity> matchingPackage =
                repository.findByProjectAndPackageName(project, packageName);

        return matchingPackage.orElseGet(
                () -> repository.save(mapToEntity(packageName, project)));
    }

    /**
     * Maps the given measurement and packages to a {@link JpaPackageEntity}
     *
     * @param packageName The package value
     * @param project     The project entity.
     * @return The mapping function.
     */
    private JpaPackageEntity mapToEntity(
            final String packageName,
            final JpaProjectEntity project) {
        final JpaPackageEntity pkg = new JpaPackageEntity();

        pkg.setPackageName(packageName);
        pkg.setLinesOfCode(0);
        pkg.setLinesOfComments(0);
        pkg.setCommentToCodeRatio(0.0);
        pkg.setCommentToCodeRating(CommentToCodeRating.NORMAL);
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

    public void save(final JpaPackageEntity packageEntity) {
        repository.save(packageEntity);
    }
}
