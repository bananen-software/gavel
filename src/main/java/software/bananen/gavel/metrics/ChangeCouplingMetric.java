package software.bananen.gavel.metrics;

/**
 * A metric that can be used to represent the change coupling between two files
 * in the same repository.
 *
 * @param origin         The origin origin.
 * @param target         The target origin.
 * @param totalChanges   The total number of changes to the origin origin.
 * @param coupledChanges The number of coupled changes.
 * @param coupling       The coupling in percentage.
 */
public record ChangeCouplingMetric(String origin,
                                   String target,
                                   int totalChanges,
                                   int coupledChanges,
                                   double coupling) {
}
