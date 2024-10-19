package software.bananen.gavel.backend.domain;

public enum Size {
    UNKNOWN, EMPTY, SMALL, MEDIUM, LARGE, VERY_LARGE;

    public static Size getClassSize(final Integer loc) {
        if (loc == 0) {
            return Size.EMPTY;
        } else if (loc <= 100) {
            return Size.SMALL;
        } else if (loc <= 500) {
            return Size.MEDIUM;
        } else if (loc <= 1000) {
            return Size.LARGE;
        } else {
            return Size.VERY_LARGE;
        }
    }

    public static Size getPackageSize(final Integer loc) {
        if (loc == 0) {
            return Size.EMPTY;
        } else if (loc <= 1_000) {
            return Size.SMALL;
        } else if (loc <= 10_000) {
            return Size.MEDIUM;
        } else if (loc <= 100_000) {
            return Size.LARGE;
        } else {
            return Size.VERY_LARGE;
        }
    }
}
