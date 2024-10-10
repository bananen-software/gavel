package software.bananen.gavel.backend.services.usecases;

public record LoadRelationalCohesionMetricsResponseModel(
        String packageName,
        double relationalCohesion,
        Integer numberOfTypes,
        Integer numberOfInternalRelationships) {
}
