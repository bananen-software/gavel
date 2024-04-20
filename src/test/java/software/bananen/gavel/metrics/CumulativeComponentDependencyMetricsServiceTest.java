package software.bananen.gavel.metrics;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaPackage;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class CumulativeComponentDependencyMetricsServiceTest {

    @Test
    public void measureCumulativeComponentDependency() {
        final JavaClasses javaClasses = new ClassFileImporter()
                .importPackages("software.bananen.gavel.metrics.examples.cumulativecomponentdependency");

        JavaPackage pkg = javaClasses.getPackage("software.bananen.gavel.metrics.examples.cumulativecomponentdependency");

        Collection<CumulativeComponentDependency> measurements = new CumulativeComponentDependencyMetricsService().measure(pkg);

        assertThat(measurements).contains(new CumulativeComponentDependency(
                "software.bananen.gavel.metrics.examples.cumulativecomponentdependency",
                14,
                2.3333333333333335,
                0.3888888888888889,
                1
        ));
    }
}