package software.bananen.gavel.metrics;

/**
 * A metric representing the depth of inheritance tree.
 *
 * @param className The class name.
 * @param value     The value of the metric.
 */
public record DepthOfInheritanceTree(String className, Integer value) {
}
