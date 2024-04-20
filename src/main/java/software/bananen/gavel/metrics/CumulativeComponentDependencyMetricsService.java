package software.bananen.gavel.metrics;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaPackage;
import com.tngtech.archunit.library.metrics.ArchitectureMetrics;
import com.tngtech.archunit.library.metrics.LakosMetrics;
import com.tngtech.archunit.library.metrics.MetricsComponents;

import java.util.ArrayList;
import java.util.Collection;

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
    public Collection<CumulativeComponentDependency> measure(final JavaPackage pkg) {
        final MetricsComponents<JavaClass> components =
                MetricsComponents.fromPackages(pkg.getSubpackages());

        final LakosMetrics metrics = ArchitectureMetrics.lakosMetrics(components);

        final Collection<CumulativeComponentDependency> measurements =
                new ArrayList<>();


        measurements.add(new CumulativeComponentDependency(
                pkg.getName(),
                metrics.getCumulativeComponentDependency(),
                Double.isNaN(metrics.getAverageComponentDependency()) ? 0 : metrics.getAverageComponentDependency(),
                Double.isNaN(metrics.getRelativeAverageComponentDependency()) ? 0 : metrics.getRelativeAverageComponentDependency(),
                Double.isNaN(metrics.getNormalizedCumulativeComponentDependency()) ? 0 : metrics.getNormalizedCumulativeComponentDependency()
        ));

        measurements.addAll(measure(pkg.getSubpackages()));

        return measurements;
    }

    /**
     * Measures the cumulative component dependency for the given packages.
     *
     * @param packages The packages.
     * @return The measurements.
     */
    public Collection<CumulativeComponentDependency> measure(final Collection<JavaPackage> packages) {
        final Collection<CumulativeComponentDependency> measurements =
                new ArrayList<>();

        for (final JavaPackage aPackage : packages) {
            measurements.addAll(measure(aPackage));
        }

        return measurements;
    }
}
