package software.bananen.gavel.metrics;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;

import java.util.ArrayList;
import java.util.Collection;

public class DepthOfInheritanceTreeMetricsService {

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
