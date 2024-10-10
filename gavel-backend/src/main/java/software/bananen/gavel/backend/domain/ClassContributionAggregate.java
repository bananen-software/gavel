package software.bananen.gavel.backend.domain;

import software.bananen.gavel.backend.entity.ClassComplexityEntity;
import software.bananen.gavel.backend.entity.ClassContributionEntity;
import software.bananen.gavel.backend.entity.ClassLinesOfCodeEntity;

import java.util.HashSet;
import java.util.List;

public class ClassContributionAggregate implements Aggregate<ClassContributionEntity> {

    private final ClassContributionEntity classContributionEntity;

    public ClassContributionAggregate(final ClassContributionEntity classContributionEntity) {
        this.classContributionEntity = classContributionEntity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassContributionEntity getAggregateRoot() {
        return classContributionEntity;
    }

    public void recordComplexityInstance(final Integer complexity) {
        final ClassComplexityEntity measuredComplexity = new ClassComplexityEntity();

        measuredComplexity.setComplexity(complexity);
        measuredComplexity.setContribution(classContributionEntity);
        measuredComplexity.setComplexityRating(measureComplexityRating(complexity));

        classContributionEntity.setClassComplexities(new HashSet<>(List.of(measuredComplexity)));
    }

    private static ComplexityRating measureComplexityRating(Integer complexity) {
        if (complexity <= 200) {
            return ComplexityRating.LOW;
        } else if (complexity <= 500) {
            return ComplexityRating.MEDIUM;
        } else if (complexity <= 1000) {
            return ComplexityRating.HIGH;
        } else {
            return ComplexityRating.VERY_HIGH;
        }
    }

    public void recordLinesOfCode(final int totalLines,
                                  final int commentLines,
                                  final double commentToCodeRatio) {

        final ClassLinesOfCodeEntity measuredLinesOfCode = new ClassLinesOfCodeEntity();

        measuredLinesOfCode.setTotalLinesOfCode(totalLines);
        measuredLinesOfCode.setCommentToCodeRatio(commentToCodeRatio);
        measuredLinesOfCode.setTotalLinesOfComment(commentLines);
        measuredLinesOfCode.setSize(measureSize(totalLines));

        measuredLinesOfCode.setContribution(classContributionEntity);

        classContributionEntity.setClassLinesOfCodes(
                new HashSet<>(List.of(measuredLinesOfCode))
        );
    }

    private Size measureSize(int totalLines) {
        if (totalLines <= 100) {
            return Size.SMALL;
        } else if (totalLines <= 500) {
            return Size.MEDIUM;
        } else if (totalLines <= 1000) {
            return Size.LARGE;
        } else {
            return Size.VERY_LARGE;
        }
    }
}
