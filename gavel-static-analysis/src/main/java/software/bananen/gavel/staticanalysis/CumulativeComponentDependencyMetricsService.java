package software.bananen.gavel.staticanalysis;

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
     * @param pkg                The package.
     * @param resolveSubpackages A flag that indicates whether the subpackages
     *                           should be resolved.
     * @return The measurements.
     */
    public Collection<CumulativeComponentDependency> measure(
            final JavaPackage pkg,
            final boolean resolveSubpackages) {
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

        if (resolveSubpackages) {
            measurements.addAll(measure(pkg.getSubpackages(), resolveSubpackages));
        }

        return measurements;
    }

    /**
     * Measures the cumulative component dependency for the given packages.
     *
     * @param packages           The packages.
     * @param resolveSubpackages A flag that indicates whether the subpackages
     *                           should be resolved.
     * @return The measurements.
     */
    public Collection<CumulativeComponentDependency> measure(final Collection<JavaPackage> packages,
                                                             final boolean resolveSubpackages) {
        final Collection<CumulativeComponentDependency> measurements =
                new ArrayList<>();

        for (final JavaPackage aPackage : packages) {
            measurements.addAll(measure(aPackage, resolveSubpackages));
        }

        return measurements;
    }
}
