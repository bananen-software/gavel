package software.bananen.gavel.backend.services.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import static java.util.Objects.requireNonNull;

/**
 * An abstract base class that can be used to implement analysis steps.
 */
public abstract class AbstractAnalysisStep implements Runnable {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AbstractAnalysisStep.class);
    private final String stepName;

    /**
     * Creates a new instance.
     *
     * @param stepName The value of the step.
     */
    public AbstractAnalysisStep(
            final String stepName) {
        this.stepName = requireNonNull(stepName, "The step value may not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void run() {
        final StopWatch stopWatch = new StopWatch();

        stopWatch.start(stepName);
        LOGGER.info("Running step: {}", stepName);
        runAnalysis();
        stopWatch.stop();
        LOGGER.info("Completed Step: {} completed in {}ms", stepName, stopWatch.getTotalTimeMillis());
    }

    /**
     * Runs the analysis of the step.
     */
    protected abstract void runAnalysis();
}
