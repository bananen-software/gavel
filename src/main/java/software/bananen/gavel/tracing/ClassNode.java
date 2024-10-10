package software.bananen.gavel.tracing;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import software.bananen.gavel.metrics.WeightedInterfaceComplexityMetricsService;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ClassNode {

    private final String className;
    private final JavaClass clazz;

    private final Map<String, MethodNode> methods = new ConcurrentHashMap<>();

    ClassNode(final JavaClass clazz) {
        this.className = clazz.getSimpleName();
        this.clazz = clazz;
    }

    public boolean isInterface() {
        return clazz.isInterface();
    }

    public boolean isAbstract() {
        return clazz.getModifiers().contains(JavaModifier.ABSTRACT);
    }

    public String name() {
        return className;
    }

    public void addMethod(final JavaMethod method) {
        methods.put(method.getFullName(), new MethodNode(method.getFullName()));
    }

    public Optional<MethodNode> findMethod(final JavaMethod method) {
        return Optional.ofNullable(
                methods.getOrDefault(method.getFullName(), null));
    }

    public Integer calculateComplexity() {
        return new WeightedInterfaceComplexityMetricsService().determineInterfaceComplexity(clazz).score();
    }

    public Integer numberOfMethods() {
        return methods.size();
    }

    @Override
    public String toString() {
        return className;
    }
}
