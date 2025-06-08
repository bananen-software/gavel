package software.bananen.gavel.backend.services.usecases;

public record LoadComponentDependencyMetricsResponseModel(
        String packageName,
        Integer afferentCoupling,
        Integer efferentCoupling,
        Double abstractness,
        Double instability,
        Double normalizedDistanceFromMainSequence) {
}
