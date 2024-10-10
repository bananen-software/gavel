package software.bananen.gavel.reports;

import software.bananen.gavel.metrics.AuthorComplexityHistoryEntry;
import software.bananen.gavel.metrics.ChangeCouplingMetric;
import software.bananen.gavel.metrics.CodeHotspot;

import java.util.Collection;

/**
 * A factory for the reports provided by gavel.
 */
public interface ReportFactory {
    /**
     * Creates an author complexity history report.
     *
     * @return The report.
     */
    Report<Collection<AuthorComplexityHistoryEntry>> createAuthorComplexityHistoryReport();

    /**
     * Creates a code hotspot report.
     *
     * @return The report.
     */
    Report<Collection<CodeHotspot>> createCodeHotspotReport();

    /**
     * Creates a change coupling metric report.
     *
     * @return The report.
     */
    Report<Collection<ChangeCouplingMetric>> createChangeCouplingMetricReport();
}
