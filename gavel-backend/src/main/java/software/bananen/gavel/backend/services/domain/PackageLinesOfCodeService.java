package software.bananen.gavel.backend.services.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.domain.model.Size;
import software.bananen.gavel.domain.service.MeasureCommentToCodeRatioService;
import software.bananen.gavel.domain.service.RatePackageSizeService;
import software.bananen.gavel.infrastructure.persistence.jpa.*;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

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
        final double packageCodeToCommentRatio =
                new MeasureCommentToCodeRatioService().measure(packageLines, packageCommentLines);

        final PackageLinesOfCodeEntity packageLinesOfCodeEntity =
                packageEntity.getPackageLinesOfCodeEntities()
                        .stream()
                        .findFirst()
                        .orElse(new PackageLinesOfCodeEntity());

        final Size packageSize = new RatePackageSizeService().rate(packageLines);

        packageLinesOfCodeEntity.setPackageField(packageEntity);
        packageLinesOfCodeEntity.setTotalLinesOfCode(packageLines);
        packageLinesOfCodeEntity.setTotalLinesOfComment(packageCommentLines);
        packageLinesOfCodeEntity.setCommentToCodeRatio(packageCodeToCommentRatio);
        packageLinesOfCodeEntity.setPackageSize(packageSize);

        packageEntity.setPackageLinesOfCodeEntities(
                new LinkedHashSet<>(List.of(packageLinesOfCodeEntity)));

        packageEntity.setLinesOfCode(packageLines);
        packageEntity.setLinesOfComments(packageCommentLines);
        packageEntity.setCommentToCodeRatio(packageCodeToCommentRatio);
        packageEntity.setSize(packageSize);

        repository.save(packageLinesOfCodeEntity);
    }

    private int measurePackageLines(final PackageEntity packageEntity) {
        int packageLines = 0;

        for (final ClassEntity classEntity : packageEntity.getActiveClasses()) {
            packageLines += classEntity.getTotalLinesOfCode();
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
