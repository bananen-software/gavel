package software.bananen.gavel.metrics;

import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaPackage;

import java.util.*;

/**
 * A service that can be used to measure the relational cohesion of a project.
 */
public class RelationalCohesionMetricsService {


    /**
     * Measures the relational cohesion for the given packages.
     *
     * @param packages The packages.
     * @return The measurements.
     */
    public Collection<RelationalCohesion> measure(final Collection<JavaPackage> packages) {
        final Collection<RelationalCohesion> measurements = new ArrayList<>();

        for (final JavaPackage aPackage : packages) {
            final Set<JavaClass> classes = aPackage.getClasses();

            final int numberOfTypes = classes.size();
            int numberOfInternalRelationships = 0;

            for (final JavaClass aClass : classes) {
                final Set<JavaClass> resolvedDependencies = new HashSet<>();

                for (final Dependency dependency : aClass.getDirectDependenciesFromSelf()) {
                    final JavaClass targetClass = dependency.getTargetClass();

                    if (!resolvedDependencies.contains(targetClass) && Objects.equals(aPackage, targetClass.getPackage())) {
                        numberOfInternalRelationships++;
                        resolvedDependencies.add(targetClass);
                    }
                }
            }

            final double relationalCohesion =
                    (numberOfInternalRelationships + 1) / (double) numberOfTypes;

            if (numberOfTypes > 0) {
                measurements.add(new RelationalCohesion(
                        aPackage.getName(),
                        numberOfTypes,
                        numberOfInternalRelationships,
                        relationalCohesion
                ));
            }

            measurements.addAll(measure(aPackage.getSubpackages()));
        }

        return measurements;
    }
}
