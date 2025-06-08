package software.bananen.gavel.domain.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectEntityTest {

    @CsvSource({
            "NOT_RUN,true",
            "PENDING,false",
            "RUNNING,false",
            "COMPLETED,true",
            "FAILED,true"
    })
    @ParameterizedTest
    public void canScheduleAnalysis(final AnalysisStatus status,
                                    final boolean expected) {
        final var project = new ProjectEntity(new ProjectIdValueObject(1L),
                new ProjectNameValueObject("Cool Project"),
                new ProjectPathValueObject(Paths.get("/tmp/cool")),
                status,
                null);

        assertThat(project.canScheduleAnalysis()).isEqualTo(expected);
    }
}