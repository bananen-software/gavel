package software.bananen.gavel.backend.services.usecases;

public record AuthorComplexityMetricsResponseModel(
        String name,
        String email,
        Integer complexityDelta,
        Integer numberOfChanges,
        Double averageComplexityAdded) {
}
