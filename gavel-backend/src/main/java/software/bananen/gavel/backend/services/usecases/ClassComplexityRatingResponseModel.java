package software.bananen.gavel.backend.services.usecases;

public record ClassComplexityRatingResponseModel(
        double lowComplexityPercentage,
        double mediumComplexityPercentage,
        double highComplexityPercentage,
        double veryHighComplexityPercentage) {
}
