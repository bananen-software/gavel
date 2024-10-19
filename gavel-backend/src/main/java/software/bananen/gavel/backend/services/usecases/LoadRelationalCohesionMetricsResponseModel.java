package software.bananen.gavel.backend.services.usecases;

import software.bananen.gavel.backend.domain.RelationalCohesionRating;

public record LoadRelationalCohesionMetricsResponseModel(
        String packageName,
        double relationalCohesion,
        Integer numberOfTypes,
        Integer numberOfInternalRelationships,
        RelationalCohesionRating rating) {
}
