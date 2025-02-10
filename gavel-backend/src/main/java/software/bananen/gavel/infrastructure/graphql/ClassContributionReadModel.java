package software.bananen.gavel.infrastructure.graphql;

public record ClassContributionReadModel(int id,
                                         String timestamp,
                                         String vcsIdentifier,
                                         int authorId) {
}
