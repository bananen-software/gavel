package software.bananen.gavel.backend.domain;

import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.ProjectEntity;

import java.util.*;

public class ProjectAggregate implements Aggregate<ProjectEntity> {

    private final ProjectEntity projectEntity;

    public ProjectAggregate(final ProjectEntity projectEntity) {
        this.projectEntity = projectEntity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectEntity getAggregateRoot() {
        return projectEntity;
    }

    /**
     * Retrieves or creates the package with the given name.
     *
     * @param packageName The package name.
     * @return The package.
     */
    public PackageAggregate findOrCreatePackage(final String packageName) {
        return findPackage(packageName)
                .orElseGet(() -> new PackageAggregate(mapToEntity(packageName)));
    }

    public Optional<PackageAggregate> findPackage(final String packageName) {
        return projectEntity.getPackages().stream()
                .filter(pkg -> Objects.equals(pkg.getPackageName(), packageName))
                .findFirst()
                .map(PackageAggregate::new);
    }

    /**
     * Maps the given measurement and packages to a {@link PackageEntity}
     *
     * @param packageName The package name
     * @return The mapping function.
     */
    private PackageEntity mapToEntity(
            final String packageName) {
        final PackageEntity pkg = new PackageEntity();

        pkg.setPackageName(packageName);
        pkg.setProject(projectEntity);
        pkg.setClasses(new HashSet<>());
        pkg.setComponentDependencyMetrics(new HashSet<>());
        pkg.setCumulativeComponentDependencies(new HashSet<>());
        pkg.setVisibilityMetrics(new HashSet<>());
        pkg.setRelationalCohesionMetrics(new HashSet<>());

        final Set<PackageEntity> packages = new HashSet<>(projectEntity.getPackages());

        packages.add(pkg);
        projectEntity.setPackages(packages);

        return pkg;
    }

    public Collection<PackageAggregate> getPackages() {
        return projectEntity.getPackages()
                .stream()
                .map(PackageAggregate::new)
                .toList();
    }
}
