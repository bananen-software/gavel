package software.bananen.gavel.domain.ports.driven;

import java.util.Collection;

public record SourceFileMetrics(int linesOfCode,
                                int complexity,
                                Collection<CodeUnitMetrics> codeUnits) {
}
