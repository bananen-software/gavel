package software.bananen.gavel.staticanalysis;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaPackage;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class CumulativeComponentDependencyMetricsServiceTest {

    @Test
    public void measureCumulativeComponentDependency() {
        final String packageName = "software.bananen.gavel.staticanalysis.examples.cumulativecomponentdependency";

        final JavaClasses javaClasses = new ClassFileImporter()
                .importPackages(packageName);

        JavaPackage pkg = javaClasses.getPackage(packageName);

        Collection<CumulativeComponentDependency> measurements =
                new CumulativeComponentDependencyMetricsService().measure(pkg, false);

        assertThat(measurements).containsExactly(new CumulativeComponentDependency(
                packageName,
                14,
                2.3333333333333335,
                0.3888888888888889,
                1
        ));
    }
}