package software.bananen.gavel.domain.service;

import software.bananen.gavel.domain.ports.service.CVERating;

/**
 * A service that can be used to rate CVE scores.
 */
public final class RateCVEScoreService {

    /**
     * Rates the given score.
     *
     * @param score The score that should be rated.
     * @return The rated score.
     */
    public CVERating rate(double score) {
        if (score == 0) {
            return CVERating.NONE;
        } else if (score >= 0.1 && score <= 3.9) {
            return CVERating.LOW;
        } else if (score >= 4.0 && score <= 6.9) {
            return CVERating.MEDIUM;
        } else if (score >= 7.0 && score <= 8.9) {
            return CVERating.HIGH;
        } else {
            return CVERating.CRITICAL;
        }
    }
}
