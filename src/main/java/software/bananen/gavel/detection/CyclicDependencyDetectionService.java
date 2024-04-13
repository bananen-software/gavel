package software.bananen.gavel.detection;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaPackage;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * A service that can be used to detect cyclic dependencies.
 */
public class CyclicDependencyDetectionService {

    /**
     * Detects the cyclic dependencies in the given package.
     *
     * @param javaClasses The java classes.
     * @param pkg         The package that the detection should run for.
     */
    public void detect(JavaClasses javaClasses, JavaPackage pkg) {
        slices().matching(pkg.getName() + ".(*)..")
                .should()
                .beFreeOfCycles()
                .check(javaClasses);
    }
}
