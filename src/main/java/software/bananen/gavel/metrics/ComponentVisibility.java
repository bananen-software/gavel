package software.bananen.gavel.metrics;

public record ComponentVisibility(String packageName,
                                  double relativeVisibility,
                                  double averageRelativeVisibility,
                                  double globalRelativeVisibility) {
}
