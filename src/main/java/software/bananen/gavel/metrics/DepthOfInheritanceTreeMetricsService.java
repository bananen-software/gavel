package software.bananen.gavel.metrics;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A service that can be used to measure the depth of inheritance for the
 * analyzed classes.
 */
public class DepthOfInheritanceTreeMetricsService {

    /**
     * Measures the metrics for the given classes.
     *
     * @param classes The classes that should be analyzed.
     * @return The measurements.
     */
    public Collection<DepthOfInheritanceTree> measure(final JavaClasses classes) {
        final Collection<DepthOfInheritanceTree> measurements =
                new ArrayList<>();

        for (final JavaClass aClass : classes) {
            measurements.add(new DepthOfInheritanceTree(
                    aClass.getName(),
                    aClass.getClassHierarchy().size()
            ));
        }

        return measurements;
    }
}
