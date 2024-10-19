package software.bananen.gavel.backend.domain;

public enum ComplexityRating {
    EMPTY, LOW, MEDIUM, HIGH, UNKNOWN, VERY_HIGH;

    public static ComplexityRating getClassComplexityRating(final Integer complexity) {
        if (complexity == 0) {
            return ComplexityRating.EMPTY;
        } else if (complexity <= 200) {
            return ComplexityRating.LOW;
        } else if (complexity <= 500) {
            return ComplexityRating.MEDIUM;
        } else if (complexity <= 1000) {
            return ComplexityRating.HIGH;
        } else {
            return ComplexityRating.VERY_HIGH;
        }
    }
}
