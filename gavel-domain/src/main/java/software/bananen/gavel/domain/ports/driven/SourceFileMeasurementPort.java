package software.bananen.gavel.domain.ports.driven;

import software.bananen.gavel.domain.model.FileDiff;

/**
 * A port for services that measure a source files.
 */
public interface SourceFileMeasurementPort {

    /**
     * Generates the file level metrics for the given file diff.
     *
     * @param fileDiff The file diff.
     * @return The metrics for the file.
     * @throws VersionControlSystemException Might be thrown in case that the contents could not be read from the given diff.
     */
    SourceFileMetrics generateFor(FileDiff fileDiff) throws VersionControlSystemException;
}
