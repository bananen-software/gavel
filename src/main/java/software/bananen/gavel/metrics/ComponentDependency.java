package software.bananen.gavel.metrics;

public record ComponentDependency(String packageName,
                                  int efferentCoupling,
                                  int afferentCoupling,
                                  double instability,
                                  double abstractness,
                                  double normalizedDistanceFromMainSequence) {
}
