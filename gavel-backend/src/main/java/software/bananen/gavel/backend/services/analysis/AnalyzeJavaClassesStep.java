package software.bananen.gavel.backend.services.analysis;

import com.tngtech.archunit.core.domain.JavaClass;
import software.bananen.gavel.backend.domain.ClassAggregate;
import software.bananen.gavel.backend.domain.PackageAggregate;
import software.bananen.gavel.backend.domain.ProjectAggregate;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.contextloader.ProjectContext;

public class AnalyzeJavaClassesStep extends AbstractAnalysisStep {

    private static final String STEP_NAME = "Analyze Java Classes";
    private final ProjectContext projectContext;
    private final ProjectEntity projectEntity;

    /**
     * Creates a new instance.
     *
     * @param taskId The ID of the task that the step belongs to.
     */
    public AnalyzeJavaClassesStep(final String taskId,
                                  final ProjectContext projectContext,
                                  final ProjectEntity projectEntity) {
        super(taskId, STEP_NAME);
        this.projectContext = projectContext;
        this.projectEntity = projectEntity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runAnalysis() {
        final ProjectAggregate projectAggregate = new ProjectAggregate(projectEntity);

        for (final JavaClass javaClass : projectContext.javaClasses()) {
            final PackageAggregate packageAggregate =
                    projectAggregate.findOrCreatePackage(javaClass.getPackageName());

            final String className = javaClass.getSimpleName();

            if (!className.isEmpty() && !className.equals("package-info")) {
                final ClassAggregate classAggregate =
                        packageAggregate.findOrCreateClass(javaClass.getSimpleName());

                //TODO: Record methods
            }
        }
    }
}
