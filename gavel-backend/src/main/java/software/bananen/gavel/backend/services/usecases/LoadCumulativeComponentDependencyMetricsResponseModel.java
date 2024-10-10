package software.bananen.gavel.backend.services.usecases;

public record LoadCumulativeComponentDependencyMetricsResponseModel(
        String packageName,
        Integer cumulative,
        Double average,
        Double relativeAverage,
        Double normalized) {
}
