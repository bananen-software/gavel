package software.bananen.gavel.infrastructure.graphql;

public record ProjectReadModel(int id,
                               String name,
                               String analysisStatus,
                               String lastAnalyzed) {
}
