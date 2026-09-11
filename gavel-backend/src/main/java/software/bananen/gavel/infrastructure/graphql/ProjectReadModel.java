package software.bananen.gavel.infrastructure.graphql;


public record ProjectReadModel(int id,
                               String name,
                               String analysisStatus,
                               String lastAnalyzed,
                               Integer numberOfTypes,
                               Integer totalLinesOfCode,
                               Integer totalLinesOfComments,
                               Integer numberOfFindings,
                               Integer numberOfHighPriorityFindings,
                               Integer numberOfPackages,
                               Double defectDensity,
                               Double highDefectDensity,
                               Double commentToCodeRatio) {
}
