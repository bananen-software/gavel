package software.bananen.gavel.staticanalysis;

/**
 * A record representing the component visibility within a system.
 *
 * @param packageName               The name of the package.
 * @param relativeVisibility        The relative visibility of the package. (RV)
 * @param averageRelativeVisibility The average relative visibility within the
 *                                  system. (ARV)
 * @param globalRelativeVisibility  The global relative visibility within the
 *                                  system. (GRV)
 */
public record ComponentVisibility(String packageName,
                                  double relativeVisibility,
                                  double averageRelativeVisibility,
                                  double globalRelativeVisibility) {
}
