package software.bananen.gavel.contextloader;

import java.util.Collection;

public record ProjectContextData(String includedPath,
                                 Collection<String> excludedPaths,
                                 String rootPackage) {
}
