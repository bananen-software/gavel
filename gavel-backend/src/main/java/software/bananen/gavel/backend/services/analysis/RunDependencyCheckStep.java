package software.bananen.gavel.backend.services.analysis;

import software.bananen.gavel.backend.services.domain.ProjectService;
import software.bananen.gavel.domain.model.VulnerableDependency;
import software.bananen.gavel.domain.ports.driven.StaticAnalysisAdapterException;
import software.bananen.gavel.domain.ports.driven.VulnerabilityCheckPort;
import software.bananen.gavel.infrastructure.persistence.jpa.ProjectEntity;

import java.io.File;

import static java.util.Objects.requireNonNull;

public class RunDependencyCheckStep extends AbstractAnalysisStep {

    private final ProjectEntity project;
    private final ProjectService projectService;
    private final VulnerabilityCheckPort dependencyCheckAdapter;

    /**
     * Creates a new instance.
     *
     * @param project                The project.
     * @param projectService         The project repository.
     * @param dependencyCheckAdapter The adapter that performs dependency checks.
     */
    public RunDependencyCheckStep(final ProjectEntity project,
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

            for (final VulnerableDependency vulnerableDependency :
                    dependencyCheckAdapter.checkDependencies(projectPath)) {
                if (vulnerableDependency.vulnerabilitiesCount() > 0) {
                    //TODO: Record these vulnerabilities
                    System.out.println("Found vulnerabilities: " + vulnerableDependency);
                }
            }
        } catch (final StaticAnalysisAdapterException e) {
            throw new RuntimeException(e);
        }
    }
}
