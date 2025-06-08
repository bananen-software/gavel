package software.bananen.gavel.tracing;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a node in a package structure.
 */
public final class PackageNode {

    private final String packageName;
    private final boolean isRootNode;

    private final Map<String, PackageNode> packageNodes =
            new ConcurrentHashMap<>();

    private final Map<String, ClassNode> classNodes =
            new ConcurrentHashMap<>();

    /**
     * Creates a new instance.
     */
    PackageNode() {
        this.packageName = "ROOT";
        this.isRootNode = true;
    }

    /**
     * Creates a new instance.
     *
     * @param packageName The package name.
     */
    PackageNode(final String packageName) {
        this.packageName = packageName;
        this.isRootNode = false;
    }


    /**
     * Adds a JavaClass to the package structure.
     *
     * @param aClass the JavaClass object to be added
     * @throws IllegalArgumentException if the JavaClass cannot be added to the package structure
     */
    public void addClass(final JavaClass aClass) {
        final String[] subpackageLabels = aClass.getPackageName().split("\\.");
        if (isRootNode) {
            final PackageNode childNode =
                    packageNodes.getOrDefault(subpackageLabels[0], new PackageNode(subpackageLabels[0]));

            packageNodes.put(subpackageLabels[0], childNode);
            childNode.addClass(aClass);
        } else if (isSubpackage(aClass)) {
            final String nodePackage = getRelativeSubpackage(aClass.getPackageName());

            final PackageNode childNode =
                    packageNodes.getOrDefault(nodePackage, new PackageNode(nodePackage));

            packageNodes.put(nodePackage, childNode);
            childNode.addClass(aClass);
        } else if (Objects.equals(aClass.getPackageName(), packageName)) {
            final ClassNode classNode = new ClassNode(aClass);

            for (final JavaMethod method : aClass.getMethods()) {
                classNode.addMethod(method);
            }

            classNodes.put(aClass.getSimpleName(), classNode);
        } else {
            throw new IllegalArgumentException("Fail!");
        }
    }

    /**
     * Returns the relative subpackage based on the specified subpackage.
     *
     * @param subpackage the subpackage string to get the relative subpackage from
     * @return the relative subpackage as a string
     */
    private String getRelativeSubpackage(final String subpackage) {
        final String[] subpackageLabels = subpackage.split("\\.");
        final int parentLength = packageName.split("\\.").length;

        if (isRootNode) {
            return Stream.of(subpackageLabels)
                    .limit(1)
                    .collect(Collectors.joining("."));
        } else {
            return Stream.of(subpackageLabels)
                    .limit(parentLength + 1)
                    .collect(Collectors.joining("."));
        }
    }

    /**
     * Finds a class in the package hierarchy.
     *
     * @param aClass the JavaClass object representing the class to find
     * @return an Optional containing the ClassNode if found, or empty if not found
     */
    public Optional<ClassNode> findClass(final JavaClass aClass) {
        if (packageName.equals(aClass.getPackageName())) {
            return Optional.ofNullable(
                    classNodes.getOrDefault(aClass.getSimpleName(), null));
        }

        return findPackage(aClass.getPackageName()).flatMap(pkg -> pkg.findClass(aClass));
    }

    /**
     * Searches for a PackageNode with the given package name.
     *
     * @param requestedPackageName The package name to search for.
     * @return An Optional containing the found PackageNode, or an empty Optional if not found.
     */
    public Optional<PackageNode> findPackage(final String requestedPackageName) {
        if (Objects.equals(this.packageName, requestedPackageName)) {
            return Optional.of(this);
        }

        if (isRootNode) {
            final String parentPackage = requestedPackageName.split("\\.")[0];

            return Optional.ofNullable(packageNodes.getOrDefault(parentPackage, null))
                    .flatMap(node -> node.findPackage(requestedPackageName));
        } else if (isSubpackage(requestedPackageName)) {

            return Optional.ofNullable(packageNodes.getOrDefault(getRelativeSubpackage(requestedPackageName), null))
                    .flatMap(node -> node.findPackage(requestedPackageName));
        }

        return Optional.empty();
    }

    /**
     * Checks if a given JavaClass is a subpackage of the current package.
     *
     * @param aClass The JavaClass to check.
     * @return true if the JavaClass is a subpackage, false otherwise.
     */
    private boolean isSubpackage(final JavaClass aClass) {
        return isSubpackage(aClass.getPackageName());
    }

    /**
     * Checks if the given package name is a subpackage of the current package.
     *
     * @param packageName The name of the package to be checked.
     * @return true if the given package name is a subpackage, false otherwise.
     */
    private boolean isSubpackage(final String packageName) {
        return packageName.startsWith(this.packageName + ".");
    }

    public Optional<MethodNode> findMethod(final JavaMethod method) {
        return findClass(method.getOwner())
                .flatMap(clazz -> clazz.findMethod(method));
    }

    public Integer calculateSize() {
        Integer size = 0;

        size += classNodes.size();

        for (final PackageNode pkg : packageNodes.values()) {
            size += pkg.calculateSize();
        }

        return size;
    }

    public Collection<PackageNode> subpackages() {
        return packageNodes.values();
    }

    public Collection<JsonNode> toJsonChildren() {
        final Collection<JsonNode> children = new ArrayList<>();

        for (final PackageNode packageNode : packageNodes.values()) {
            children.add(new JsonNode(
                    packageNode.packageName,
                    packageNode.calculateSize(),
                    packageNode.toJsonChildren(),
                    "package"
            ));
        }

        for (final ClassNode classNode : classNodes.values()) {
            children.add(new JsonNode(
                    classNode.name(),
                    classNode.numberOfMethods(),
                    new ArrayList<>(),
                    "class"
            ));
        }


        return children;
    }
}
