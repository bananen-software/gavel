package software.bananen.gavel.domain.service;

import software.bananen.gavel.domain.model.Size;

/**
 * A service that can be used to rate the size of a package.
 */
public final class RatePackageSizeService {

    private static final int EMPTY_SIZE = 0;
    private static final int SMALL_PACKAGE_MAX_SIZE = 1_000;
    private static final int MEDIUM_PACKAGE_MAX_SIZE = 10_000;
    private static final int LARGE_PACKAGE_MAX_SIZE = 100_000;

    /**
     * Rates the given lines of code of a package.
     *
     * @param loc The number of lines of code of a package.
     * @return The size.
     */
    public Size rate(final Integer loc) {
        if (loc == EMPTY_SIZE) {
            return Size.EMPTY;
        } else if (loc <= SMALL_PACKAGE_MAX_SIZE) {
            return Size.SMALL;
        } else if (loc <= MEDIUM_PACKAGE_MAX_SIZE) {
            return Size.MEDIUM;
        } else if (loc <= LARGE_PACKAGE_MAX_SIZE) {
            return Size.LARGE;
        } else {
            return Size.VERY_LARGE;
        }
    }
}
