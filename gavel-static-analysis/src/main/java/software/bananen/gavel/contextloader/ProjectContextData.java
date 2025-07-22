package software.bananen.gavel.contextloader;

import java.util.Collection;

/**
 * A data object that represents a project context.
 *
 * @param includedPath  The included path.
 * @param excludedPaths The excluded paths.
 * @param rootPackage   The root package that should be scanned for.
 */
public record ProjectContextData(String includedPath,
                                 Collection<String> excludedPaths,
                                 String rootPackage) {
}
