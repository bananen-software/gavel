package software.bananen.gavel.domain.ports.driven;

public sealed interface CodeUnitMetrics
        permits ClassCodeUnitMetrics, MethodCodeUnitMetrics {
}
