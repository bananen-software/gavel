package software.bananen.gavel.staticanalysis;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A service that can be used to measure the depth of inheritance for the
 * analyzed classes.
 */
public final class DepthOfInheritanceTreeMetricsService {

    /**
     * Measures the metrics for the given classes.
     *
     * @param classes   The classes that should be analyzed.
     * @param threshold The threshold that should be used for the detection of
     *                  problematic inheritance hierarchies.
     * @return The measurements.
     */
    public Collection<DepthOfInheritanceTree> measure(final JavaClasses classes, int threshold) {
        final Collection<DepthOfInheritanceTree> measurements =
                new ArrayList<>();

        for (final JavaClass aClass : classes) {
            if (aClass.getClassHierarchy().size() >= threshold) {
                measurements.add(new DepthOfInheritanceTree(
                        aClass.getPackageName(),
                        aClass.getSimpleName(),
                        aClass.getClassHierarchy().size()
                ));
            }
        }

        return measurements;
    }
}
