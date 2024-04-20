package software.bananen.gavel.detection;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaPackage;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

class CyclicDependencyDetectionServiceTest {

    @Test
    public void detectCyclicDependency() {
        final JavaClasses javaClasses = new ClassFileImporter().importPackages("software.bananen.gavel.detection.example");

        final JavaPackage pkg = javaClasses.getPackage("software.bananen.gavel.detection.example");

        try {
            new CyclicDependencyDetectionService().detect(javaClasses, pkg);
            failBecauseExceptionWasNotThrown(AssertionError.class);
        } catch (final AssertionError e) {
            final String expected = """
                    Architecture Violation [Priority: MEDIUM] - Rule 'slices matching 'software.bananen.gavel.detection.example.(*)..' should be free of cycles' was violated (1 times):
                    Cycle detected: Slice a ->\s
                                    Slice b ->\s
                                    Slice c ->\s
                                    Slice a
                      1. Dependencies of Slice a
                        - Constructor <software.bananen.gavel.detection.example.a.A.<init>(software.bananen.gavel.detection.example.b.B)> has parameter of type <software.bananen.gavel.detection.example.b.B> in (A.java:0)
                        - Field <software.bananen.gavel.detection.example.a.A.b> has type <software.bananen.gavel.detection.example.b.B> in (A.java:0)
                        - Method <software.bananen.gavel.detection.example.a.A.b()> has return type <software.bananen.gavel.detection.example.b.B> in (A.java:0)
                      2. Dependencies of Slice b
                        - Constructor <software.bananen.gavel.detection.example.b.B.<init>(software.bananen.gavel.detection.example.c.C)> has parameter of type <software.bananen.gavel.detection.example.c.C> in (B.java:0)
                        - Field <software.bananen.gavel.detection.example.b.B.c> has type <software.bananen.gavel.detection.example.c.C> in (B.java:0)
                        - Method <software.bananen.gavel.detection.example.b.B.c()> has return type <software.bananen.gavel.detection.example.c.C> in (B.java:0)
                      3. Dependencies of Slice c
                        - Constructor <software.bananen.gavel.detection.example.c.C.<init>(software.bananen.gavel.detection.example.a.A)> has parameter of type <software.bananen.gavel.detection.example.a.A> in (C.java:0)
                        - Field <software.bananen.gavel.detection.example.c.C.a> has type <software.bananen.gavel.detection.example.a.A> in (C.java:0)
                        - Method <software.bananen.gavel.detection.example.c.C.a()> has return type <software.bananen.gavel.detection.example.a.A> in (C.java:0)""";

            assertThat(e.getMessage()).isEqualTo(expected);
        }
    }
}