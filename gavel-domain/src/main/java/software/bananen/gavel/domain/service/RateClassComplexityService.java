package software.bananen.gavel.domain.service;

import software.bananen.gavel.domain.model.ClassComplexityRating;

/**
 * A service that can be used to rate the complexity of a class.
 */
public final class RateClassComplexityService {

    private static final int EMPTY_CLASS_SCORE = 0;
    private static final int LOW_CLASS_MAX_SCORE = 200;
    private static final int MEDIUM_CLASS_MAX_SCORE = 500;
    private static final int HIGH_CLASS_MAX_SCORE = 1000;

    /**
     * Rates the class complexity.
     *
     * @param complexityScore The complexity score.
     * @return The rating.
     */
    public ClassComplexityRating rate(final Integer complexityScore) {
        if (complexityScore == EMPTY_CLASS_SCORE) {
            return ClassComplexityRating.EMPTY;
        } else if (complexityScore <= LOW_CLASS_MAX_SCORE) {
            return ClassComplexityRating.LOW;
        } else if (complexityScore <= MEDIUM_CLASS_MAX_SCORE) {
            return ClassComplexityRating.MEDIUM;
        } else if (complexityScore <= HIGH_CLASS_MAX_SCORE) {
            return ClassComplexityRating.HIGH;
        } else {
            return ClassComplexityRating.VERY_HIGH;
        }
    }
}
