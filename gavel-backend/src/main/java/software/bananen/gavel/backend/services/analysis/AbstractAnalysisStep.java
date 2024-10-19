package software.bananen.gavel.backend.services.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

/**
 * An abstract base class that can be used to implement analysis steps.
 */
public abstract class AbstractAnalysisStep implements Runnable {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AbstractAnalysisStep.class);
    private final String taskId;
    private final String stepName;

    /**
     * Creates a new instance.
     *
     * @param taskId   The ID of the task that the step belongs to.
     * @param stepName The name of the step.
     */
    public AbstractAnalysisStep(
            final String taskId,
            final String stepName) {
        this.taskId = taskId;
        this.stepName = stepName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void run() {
        final StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        LOGGER.info("[Task: {}] Running step: {}", taskId, stepName);
        runAnalysis();
        stopWatch.stop();
        LOGGER.info("[Task: {}] Completed Step: {} completed in {}ms", taskId, stepName, stopWatch.getTotalTimeMillis());
    }

    /**
     * Runs the analysis of the step.
     */
    protected abstract void runAnalysis();
}
