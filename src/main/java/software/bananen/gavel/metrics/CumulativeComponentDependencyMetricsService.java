package software.bananen.gavel.metrics;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaPackage;
import com.tngtech.archunit.library.metrics.ArchitectureMetrics;
import com.tngtech.archunit.library.metrics.LakosMetrics;
import com.tngtech.archunit.library.metrics.MetricsComponents;

/**
 * A service that can be used to measure the cumulative component dependency
 * metrics for a system.
 */
public class CumulativeComponentDependencyMetricsService {

    /**
     * Measures the cumulative component dependency for the given package.
     *
     * @param pkg The package.
     * @return The measurements.
     */
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
