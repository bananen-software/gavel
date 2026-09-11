package software.bananen.gavel.infrastructure.graphql;

import java.util.Collection;

public record WorkspaceReadModel(Long id,
                                 String name,
                                 String path,
                                 String basePackage,
                                 Collection<String> excludedPaths) {
}
