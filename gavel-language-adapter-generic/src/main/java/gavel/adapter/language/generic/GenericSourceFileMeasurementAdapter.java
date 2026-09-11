package gavel.adapter.language.generic;

import software.bananen.gavel.domain.model.FileDiff;
import software.bananen.gavel.domain.ports.driven.SourceFileMeasurementPort;
import software.bananen.gavel.domain.ports.driven.SourceFileMetrics;
import software.bananen.gavel.domain.ports.driven.VersionControlSystemException;
import software.bananen.gavel.domain.service.MeasureWhitespaceComplexityService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static java.util.Objects.requireNonNull;

/**
 * A generic language adapter that can be used if no language specific adapters are available.
 */
public final class GenericSourceFileMeasurementAdapter implements SourceFileMeasurementPort {

    private final MeasureWhitespaceComplexityService whitespaceService;

    /**
     * Creates a new instance.
     *
     * @param whitespaceService A service that is used to determine the whitespace complexity for files.
     */
    public GenericSourceFileMeasurementAdapter(final MeasureWhitespaceComplexityService whitespaceService) {
        this.whitespaceService = requireNonNull(whitespaceService, "The whitespace service must not be null.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceFileMetrics generateFor(final FileDiff diff) throws VersionControlSystemException {
        final var content = new String(diff.loadContent(), StandardCharsets.UTF_8);
        final var contentLines = content.split("\n");
        final var complexity = whitespaceService.measure(content);

        return new SourceFileMetrics(
                contentLines.length,
                complexity,
                Collections.emptyList()
        );
    }
}
