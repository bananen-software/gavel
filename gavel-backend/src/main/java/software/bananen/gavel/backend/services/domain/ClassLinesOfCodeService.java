package software.bananen.gavel.backend.services.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.domain.service.RateClassSizeService;
import software.bananen.gavel.domain.service.RateCommentToCodeRatioService;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaClassContributionEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaClassEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaClassLinesOfCodeEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaClassLinesOfCodeRepository;

import java.util.Optional;

@Service
public class ClassLinesOfCodeService {

    private final JpaClassLinesOfCodeRepository repository;
    private static final RateCommentToCodeRatioService COMMENT_TO_CODE_RATING_SERVICE = new RateCommentToCodeRatioService();

    public ClassLinesOfCodeService(@Autowired final JpaClassLinesOfCodeRepository repository) {
        this.repository = repository;
    }

    public void createOrUpdate(final JpaClassContributionEntity contribution,
                               final Optional<JpaClassContributionEntity> latestContribution,
                               final int totalLines,
                               final int commentLines,
                               final double commentToCodeRatio) {

        final Integer latestLinesOfCode =
                latestContribution.map(JpaClassContributionEntity::getClassLinesOfCodes)
                        .flatMap(cloc -> cloc.stream().findFirst())
                        .map(JpaClassLinesOfCodeEntity::getTotalLinesOfCode)
                        .orElse(0);

        final Integer addedLinesOfCode =
                totalLines - latestLinesOfCode;

        final Integer latestLinesOfComments =
                latestContribution.map(JpaClassContributionEntity::getClassLinesOfCodes)
                        .flatMap(cloc -> cloc.stream().findFirst())
                        .map(JpaClassLinesOfCodeEntity::getTotalLinesOfComment)
                        .orElse(0);

        final Integer addedLinesOfComments =
                commentLines - latestLinesOfComments;

        final var size = new RateClassSizeService().rate(totalLines);

        final JpaClassLinesOfCodeEntity measuredLinesOfCode =
                repository.findByContribution(contribution).orElse(new JpaClassLinesOfCodeEntity());

        measuredLinesOfCode.setTotalLinesOfCode(totalLines);
        measuredLinesOfCode.setCommentToCodeRatio(commentToCodeRatio);
        measuredLinesOfCode.setTotalLinesOfComment(commentLines);
        measuredLinesOfCode.setSize(size);
        measuredLinesOfCode.setAddedLinesOfCode(addedLinesOfCode);
        measuredLinesOfCode.setAddedLinesOfComment(addedLinesOfComments);

        measuredLinesOfCode.setContribution(contribution);

        contribution.getClassLinesOfCodes().add(measuredLinesOfCode);

        final JpaClassEntity classEntity = contribution.getClassField();

        classEntity.setTotalLinesOfComments(commentLines);
        classEntity.setTotalLinesOfCode(totalLines);
        classEntity.setCommentToCodeRatio(commentToCodeRatio);
        classEntity.setCommentToCodeRating(COMMENT_TO_CODE_RATING_SERVICE.rate(commentToCodeRatio));
        classEntity.setSize(size);

        repository.save(measuredLinesOfCode);
    }
}
