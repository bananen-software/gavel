package software.bananen.gavel.backend.services.analysis;

import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.contextloader.ProjectContext;
import software.bananen.gavel.staticanalysis.DepthOfInheritanceTree;
import software.bananen.gavel.staticanalysis.DepthOfInheritanceTreeMetricsService;

import static java.util.Objects.requireNonNull;

public class AnalyzeDepthOfInheritanceTreeStep extends AbstractAnalysisStep {

    private static final String STEP_NAME = "Analyze component visibility";
    private final DepthOfInheritanceTreeMetricsService service;
    private final ProjectContext projectContext;
    private final ProjectEntity project;

    /**
     * Creates a new instance.
     *
     * @param taskId         The ID of the task.
     * @param service        The service that should be used by the step.
     * @param projectContext The project context that should be analyzed.
     */
    public AnalyzeDepthOfInheritanceTreeStep(
            final String taskId,
            final DepthOfInheritanceTreeMetricsService service,
            final ProjectContext projectContext,
            final ProjectEntity project) {
        super(taskId, STEP_NAME);
        this.service = requireNonNull(service, "The service may not be null");
        this.projectContext =
                requireNonNull(projectContext, "The project context may not be null");
        this.project = project;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runAnalysis() {
        for (final DepthOfInheritanceTree measurement :
                service.measure(projectContext.javaClasses(), 0)) {
            //TODO: Record in database
        }
    }
}
