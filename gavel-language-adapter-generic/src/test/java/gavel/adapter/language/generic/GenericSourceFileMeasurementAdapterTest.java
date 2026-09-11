package gavel.adapter.language.generic;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import software.bananen.gavel.domain.model.FileDiff;
import software.bananen.gavel.domain.service.MeasureWhitespaceComplexityService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GenericSourceFileMeasurementAdapterTest {

    private static final GenericSourceFileMeasurementAdapter MEASUREMENT_ADAPTER =
            new GenericSourceFileMeasurementAdapter(new MeasureWhitespaceComplexityService());

    @Test
    void measureLibrarySystem() throws Exception {
        final byte[] javaSourceCode = readResourceFileContent("LibrarySystem.java");

        final FileDiff diff = mock(FileDiff.class);

        when(diff.newPath()).thenReturn("LibrarySystem.java");
        when(diff.loadContent()).thenReturn(javaSourceCode);

        final var measurement = MEASUREMENT_ADAPTER.generateFor(diff);

        final SoftAssertions assertions = new SoftAssertions();

        assertions.assertThat(measurement.linesOfCode()).isEqualTo(404);
        assertions.assertThat(measurement.complexity()).isEqualTo(3434);
        assertions.assertThat(measurement.codeUnits()).isEmpty();

        assertions.assertAll();
    }

    private byte[] readResourceFileContent(final String fileName) throws IOException, URISyntaxException {
        return Files.readAllBytes(
                Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).toURI()));
    }
}