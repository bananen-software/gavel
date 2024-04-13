package software.bananen.gavel.metrics;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaPackage;
import com.tngtech.archunit.library.metrics.ArchitectureMetrics;
import com.tngtech.archunit.library.metrics.MetricsComponents;
import com.tngtech.archunit.library.metrics.VisibilityMetrics;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A service that can be used to measure the component visibility metrics.
 */
public class ComponentVisibilityMetricsService {

    /**
     * Measures the component visibility metrics.
     *
     * @param pkg                The package that the measurement should be
     *                           performed for.
     * @param resolveSubpackages A flag that indicates whether the subpackages
     *                           should be resolved.
     * @return The measurements.
     */
    public Collection<ComponentVisibility> measure(final JavaPackage pkg,
                                                   final boolean resolveSubpackages) {
        final MetricsComponents<JavaClass> components =
                MetricsComponents.fromPackages(pkg.getSubpackages());
        final VisibilityMetrics metrics = ArchitectureMetrics.visibilityMetrics(components);

        final Collection<ComponentVisibility> measurements = new ArrayList<>();

        for (final JavaPackage subpackage : pkg.getSubpackages()) {

            measurements.add(new ComponentVisibility(
                    subpackage.getName(),
                    metrics.getRelativeVisibility(subpackage.getName()),
                    metrics.getAverageRelativeVisibility(),
                    metrics.getGlobalRelativeVisibility()
            ));

            if (resolveSubpackages) {
                measurements.addAll(measure(subpackage, true));
            }
        }

        return measurements;
    }
}
