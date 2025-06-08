package software.bananen.gavel.domain.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import software.bananen.gavel.domain.model.CVERating;

import static org.assertj.core.api.Assertions.assertThat;

class RateCVEScoreServiceTest {

    @ParameterizedTest
    @CsvSource({
            "0.0,NONE",
            "0.1,LOW",
            "3.9,LOW",
            "4.0,MEDIUM",
            "6.9,MEDIUM",
            "7.0,HIGH",
            "8.9,HIGH",
            "9.0,CRITICAL",
            "10.0,CRITICAL"
    })
    public void rate(double score, CVERating expected) {
        assertThat(new RateCVEScoreService().rate(score)).isEqualTo(expected);
    }
}