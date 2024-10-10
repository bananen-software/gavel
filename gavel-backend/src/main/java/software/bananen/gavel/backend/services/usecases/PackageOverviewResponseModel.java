package software.bananen.gavel.backend.services.usecases;

import software.bananen.gavel.backend.domain.Size;
import software.bananen.gavel.backend.entity.PackageComplexity;

public record PackageOverviewResponseModel(
        String packageName,
        int complexity,
        int totalLinesOfCode,
        int totalLinesOfComments,
        double commentToCodeRatio,
        Size size,
        PackageComplexity packageComplexity,
        int numberOfTypes,
        ClassComplexityRatingResponseModel classComplexityRatings) {
}
