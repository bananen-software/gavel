package software.bananen.gavel.infrastructure.graphql;

public record ComponentDependencyReadModel(
        int afferentCoupling,
        int efferentCoupling,
        double abstractness,
        double instability,
        double distance) {
}
