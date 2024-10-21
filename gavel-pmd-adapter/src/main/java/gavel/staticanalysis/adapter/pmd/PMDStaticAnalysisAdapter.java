package gavel.staticanalysis.adapter.pmd;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.rule.RulePriority;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.RuleViolation;
import net.sourceforge.pmd.util.log.internal.SimpleMessageReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * An adapter that integrates with the PMD static analysis tool.
 */
public final class PMDStaticAnalysisAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PMDStaticAnalysisAdapter.class);

    private static final String DEFAULT_RULESET_PATH =
            requireNonNull(PMDStaticAnalysisAdapter.class.getClassLoader().getResource("default-ruleset.xml")).getPath();

    private final String rulesetPath;
    private final RulePriority minimumPriority;

    /**
     * Creates a new instance.
     */
    public PMDStaticAnalysisAdapter() {
        this(DEFAULT_RULESET_PATH, RulePriority.LOW);
    }

    /**
     * Creates a new instance.
     *
     * @param rulesetPath     The path to the ruleset that should be used.
     * @param minimumPriority The minimum priority that should be used.
     */
    public PMDStaticAnalysisAdapter(final String rulesetPath,
                                    final RulePriority minimumPriority) {
        this.rulesetPath =
                requireNonNull(rulesetPath, "The ruleset path may not be null");
        this.minimumPriority =
                requireNonNull(minimumPriority, "The minimum priority may not be null");
    }

    /**
     * Analyzes the given projectPath recursively using the PMD static code analysis tool.
     *
     * @param projectPath The projects' path.
     * @return The findings.
     */
    public Collection<StaticAnalysisFinding> analyze(final Path projectPath) {
        final Collection<StaticAnalysisFinding> findings = new ArrayList<>();

        final PMDConfiguration configuration = new PMDConfiguration();

        configuration.setRuleSets(List.of(rulesetPath));
        configuration.setInputPathList(List.of(projectPath));
        configuration.setReporter(new SimpleMessageReporter(LOGGER));
        configuration.setMinimumPriority(minimumPriority);

        try (final PmdAnalysis analysis = PmdAnalysis.create(configuration)) {
            analysis.files().setRecursive(true);

            final Report report = analysis.performAnalysisAndCollectReport();

            for (final RuleViolation violation : report.getViolations()) {
                findings.add(new StaticAnalysisFinding(
                        violation.getAdditionalInfo().get("className"),
                        violation.getAdditionalInfo().get("packageName"),
                        violation.getDescription(),
                        violation.getRule().getName(),
                        violation.getRule()
                                .getDescription()
                                .lines()
                                .map(String::trim)
                                .collect(Collectors.joining("\n")),
                        mapToSeverity(violation.getRule().getPriority()),
                        "PMD"
                ));
            }
        }

        return findings;
    }

    /**
     * MAps the given PMD rule priority to its {@link Severity}
     *
     * @param priority The priority that should be mapped.
     * @return The severity.
     */
    private Severity mapToSeverity(final RulePriority priority) {
        switch (priority) {
            case HIGH -> {
                return Severity.HIGH;
            }
            case MEDIUM_HIGH, MEDIUM_LOW, MEDIUM -> {
                return Severity.MEDIUM;
            }
            case LOW -> {
                return Severity.LOW;
            }
        }

        return Severity.LOW;
    }
}
