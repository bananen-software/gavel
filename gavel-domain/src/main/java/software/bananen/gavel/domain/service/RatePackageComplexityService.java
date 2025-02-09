package software.bananen.gavel.domain.service;

import software.bananen.gavel.domain.model.PackageComplexityRating;

/**
 * A service to rate the complexity of a package.
 */
public final class RatePackageComplexityService {

    private static final double MOSTLY_SIMPLE_PACKAGE_THRESHOLD = 0.7;
    private static final double HIGHLY_COMPLEX_PACKAGE_THRESHOLD = 0.5;
    private static final double COMPLEX_PACKAGE_THRESHOLD = 0.3;

    /**
     * Rates the package based on the given class counts.
     *
     * @param lowComplexityCount      The number of low complexity classes.
     * @param mediumComplexityCount   The number of medium complexity classes.
     * @param highComplexityCount     The number of high complexity classes.
     * @param veryHighComplexityCount The number of very high complexity classes.
     * @return The rated package complexity.
     */
    public PackageComplexityRating rate(
            final Integer lowComplexityCount,
            final Integer mediumComplexityCount,
            final Integer highComplexityCount,
            final Integer veryHighComplexityCount) {
        final double totalCount =
                lowComplexityCount + mediumComplexityCount
                        + highComplexityCount + veryHighComplexityCount;

        if (totalCount == 0) {
            return PackageComplexityRating.EMPTY;
        }

        if ((lowComplexityCount + mediumComplexityCount) / totalCount > MOSTLY_SIMPLE_PACKAGE_THRESHOLD) {
            return PackageComplexityRating.MOSTLY_SIMPLE;
        } else if ((highComplexityCount + veryHighComplexityCount) / totalCount > HIGHLY_COMPLEX_PACKAGE_THRESHOLD) {
            return PackageComplexityRating.HIGHLY_COMPLEX;
        } else if ((highComplexityCount + veryHighComplexityCount) / totalCount > COMPLEX_PACKAGE_THRESHOLD) {
            return PackageComplexityRating.COMPLEX;
        }

        return PackageComplexityRating.BALANCED;
    }
}
