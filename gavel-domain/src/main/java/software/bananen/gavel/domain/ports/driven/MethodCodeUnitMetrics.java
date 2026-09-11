package software.bananen.gavel.domain.ports.driven;

public record MethodCodeUnitMetrics(String name,
                                    String signature,
                                    String hash,
                                    int linesOfCode,
                                    int linesOfComments,
                                    int complexity) implements CodeUnitMetrics {
}
