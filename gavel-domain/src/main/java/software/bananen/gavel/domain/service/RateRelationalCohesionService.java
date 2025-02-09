package software.bananen.gavel.domain.service;

import software.bananen.gavel.domain.model.RelationalCohesionRating;

/**
 * A service that can be used to rate the relational cohesion of a package.
 */
public final class RateRelationalCohesionService {

    private static final double LOW_COHESION_THRESHOLD = 1.5;
    private static final double HIGH_COHESION_THRESHOLD = 4;

    /**
     * Rates the given relational cohesion.
     *
     * @param relationalCohesion The measured relational cohesion.
     * @return The rating.
     */
    public RelationalCohesionRating rate(final double relationalCohesion) {
        if (relationalCohesion < LOW_COHESION_THRESHOLD) {
            return RelationalCohesionRating.LOW;
        } else if (relationalCohesion > HIGH_COHESION_THRESHOLD) {
            return RelationalCohesionRating.HIGH;
        } else {
            return RelationalCohesionRating.GOOD;
        }
    }
}
