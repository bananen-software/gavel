package software.bananen.gavel.detection;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaPackage;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

public class CyclicDependencyDetectionService {

    public void detect(JavaClasses javaClasses, JavaPackage pkg) {
        slices().matching(pkg.getName() + ".(*)..")
                .should()
                .beFreeOfCycles()
                .check(javaClasses);
    }
}
