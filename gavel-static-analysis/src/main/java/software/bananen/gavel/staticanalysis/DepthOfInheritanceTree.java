package software.bananen.gavel.staticanalysis;

/**
 * A metric representing the depth of inheritance tree.
 *
 * @param packageName The package name.
 * @param className   The class name.
 * @param value       The value of the metric.
 */
public record DepthOfInheritanceTree(String packageName,
                                     String className,
                                     Integer value) {
}
