package gavel.staticanalysis.adapter.owaspdependencycheck;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class CVEScoreTest {

    @ParameterizedTest
    @CsvSource({
            "0.0, NONE",
            "0.1, LOW",
            "3.9, LOW",
            "4.0, MEDIUM",
            "6.9, MEDIUM",
            "7.0, HIGH",
            "8.9, HIGH",
            "9.0, CRITICAL",
            "10.0, CRITICAL"
    })
    public void mapScore(String score, String expected) {
        assertThat(CVEScore.map(Double.parseDouble(score)))
                .isEqualTo(CVEScore.valueOf(expected));
    }
}
