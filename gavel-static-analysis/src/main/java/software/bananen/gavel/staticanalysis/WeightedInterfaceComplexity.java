package software.bananen.gavel.staticanalysis;

/**
 * The weighted interface complexity of a class.
 *
 * @param className      The name of the class.
 * @param methodScore    The method score.
 * @param parameterScore The parameter score.
 * @param datatypeScore  The datatype score.
 * @param nestingScore   The nesting score.
 * @param score          The total score aka the weighted interface complexity.
 */
public record WeightedInterfaceComplexity(String className,
                                          int methodScore,
                                          int parameterScore,
                                          int datatypeScore,
                                          int nestingScore,
                                          int score) {
}
