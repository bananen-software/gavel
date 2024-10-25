package software.bananen.gavel.backend.services.analysis;

import gavel.staticanalysis.adapter.ProjectDependency;
import gavel.staticanalysis.adapter.StaticAnalysisAdapterException;
import gavel.staticanalysis.adapter.owaspdependencycheck.OWASPDependencyCheckAdapter;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.backend.repository.ProjectRepository;

import java.io.File;

public class RunOWASPDependencyCheckStep extends AbstractAnalysisStep {
    private final ProjectEntity project;
    private final ProjectRepository projectRepository;
    private final OWASPDependencyCheckAdapter owaspDependencyCheckAdapter;

    /**
     * Creates a new instance.
     *
     * @param taskId                      The ID of the task that the step belongs to.
     * @param project
     * @param projectRepository
     * @param owaspDependencyCheckAdapter
     */
    public RunOWASPDependencyCheckStep(final String taskId,
                                       final ProjectEntity project,
                                       final ProjectRepository projectRepository,
                                       final OWASPDependencyCheckAdapter owaspDependencyCheckAdapter) {
        super(taskId, "OWASP Dependency Check");
        this.project = project;
        this.projectRepository = projectRepository;
        this.owaspDependencyCheckAdapter = owaspDependencyCheckAdapter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runAnalysis() {
        try {
            for (final ProjectDependency projectDependency :
                    owaspDependencyCheckAdapter.checkDependencies(new File(project.getPath()))) {

            }
        } catch (final StaticAnalysisAdapterException e) {
            throw new RuntimeException(e);
        }
    }
}
