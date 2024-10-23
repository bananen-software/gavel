package gavel.staticanalysis.adapter.spotbugs;

import edu.umd.cs.findbugs.*;
import edu.umd.cs.findbugs.config.UserPreferences;
import gavel.staticanalysis.adapter.Severity;
import gavel.staticanalysis.adapter.StaticAnalysisAdapterException;
import gavel.staticanalysis.adapter.StaticAnalysisClassFinding;
import gavel.staticanalysis.adapter.StaticCodeAnalysisAdapter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * An adapter that can be used to run the spotbugs static code analysis.
 */
public final class SpotbugsAdapter implements StaticCodeAnalysisAdapter {

    private final Path javaHomeDirectory;

    /**
     * Creates a new instance.
     *
     * @param javaHomeDirectory The java home directory.
     */
    public SpotbugsAdapter(final Path javaHomeDirectory) {
        this.javaHomeDirectory =
                requireNonNull(javaHomeDirectory, "The java home directory may not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<StaticAnalysisClassFinding> analyze(final Path projectPath) throws StaticAnalysisAdapterException {
        try (Project project = new Project()) {
            try (FindBugs2 engine = new FindBugs2()) {
                final BugCollectionBugReporter reporter = new BugCollectionBugReporter(project);
                final DetectorFactoryCollection detectorFactoryCollection = DetectorFactoryCollection.instance();

                DetectorFactoryCollection.resetInstance(detectorFactoryCollection);
                engine.setDetectorFactoryCollection(detectorFactoryCollection);
                engine.setProject(project);
                engine.setUserPreferences(UserPreferences.createDefaultUserPreferences());

                project.addFile(projectPath.toString());
                project.addSourceDirs(Stream.of(projectPath).map(Path::toString).toList());
                project.addAuxClasspathEntry(javaHomeDirectory.resolve("lib/jrt-fs.jar").toString());


                reporter.setPriorityThreshold(Priorities.NORMAL_PRIORITY);
                engine.setBugReporter(reporter);

                try {
                    engine.execute();
                } catch (final IOException | InterruptedException e) {
                    throw new StaticAnalysisAdapterException("Static analysis failed", e);
                }

                return reporter.getBugCollection()
                        .getCollection()
                        .stream()
                        .map(bug -> new StaticAnalysisClassFinding(
                                bug.getPrimaryClass().getClassName(),
                                bug.getPrimaryClass().getPackageName(),
                                bug.getMessageWithoutPrefix(),
                                bug.getType(),
                                "",
                                mapPriority(bug.getPriority()),
                                "Spotbugs"
                        ))
                        .toList();
            }
        }
    }

    /**
     * Maps the priority to the enumerable representation.
     *
     * @param rating The rating.
     * @return The severity.
     */
    private Severity mapPriority(final int rating) {
        if (rating == Priorities.HIGH_PRIORITY) {
            return Severity.HIGH;
        } else if (rating == Priorities.NORMAL_PRIORITY) {
            return Severity.MEDIUM;
        } else {
            return Severity.LOW;
        }
    }
}
