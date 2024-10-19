package software.bananen.gavel.backend.domain;

public enum PackageComplexity {
    EMPTY,
    MOSTLY_SIMPLE,
    BALANCED,
    COMPLEX,
    HIGHLY_COMPLEX;

    public static PackageComplexity determine(
            final Integer lowComplexityCount,
            final Integer mediumComplexityCount,
            final Integer highComplexityCount,
            final Integer veryHighComplexityCount) {
        final int totalCount =
                lowComplexityCount + mediumComplexityCount
                        + highComplexityCount + veryHighComplexityCount;

        if (totalCount == 0) {
            return PackageComplexity.EMPTY;
        }

        if ((lowComplexityCount + mediumComplexityCount) / (double) totalCount > 0.7) {
            return PackageComplexity.MOSTLY_SIMPLE;
        } else if ((highComplexityCount + veryHighComplexityCount) / (double) totalCount > 0.5) {
            return PackageComplexity.HIGHLY_COMPLEX;
        } else if ((highComplexityCount + veryHighComplexityCount) / (double) totalCount > 0.3) {
            return PackageComplexity.COMPLEX;
        }

        return PackageComplexity.BALANCED;
    }
}
