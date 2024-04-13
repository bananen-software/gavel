package software.bananen.gavel.metrics;

public record CumulativeComponentDependency(int cumulative,
                                            double average,
                                            double relativeAverage,
                                            double normalized) {
}
