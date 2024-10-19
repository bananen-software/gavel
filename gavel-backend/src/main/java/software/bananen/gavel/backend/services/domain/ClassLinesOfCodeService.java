package software.bananen.gavel.backend.services.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.backend.domain.Size;
import software.bananen.gavel.backend.entity.ClassContributionEntity;
import software.bananen.gavel.backend.entity.ClassEntity;
import software.bananen.gavel.backend.entity.ClassLinesOfCodeEntity;
import software.bananen.gavel.backend.repository.ClassLinesOfCodeRepository;

import java.util.HashSet;
import java.util.List;
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

        final ClassLinesOfCodeEntity measuredLinesOfCode =
                repository.findByContribution(contribution).orElse(new ClassLinesOfCodeEntity());

        measuredLinesOfCode.setTotalLinesOfCode(totalLines);
        measuredLinesOfCode.setCommentToCodeRatio(commentToCodeRatio);
        measuredLinesOfCode.setTotalLinesOfComment(commentLines);
        measuredLinesOfCode.setSize(Size.getClassSize(totalLines));
        measuredLinesOfCode.setAddedLinesOfCode(addedLinesOfCode);
        measuredLinesOfCode.setAddedLinesOfComment(addedLinesOfComments);

        measuredLinesOfCode.setContribution(contribution);

        contribution.setClassLinesOfCodes(new HashSet<>(List.of(measuredLinesOfCode)));

        final ClassEntity classEntity = contribution.getClassField();

        classEntity.setTotalLinesOfComments(commentLines);
        classEntity.setTotalLinesOfCode(totalLines);
        classEntity.setCommentToCodeRatio(commentToCodeRatio);
        classEntity.setSize(Size.getClassSize(totalLines));

        repository.save(measuredLinesOfCode);
    }
}
