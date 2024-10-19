package software.bananen.gavel.backend.services.usecases;

import software.bananen.gavel.backend.domain.ComplexityRating;

import java.time.LocalDateTime;

public record ClassOverviewResponseModel(String packageName,
                                         String className,
                                         LocalDateTime lastModified,
                                         int numberOfChanges,
                                         int numberOfAuthors,
                                         Integer complexity,
                                         ComplexityRating complexityRating,
                                         Integer totalLinesOfCode,
                                         Integer totalLinesOfComments,
                                         Double commentToCodeRatio,
                                         Integer classCohesion) {
}
