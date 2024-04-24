package software.bananen.gavel.reports;

import software.bananen.gavel.metrics.*;

import java.util.Collection;

/**
 * A factory for the reports provided by gavel.
 */
public interface ReportFactory {

    /**
     * Creates a component dependency report.
     *
     * @return The report.
     */
    Report<Collection<ComponentDependency>> createComponentDependencyReport();

    /**
     * Creates a cumulative component dependency report.
     *
     * @return The report.
     */
    Report<Collection<CumulativeComponentDependency>> createCumulativeComponentDependencyReport();

    /**
     * Creates a depth of inheritance tree report.
     *
     * @return The report.
     */
    Report<Collection<DepthOfInheritanceTree>> createDepthOfInheritanceTreeReport();

    /**
     * Creates a component visibility report.
     *
     * @return The report.
     */
    Report<Collection<ComponentVisibility>> createComponentVisibilityReport();

    /**
     * Creates a relational cohesion report.
     *
     * @return The report.
     */
    Report<Collection<RelationalCohesion>> createRelationalCohesionReport();
}
