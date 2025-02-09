package software.bananen.gavel.infrastructure.graphql;

public record ClassReadModel(int id,
                             int packageId,
                             String name,
                             String programmingLanguage,
                             String lastModified,
                             int numberOfChanges,
                             int numberOfAuthors,
                             int complexity,
                             String complexityRating,
                             int totalLinesOfCode,
                             int totalLinesOfComments,
                             double commentToCodeRatio,
                             int numberOfResponsibilities,
                             String status,
                             int totalNumberOfFindings,
                             int numberOfHighPriorityFindings,
                             double defectDensity,
                             double highDefectDensity) {

}
