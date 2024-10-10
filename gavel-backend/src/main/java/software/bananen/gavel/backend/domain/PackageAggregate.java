package software.bananen.gavel.backend.domain;

import software.bananen.gavel.backend.entity.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class PackageAggregate implements Aggregate<PackageEntity> {

    private final PackageEntity packageEntity;

    public PackageAggregate(final PackageEntity packageEntity) {
        this.packageEntity = packageEntity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PackageEntity getAggregateRoot() {
        return packageEntity;
    }

    public ClassAggregate findOrCreateClass(final String className) {
        return findClass(className)
                .orElseGet(() -> {
                    final ClassEntity classEntity = new ClassEntity();

                    classEntity.setName(className);
                    classEntity.setPackageField(packageEntity);
                    classEntity.setLastModified(LocalDateTime.now());

                    final Set<ClassEntity> classes = packageEntity.getClasses();
                    classes.add(classEntity);
                    packageEntity.setClasses(classes);

                    return new ClassAggregate(classEntity);
                });
    }

    public Optional<ClassAggregate> findClass(final String className) {
        return packageEntity.getClasses()
                .stream().filter(classEntity -> classEntity.getName().equals(className))
                .findFirst()
                .map(ClassAggregate::new);
    }

    public void recordPackageComplexity() {
        final Set<PackageComplexityEntity> values = new LinkedHashSet<>();

        final PackageComplexityEntity packageComplexityEntity =
                packageEntity.getPackageComplexityEntities()
                        .stream()
                        .findFirst()
                        .orElse(new PackageComplexityEntity());

        packageComplexityEntity.setComplexity(measurePackageComplexity());
        packageComplexityEntity.setPackageField(packageEntity);

        values.add(packageComplexityEntity);
        packageEntity.setPackageComplexityEntities(values);
    }

    public int measurePackageComplexity() {
        int packageComplexity = 0;

        for (final ClassEntity classEntity : packageEntity.getClasses()) {
            packageComplexity += classEntity.getClassContributions()
                    .stream()
                    .max(Comparator.comparing(ClassContributionEntity::getTimestamp))
                    .flatMap(c -> c.getClassComplexities().stream().findFirst())
                    .map(ClassComplexityEntity::getComplexity)
                    .orElse(0);
        }

        return packageComplexity;
    }

    public int measurePackageLines() {
        int packageLines = 0;
        
        for (final ClassEntity classEntity : packageEntity.getClasses()) {
            packageLines += classEntity.getClassContributions()
                    .stream()
                    .max(Comparator.comparing(ClassContributionEntity::getTimestamp))
                    .flatMap(c -> c.getClassLinesOfCodes().stream().findFirst())
                    .map(ClassLinesOfCodeEntity::getTotalLinesOfCode)
                    .orElse(0);
        }

        return packageLines;
    }

    public int measurePackageCommentLines() {
        int packageCommentLines = 0;

        for (final ClassEntity classEntity : packageEntity.getClasses()) {
            packageCommentLines += classEntity.getClassContributions()
                    .stream()
                    .max(Comparator.comparing(ClassContributionEntity::getTimestamp))
                    .flatMap(c -> c.getClassLinesOfCodes().stream().findFirst())
                    .map(ClassLinesOfCodeEntity::getTotalLinesOfComment)
                    .orElse(0);
        }

        return packageCommentLines;
    }

    public void recordPackageLines() {
        int packageLines = measurePackageLines();
        int packageCommentLines = measurePackageCommentLines();
        double packageCodeToCommentRatio =
                packageLines > 0 ?
                        (packageCommentLines / (double) packageLines) :
                        0;

        final Set<PackageLinesOfCodeEntity> values = new LinkedHashSet<>();
        final PackageLinesOfCodeEntity packageLinesOfCodeEntity =
                packageEntity.getPackageLinesOfCodeEntities()
                        .stream()
                        .findFirst().orElse(new PackageLinesOfCodeEntity());

        packageLinesOfCodeEntity.setTotalLinesOfCode(packageLines);
        packageLinesOfCodeEntity.setTotalLinesOfComment(packageCommentLines);
        packageLinesOfCodeEntity.setCommentToCodeRatio(packageCodeToCommentRatio);
        packageLinesOfCodeEntity.setPackageSize(measurePackageSize(packageLines));

        packageLinesOfCodeEntity.setPackageField(packageEntity);
        values.add(packageLinesOfCodeEntity);
        packageEntity.setPackageLinesOfCodeEntities(values);
    }

    private Size measurePackageSize(final int packageLines) {
        if (packageLines <= 1_000) {
            return Size.SMALL;
        } else if (packageLines <= 10_000) {
            return Size.MEDIUM;
        } else if (packageLines <= 100_000) {
            return Size.LARGE;
        } else {
            return Size.VERY_LARGE;
        }
    }
}
