package software.bananen.gavel.backend.services.usecases;

import software.bananen.gavel.backend.domain.PackageComplexity;
import software.bananen.gavel.backend.domain.Size;

public record PackageOverviewResponseModel(
        String packageName,
        int complexity,
        int totalLinesOfCode,
        int totalLinesOfComments,
        double commentToCodeRatio,
        Size size,
        PackageComplexity packageComplexity,
        int packageComplexityOrdinal,
        int numberOfTypes,
        ClassComplexityRatingResponseModel classComplexityRatings) {
}
