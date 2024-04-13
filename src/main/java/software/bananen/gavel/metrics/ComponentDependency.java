package software.bananen.gavel.metrics;

/**
 * A record representing the component dependency results for a specific package.
 *
 * @param packageName                        The name of the package.
 * @param efferentCoupling                   The efferent coupling.
 * @param afferentCoupling                   The afferent coupling.
 * @param instability                        The instability.
 * @param abstractness                       The abstractness.
 * @param normalizedDistanceFromMainSequence The normalized distance from the
 *                                           main sequence.
 */
public record ComponentDependency(String packageName,
                                  int efferentCoupling,
                                  int afferentCoupling,
                                  double instability,
                                  double abstractness,
                                  double normalizedDistanceFromMainSequence) {
}
