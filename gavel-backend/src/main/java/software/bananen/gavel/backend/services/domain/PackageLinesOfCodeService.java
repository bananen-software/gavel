package software.bananen.gavel.backend.services.domain;

import gavel.adapter.persistence.jpa.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.domain.model.Size;
import software.bananen.gavel.domain.service.MeasureCommentToCodeRatioService;
import software.bananen.gavel.domain.service.RateCommentToCodeRatioService;
import software.bananen.gavel.domain.service.RatePackageSizeService;
import gavel.adapter.persistence.jpa.JpaPackageLinesOfCodeRepository;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class PackageLinesOfCodeService {

    private final JpaPackageLinesOfCodeRepository repository;

    private static final RateCommentToCodeRatioService RATE_COMMENT_TO_CODE_RATIO_SERVICE = new RateCommentToCodeRatioService();

    public PackageLinesOfCodeService(
            @Autowired final JpaPackageLinesOfCodeRepository repository) {
        this.repository = repository;
    }

    public void createOrUpdate(final JpaPackageEntity packageEntity) {
        int packageLines = measurePackageLines(packageEntity);
        int packageCommentLines = measurePackageCommentLines(packageEntity);
        final double packageCodeToCommentRatio =
                new MeasureCommentToCodeRatioService().measure(packageLines, packageCommentLines);

        final JpaPackageLinesOfCodeEntity packageLinesOfCodeEntity =
                packageEntity.getPackageLinesOfCodeEntities()
                        .stream()
                        .findFirst()
                        .orElse(new JpaPackageLinesOfCodeEntity());

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
        packageEntity.setCommentToCodeRating(RATE_COMMENT_TO_CODE_RATIO_SERVICE.rate(packageCodeToCommentRatio));
        packageEntity.setSize(packageSize);

        repository.save(packageLinesOfCodeEntity);
    }

    private int measurePackageLines(final JpaPackageEntity packageEntity) {
        int packageLines = 0;

        for (final JpaClassEntity classEntity : packageEntity.getActiveClasses()) {
            packageLines += classEntity.getTotalLinesOfCode();
        }

        return packageLines;
    }

    public int measurePackageCommentLines(final JpaPackageEntity packageEntity) {
        int packageCommentLines = 0;

        for (final JpaClassEntity classEntity : packageEntity.getClasses()) {
            packageCommentLines += classEntity.getClassContributions()
                    .stream()
                    .max(Comparator.comparing(JpaClassContributionEntity::getTimestamp))
                    .flatMap(c -> c.getClassLinesOfCodes().stream().findFirst())
                    .map(JpaClassLinesOfCodeEntity::getTotalLinesOfComment)
                    .orElse(0);
        }

        return packageCommentLines;
    }
}
