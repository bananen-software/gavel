package software.bananen.gavel.backend.services.usecases;


public record PackageOverviewResponseModel(
        String packageName,
        int complexity,
        int totalLinesOfCode,
        int totalLinesOfComments,
        double commentToCodeRatio,
        String size,
        String packageComplexity,
        int packageComplexityOrdinal,
        int numberOfTypes,
        ClassComplexityRatingResponseModel classComplexityRatings,
        int numberOfFindings,
        int numberOfHighFindings,
        double defectDensity,
        double highDefectDensity) {
}
