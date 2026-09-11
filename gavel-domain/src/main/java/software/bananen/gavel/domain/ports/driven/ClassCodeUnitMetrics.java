package software.bananen.gavel.domain.ports.driven;

import java.util.Collection;

public record ClassCodeUnitMetrics(String name,
                                   String packageName,
                                   Collection<CodeUnitMetrics> children,
                                   String hash,
                                   int linesOfCode,
                                   int linesOfComments,
                                   int complexity) implements CodeUnitMetrics {
}
