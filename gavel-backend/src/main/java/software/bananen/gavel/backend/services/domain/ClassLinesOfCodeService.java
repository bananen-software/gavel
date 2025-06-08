package software.bananen.gavel.backend.services.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.domain.service.RateClassSizeService;
import software.bananen.gavel.infrastructure.persistence.jpa.ClassContributionEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.ClassEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.ClassLinesOfCodeEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.ClassLinesOfCodeRepository;

import java.util.Optional;

@Service
public class ClassLinesOfCodeService {

    private final ClassLinesOfCodeRepository repository;

    public ClassLinesOfCodeService(@Autowired final ClassLinesOfCodeRepository repository) {
        this.repository = repository;
    }

    public void createOrUpdate(final ClassContributionEntity contribution,
                               final Optional<ClassContributionEntity> latestContribution,
                               final int totalLines,
                               final int commentLines,
                               final double commentToCodeRatio) {

        final Integer latestLinesOfCode =
                latestContribution.map(ClassContributionEntity::getClassLinesOfCodes)
                        .flatMap(cloc -> cloc.stream().findFirst())
                        .map(ClassLinesOfCodeEntity::getTotalLinesOfCode)
                        .orElse(0);

        final Integer addedLinesOfCode =
                totalLines - latestLinesOfCode;

        final Integer latestLinesOfComments =
                latestContribution.map(ClassContributionEntity::getClassLinesOfCodes)
                        .flatMap(cloc -> cloc.stream().findFirst())
                        .map(ClassLinesOfCodeEntity::getTotalLinesOfComment)
                        .orElse(0);

        final Integer addedLinesOfComments =
                commentLines - latestLinesOfComments;

        final var size = new RateClassSizeService().rate(totalLines);

        final ClassLinesOfCodeEntity measuredLinesOfCode =
                repository.findByContribution(contribution).orElse(new ClassLinesOfCodeEntity());

        measuredLinesOfCode.setTotalLinesOfCode(totalLines);
        measuredLinesOfCode.setCommentToCodeRatio(commentToCodeRatio);
        measuredLinesOfCode.setTotalLinesOfComment(commentLines);
        measuredLinesOfCode.setSize(size);
        measuredLinesOfCode.setAddedLinesOfCode(addedLinesOfCode);
        measuredLinesOfCode.setAddedLinesOfComment(addedLinesOfComments);

        measuredLinesOfCode.setContribution(contribution);

        contribution.getClassLinesOfCodes().add(measuredLinesOfCode);

        final ClassEntity classEntity = contribution.getClassField();

        classEntity.setTotalLinesOfComments(commentLines);
        classEntity.setTotalLinesOfCode(totalLines);
        classEntity.setCommentToCodeRatio(commentToCodeRatio);
        classEntity.setSize(size);

        repository.save(measuredLinesOfCode);
    }
}
