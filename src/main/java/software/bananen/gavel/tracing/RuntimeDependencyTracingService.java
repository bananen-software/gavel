package software.bananen.gavel.tracing;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import software.bananen.gavel.config.json.RuntimeDependenciesConfig;

public final class RuntimeDependencyTracingService {

    /**
     * Traces the runtime dependencies between the supplied classes.
     *
     * @param classes The classes for which runtime dependencies should be traced.
     */
    public JsonNode traceRuntimeDependencies(final JavaClasses classes,
                                             final RuntimeDependenciesConfig config) {
        final RuntimeDependencyTracer tracer = new RuntimeDependencyTracer(config);

        for (final JavaClass aClass : classes) {
            tracer.addClass(aClass);
        }

        for (final JavaClass aClass : classes) {
            for (final JavaMethod method : aClass.getMethods()) {
                tracer.traceRuntimeDependencies(method);
            }
        }

        PackageNode rootPackage = tracer.rootPackage();

        return new JsonNode("MySuperCoolApp",
                rootPackage.calculateSize(),
                rootPackage.toJsonChildren(),
                "package");
    }
}
