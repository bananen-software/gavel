package software.bananen.gavel.backend.services.usecases;

public record LoadVisibilityMetricsResponseModel(String packageName,
                                                 double relativeVisibility,
                                                 double averageRelativeVisibility,
                                                 double globalRelativeVisibility) {
}
