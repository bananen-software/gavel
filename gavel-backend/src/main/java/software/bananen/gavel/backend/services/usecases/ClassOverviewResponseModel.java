package software.bananen.gavel.backend.services.usecases;


import java.time.LocalDateTime;

public record ClassOverviewResponseModel(String packageName,
                                         String className,
                                         LocalDateTime lastModified,
                                         int numberOfChanges,
                                         int numberOfAuthors,
                                         Integer complexity,
                                         String complexityRating,
                                         Integer totalLinesOfCode,
                                         Integer totalLinesOfComments,
                                         Double commentToCodeRatio,
                                         Integer classCohesion,
                                         double numberOfFindings,
                                         double numberOfHighFindings,
                                         double defectDensity,
                                         double highDefectDensity) {
}
