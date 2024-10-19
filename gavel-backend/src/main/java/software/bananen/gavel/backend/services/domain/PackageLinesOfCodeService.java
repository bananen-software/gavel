package software.bananen.gavel.backend.services.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.backend.domain.ClassStatus;
import software.bananen.gavel.backend.domain.Size;
import software.bananen.gavel.backend.entity.*;
import software.bananen.gavel.backend.repository.PackageLinesOfCodeRepository;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class PackageLinesOfCodeService {

    private final PackageLinesOfCodeRepository repository;

    public PackageLinesOfCodeService(
            @Autowired final PackageLinesOfCodeRepository repository) {
        this.repository = repository;
    }

    public void createOrUpdate(final PackageEntity packageEntity) {
        int packageLines = measurePackageLines(packageEntity);
        int packageCommentLines = measurePackageCommentLines(packageEntity);
        double packageCodeToCommentRatio =
                packageLines > 0 ?
                        (packageCommentLines / (double) packageLines) :
                        0;

        final Set<PackageLinesOfCodeEntity> values = new LinkedHashSet<>();
        final PackageLinesOfCodeEntity packageLinesOfCodeEntity =
                packageEntity.getPackageLinesOfCodeEntities()
                        .stream()
                        .findFirst()
                        .orElse(new PackageLinesOfCodeEntity());

        packageLinesOfCodeEntity.setTotalLinesOfCode(packageLines);
        packageLinesOfCodeEntity.setTotalLinesOfComment(packageCommentLines);
        packageLinesOfCodeEntity.setCommentToCodeRatio(packageCodeToCommentRatio);
        packageLinesOfCodeEntity.setPackageSize(Size.getPackageSize(packageLines));

        packageLinesOfCodeEntity.setPackageField(packageEntity);
        values.add(packageLinesOfCodeEntity);
        packageEntity.setPackageLinesOfCodeEntities(values);

        packageEntity.setLinesOfCode(packageLines);
        packageEntity.setLinesOfComments(packageCommentLines);
        packageEntity.setCommentToCodeRatio(packageCodeToCommentRatio);
        packageEntity.setSize(Size.getPackageSize(packageLines));

        repository.save(packageLinesOfCodeEntity);
    }

    private int measurePackageLines(final PackageEntity packageEntity) {
        int packageLines = 0;

        for (final ClassEntity classEntity :
                packageEntity.getClasses()
                        .stream().filter(e -> ClassStatus.ACTIVE.equals(e.getStatus()))
                        .toList()) {
            packageLines += classEntity.getClassContributions()
                    .stream()
                    .max(Comparator.comparing(ClassContributionEntity::getTimestamp))
                    .flatMap(c -> c.getClassLinesOfCodes().stream().findFirst())
                    .map(ClassLinesOfCodeEntity::getTotalLinesOfCode)
                    .orElse(0);
        }

        return packageLines;
    }

    public int measurePackageCommentLines(final PackageEntity packageEntity) {
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
}
