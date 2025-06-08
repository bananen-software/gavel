package software.bananen.gavel.infrastructure.graphql;

public record PackageReadModel(int id,
                               int projectId,
                               String name,
                               Integer complexity,
                               String complexityRating,
                               Double commentToCodeRatio,
                               double defectDensity,
                               double highDefectDensity,
                               Integer linesOfCode,
                               Integer linesOfComments,
                               Integer numberOfVeryHighComplexityTypes,
                               Integer numberOfHighComplexityTypes,
                               Integer numberOfMediumComplexityTypes,
                               Integer numberOfLowComplexityTypes,
                               Integer numberOfHighPriorityFindings,
                               Integer totalNumberOfFindings,
                               String size,
                               Integer numberOfTypes,
                               int complexityOrdinal) {

}
