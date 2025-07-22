package software.bananen.gavel.backend.services.analysis;

import gavel.adapter.persistence.jpa.JpaProjectEntity;
import software.bananen.gavel.backend.services.domain.ProjectService;
import software.bananen.gavel.domain.model.VulnerabilityFinding;
import software.bananen.gavel.domain.model.VulnerableDependency;
import software.bananen.gavel.domain.ports.driven.StaticAnalysisAdapterException;
import software.bananen.gavel.domain.ports.driven.VulnerabilityCheckPort;

import java.io.File;

import static java.util.Objects.requireNonNull;

public class RunDependencyCheckStep extends AbstractAnalysisStep {

    private final JpaProjectEntity project;
    private final ProjectService projectService;
    private final VulnerabilityCheckPort dependencyCheckAdapter;

    /**
     * Creates a new instance.
     *
     * @param project                The project.
     * @param projectService         The project repository.
     * @param dependencyCheckAdapter The adapter that performs dependency checks.
     */
    public RunDependencyCheckStep(final JpaProjectEntity project,
                                  final ProjectService projectService,
                                  final VulnerabilityCheckPort dependencyCheckAdapter) {
        super("OWASP Dependency Check");

        this.project =
                requireNonNull(project, "The project may not be null");
        this.projectService =
                requireNonNull(projectService, "The project service may not be null");
        this.dependencyCheckAdapter =
                requireNonNull(dependencyCheckAdapter, "The dependency check adapter may not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runAnalysis() {
        try {
            final File projectPath = new File(project.getPath());

            for (final VulnerableDependency vulnerableDependency : dependencyCheckAdapter.checkDependencies(projectPath)) {
                System.out.println(vulnerableDependency.name() + " " + vulnerableDependency.fileName() + " " + vulnerableDependency.vulnerabilitiesCount());

                for (final VulnerabilityFinding finding : vulnerableDependency.findings()) {
                    finding.description();
                    finding.cveRating();
                    finding.name();
                    finding.detailUrl();
                }
            }
        } catch (final StaticAnalysisAdapterException e) {
            throw new RuntimeException(e);
        }
    }
}
