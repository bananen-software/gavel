package software.bananen.gavel.metrics;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaPackage;
import com.tngtech.archunit.library.metrics.ArchitectureMetrics;
import com.tngtech.archunit.library.metrics.MetricsComponents;
import com.tngtech.archunit.library.metrics.VisibilityMetrics;

import java.util.ArrayList;
import java.util.Collection;

public class ComponentVisibilityMetricsService {

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
