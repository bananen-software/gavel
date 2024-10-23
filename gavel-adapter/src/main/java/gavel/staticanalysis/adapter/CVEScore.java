package gavel.staticanalysis.adapter;

/**
 * An enumerable representation of the common vulnerability exposure score ratings.
 */
public enum CVEScore {
    NONE, LOW, MEDIUM, HIGH, CRITICAL;

    /**
     * Maps the given numeric score to the enumerable value.
     *
     * @param score The numeric score.
     * @return The enumerable value.
     */
    public static CVEScore map(final Double score) {
        if (score == 0) {
            return NONE;
        } else if (score >= 0.1 && score <= 3.9) {
            return LOW;
        } else if (score >= 4.0 && score <= 6.9) {
            return MEDIUM;
        } else if (score >= 7.0 && score <= 8.9) {
            return HIGH;
        } else {
            return CRITICAL;
        }
    }
}
