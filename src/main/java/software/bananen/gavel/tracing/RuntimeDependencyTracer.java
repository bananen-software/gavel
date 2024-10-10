package software.bananen.gavel.tracing;

import com.tngtech.archunit.core.domain.*;
import software.bananen.gavel.config.json.RuntimeDependenciesConfig;

import static java.util.Objects.requireNonNull;

public class RuntimeDependencyTracer {

    private final PackageNode rootPackage = new PackageNode();
    private final RuntimeDependenciesConfig config;

    /**
     * Creates a new instance.
     *
     * @param config The configuration that should be used.
     */
    public RuntimeDependencyTracer(final RuntimeDependenciesConfig config) {
        this.config = requireNonNull(config, "The config may not be null");
    }

    /**
     * Adds a JavaClass to the package structure.
     *
     * @param aClass the JavaClass object to be added
     * @throws IllegalArgumentException if the JavaClass cannot be added to the package structure
     */
    public void addClass(final JavaClass aClass) {
        rootPackage.addClass(aClass);
    }

    /**
     * Traces the runtime dependencies of a given JavaMethod.
     *
     * @param method the JavaMethod object to trace the dependencies of
     */
    public void traceRuntimeDependencies(final JavaMethod method) {
        //JavaMethodCall
        //JavaContructorCall
        //JavaFieldAccess (Does this detect changing the value on a field in another class? Static variables and shit?)

        for (final JavaAccess<?> javaCodeUnitAccess : method.getAccessesToSelf()) {
            if (rootPackage.findClass(javaCodeUnitAccess.getOriginOwner()).isEmpty() &&
                    isNotExcluded(javaCodeUnitAccess.getOriginOwner())) {
                rootPackage.addClass(javaCodeUnitAccess.getOriginOwner());
            }

            if (rootPackage.findClass(javaCodeUnitAccess.getTargetOwner()).isEmpty() &&
                    isNotExcluded(javaCodeUnitAccess.getTargetOwner())) {
                rootPackage.addClass(javaCodeUnitAccess.getTargetOwner());
            }
        }

        for (final JavaAccess<?> javaCodeUnitAccess : method.getAccessesFromSelf()) {
            if (rootPackage.findClass(javaCodeUnitAccess.getOriginOwner()).isEmpty() &&
                    isNotExcluded(javaCodeUnitAccess.getOriginOwner())) {
                rootPackage.addClass(javaCodeUnitAccess.getOriginOwner());
            }

            if (rootPackage.findClass(javaCodeUnitAccess.getTargetOwner()).isEmpty() &&
                    isNotExcluded(javaCodeUnitAccess.getTargetOwner())) {
                rootPackage.addClass(javaCodeUnitAccess.getTargetOwner());
            }
        }

        for (final JavaCodeUnitAccess<?> afferentAccess : method.getAccessesToSelf()) {
            if (afferentAccess instanceof JavaMethodCall methodCall) {
                final var sourceNode = rootPackage.findClass(methodCall.getOriginOwner());
                final var targetNode = rootPackage.findClass(methodCall.getTargetOwner());

                if (sourceNode.isPresent() && targetNode.isPresent()) {
                    System.out.println("A(S) " + sourceNode);
                    System.out.println("A(T) " + targetNode);
                }
            }
        }

        for (final JavaAccess<?> efferentAccess : method.getAccessesFromSelf()) {
            if (efferentAccess instanceof JavaMethodCall methodCall) {
                final var sourceNode = rootPackage.findClass(methodCall.getOriginOwner());
                final var targetNode = rootPackage.findClass(methodCall.getTargetOwner());

                if (sourceNode.isPresent() && targetNode.isPresent()) {
                    System.out.println("E(S) " + sourceNode);
                    System.out.println("E(T) " + targetNode);
                }
            }
        }

        //JavaMethodReference
        //JavaConstructorReference
        //Code Unit???
        // Others...
    }

    /**
     * Checks if a given JavaClass is excluded based on the package name.
     *
     * @param clazz The JavaClass object to check.
     * @return true if the JavaClass is excluded, false otherwise.
     */
    private boolean isNotExcluded(final JavaClass clazz) {
        return config.exclusions()
                .stream()
                .noneMatch(exclusion -> clazz.getPackageName().startsWith(exclusion.concat(".")));
    }

    @Override
    public String toString() {
        return rootPackage.toString();
    }

    public PackageNode rootPackage() {
        return rootPackage;
    }
}
