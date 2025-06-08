package software.bananen.gavel.infrastructure.graphql;

public record RelationalCohesionReadModel(
        String rating,
        int numberOfTypes,
        int numberOfInternalRelationships,
        double relationalCohesion) {
}
