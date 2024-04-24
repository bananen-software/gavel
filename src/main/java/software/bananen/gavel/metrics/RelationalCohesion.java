package software.bananen.gavel.metrics;

/**
 * A record that represents the relational cohesion of a package.
 *
 * @param packageName                   The name of the package.
 * @param numberOfTypes                 The number of types contained in the package.
 * @param numberOfInternalRelationships The number of internal relationships contained in the package.
 * @param relationalCohesion            The relational cohesion of the package.
 */
public record RelationalCohesion(String packageName,
                                 int numberOfTypes,
                                 int numberOfInternalRelationships,
                                 double relationalCohesion) {
}
