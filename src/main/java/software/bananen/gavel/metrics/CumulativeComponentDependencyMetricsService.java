package software.bananen.gavel.metrics;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaPackage;
import com.tngtech.archunit.library.metrics.ArchitectureMetrics;
import com.tngtech.archunit.library.metrics.LakosMetrics;
import com.tngtech.archunit.library.metrics.MetricsComponents;

public class CumulativeComponentDependencyMetricsService {

    public CumulativeComponentDependency measure(final JavaPackage pkg) {
        final MetricsComponents<JavaClass> components =
                MetricsComponents.fromPackages(pkg.getSubpackages());

        final LakosMetrics metrics = ArchitectureMetrics.lakosMetrics(components);

        return new CumulativeComponentDependency(
                metrics.getCumulativeComponentDependency(),
                metrics.getAverageComponentDependency(),
                metrics.getRelativeAverageComponentDependency(),
                metrics.getNormalizedCumulativeComponentDependency()
        );
    }
}
