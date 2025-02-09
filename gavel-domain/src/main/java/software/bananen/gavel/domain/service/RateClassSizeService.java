package software.bananen.gavel.domain.service;

import software.bananen.gavel.domain.model.Size;

/**
 * A service that can be used to rate the size of a class.
 */
public final class RateClassSizeService {

    private static final int EMPTY_SIZE = 0;
    private static final int SMALL_CLASS_MAX_SIZE = 100;
    private static final int MEDIUM_CLASS_MAX_SIZE = 500;
    private static final int LARGE_CLASS_MAX_SIZE = 1_000;

    /**
     * Rates the given lines of code of a class.
     *
     * @param loc The number of lines of code of a class.
     * @return The size.
     */
    public Size rate(final Integer loc) {
        if (loc == EMPTY_SIZE) {
            return Size.EMPTY;
        } else if (loc <= SMALL_CLASS_MAX_SIZE) {
            return Size.SMALL;
        } else if (loc <= MEDIUM_CLASS_MAX_SIZE) {
            return Size.MEDIUM;
        } else if (loc <= LARGE_CLASS_MAX_SIZE) {
            return Size.LARGE;
        } else {
            return Size.VERY_LARGE;
        }
    }
}
