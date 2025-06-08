package software.bananen.gavel.staticanalysis;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaMethod;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This class provides services for measuring the Lack of Cohesion in Methods (LCOM4) metric.
 * LCOM4 is a software quality metric that measures the degree to which methods of a class
 * are related by accessing shared fields.
 */
public class LCOM4MetricsService {

    private enum NodeType {
        METHOD, FIELD
    }

    private record Node(NodeType type, String name) {
    }

    /**
     * Measures the LCOM4 metric for a collection of Java classes.
     *
     * @param classes the collection of Java classes to measure
     * @return a collection of LCOM4 metrics corresponding to the input classes
     */
    public Collection<LCOM4Metric> measure(final JavaClasses classes) {
        return classes.stream()
                .filter(isClass())
                .map(this::measure).toList();
    }

    /**
     * Measures the LCOM4 metric for a given Java class.
     *
     * @param javaClass the Java class for which the LCOM4 metric will be measured
     * @return the LCOM4 metric of the given Java class, including package name, class name,
     * LCOM4 value, and clusters of methods based on their access to fields
     */
    public LCOM4Metric measure(final JavaClass javaClass) {
        final Graph<Node, DefaultEdge> graph
                = new SimpleDirectedGraph<>(DefaultEdge.class);

        for (final JavaField field : javaClass.getFields()) {
            Node fieldNode = new Node(NodeType.FIELD, field.getName());
            graph.addVertex(fieldNode);
            field.getAccessesToSelf().forEach(accessor -> {
                if (accessor.getOrigin() instanceof JavaMethod) {
                    Node methodNode = new Node(NodeType.METHOD, accessor.getOrigin().getName());
                    graph.addVertex(methodNode);
                    graph.addEdge(methodNode, fieldNode);
                }
            });
        }

        final ConnectivityInspector<Node, DefaultEdge> inspector =
                new ConnectivityInspector<>(graph);

        final Set<Set<String>> clusters =
                inspector.connectedSets().stream()
                        .map(subgraph -> subgraph.stream()
                                .filter(isMethod())
                                .map(Node::name)
                                .collect(Collectors.toSet()))
                        .filter(set -> !set.isEmpty())
                        .collect(Collectors.toSet());

        return new LCOM4Metric(
                javaClass.getPackageName(),
                javaClass.getSimpleName(),
                clusters.size(),
                clusters
        );
    }

    private static Predicate<Node> isMethod() {
        return n -> Objects.equals(n.type, NodeType.METHOD);
    }


    /**
     * Checks whether the given class is a class.
     *
     * @return The predicate
     */
    private Predicate<? super JavaClass> isClass() {
        return javaClass ->
                !javaClass.isInterface() &&
                        !javaClass.isEnum() &&
                        !javaClass.isRecord();
    }
}
