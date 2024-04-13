package software.bananen.gavel.metrics;

/**
 * A record that can be used to represent the cumulative component dependencies
 * within a system.
 *
 * @param cumulative      The cumulative component dependency. (CCD)
 * @param average         The average component dependency. (ACD)
 * @param relativeAverage The relative average component dependency. (RACD)
 * @param normalized      The normalized cumulative component dependency. (NCCD)
 */
public record CumulativeComponentDependency(int cumulative,
                                            double average,
                                            double relativeAverage,
                                            double normalized) {
}
