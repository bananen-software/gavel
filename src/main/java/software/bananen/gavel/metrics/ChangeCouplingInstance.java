package software.bananen.gavel.metrics;

/**
 * An instance of a change coupling that indicates that two files have been
 * changed in the same commit.
 *
 * @param origin The origin.
 * @param target The target
 */
public record ChangeCouplingInstance(String origin, String target) {
}
