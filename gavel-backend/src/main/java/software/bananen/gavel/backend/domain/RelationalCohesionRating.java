package software.bananen.gavel.backend.domain;

public enum RelationalCohesionRating {
    LOW,

    GOOD,

    HIGH;

    public static RelationalCohesionRating getCohesionRating(final double relationalCohesion) {
        if (relationalCohesion < 1.5) {
            return RelationalCohesionRating.LOW;
        } else if (relationalCohesion > 4) {
            return RelationalCohesionRating.HIGH;
        } else {
            return RelationalCohesionRating.GOOD;
        }
    }
}
