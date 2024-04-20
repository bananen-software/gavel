package software.bananen.gavel.metrics;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class DepthOfInheritanceTreeMetricsServiceTest {

    @Test
    public void measureDepthOfInheritanceTree() {
        final JavaClasses javaClasses = new ClassFileImporter()
                .importPackages("software.bananen.gavel.metrics.examples.depthofinheritancetree");

        final Collection<DepthOfInheritanceTree> measurements = new DepthOfInheritanceTreeMetricsService().measure(javaClasses);

        assertThat(measurements).containsOnly(
                new DepthOfInheritanceTree("software.bananen.gavel.metrics.examples.depthofinheritancetree.A", 2),
                new DepthOfInheritanceTree("software.bananen.gavel.metrics.examples.depthofinheritancetree.B", 3),
                new DepthOfInheritanceTree("software.bananen.gavel.metrics.examples.depthofinheritancetree.C", 4));
    }
}