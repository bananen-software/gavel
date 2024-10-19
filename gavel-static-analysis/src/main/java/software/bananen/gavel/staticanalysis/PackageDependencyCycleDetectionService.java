package software.bananen.gavel.staticanalysis;

import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaPackage;
import com.tngtech.archunit.core.domain.SourceCodeLocation;
import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This service allows to detect cyclic package dependencies within a given
 * root package.
 */
public class PackageDependencyCycleDetectionService {

    private record Node(String name) {
    }

    /**
     * Detects cyclic package dependencies in the given root package.
     * <p>
     * This will only return one cycle of package dependencies, even if there
     * are multiple.
     *
     * @param rootPackage The root package.
     * @return The
     */
    public Optional<PackageCycle> detectCycles(final JavaPackage rootPackage) {
        final Graph<Node, DefaultEdge> graph
                = new SimpleDirectedGraph<>(DefaultEdge.class);

        buildSubpackageGraph(graph, rootPackage, rootPackage);

        final CycleDetector<Node, DefaultEdge> cycleDetector = new CycleDetector<>(graph);

        if (cycleDetector.detectCycles()) {
            return Optional.of(new PackageCycle(cycleDetector.findCycles().stream().map(Node::name).toList()));
        }

        return Optional.empty();
    }

    /**
     * Builds the subpackage graph that is used to detect the cyclic dependencies.
     * This method is invoked recursively to process the entire subpackage tree.
     *
     * @param graph       The graph that tracks the subpackages.
     * @param rootPackage The actual root package of the analysis.
     * @param javaPackage The subpackage that should be analysed.
     */
    private void buildSubpackageGraph(final Graph<Node, DefaultEdge> graph,
                                      final JavaPackage rootPackage,
                                      final JavaPackage javaPackage) {
        for (final JavaPackage subpackage : javaPackage.getSubpackagesInTree()) {
            final Node currentPackage = new Node(subpackage.getName());

            if (!graph.containsVertex(currentPackage)) {
                graph.addVertex(currentPackage);
            }

            subpackage.getClassDependenciesToThisPackage()
                    .stream()
                    .map(Dependency::getSourceCodeLocation)
                    .map(SourceCodeLocation::getSourceClass)
                    .map(clazz -> new Node(clazz.getPackageName()))
                    .filter(node -> node.name.startsWith(rootPackage.getName() + "."))
                    .collect(Collectors.toSet())
                    .forEach(pkg -> {
                        if (!graph.containsVertex(pkg)) {
                            graph.addVertex(pkg);
                        }
                        if (!graph.containsEdge(pkg, currentPackage)) {
                            graph.addEdge(pkg, currentPackage);
                        }
                    });

            subpackage.getClassDependenciesFromThisPackage()
                    .stream()
                    .map(Dependency::getTargetClass)
                    .map(clazz -> new Node(clazz.getPackageName()))
                    .filter(node -> node.name.startsWith(rootPackage.getName() + "."))
                    .collect(Collectors.toSet())
                    .forEach(pkg -> {
                        if (!graph.containsVertex(pkg)) {
                            graph.addVertex(pkg);
                        }
                        if (!graph.containsEdge(currentPackage, pkg)) {
                            graph.addEdge(currentPackage, pkg);
                        }
                    });

            buildSubpackageGraph(graph, rootPackage, subpackage);
        }
    }
}
