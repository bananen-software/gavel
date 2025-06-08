package software.bananen.gavel.domain.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import software.bananen.gavel.domain.model.RelationalCohesionRating;

import static org.assertj.core.api.Assertions.assertThat;

class RateRelationalCohesionServiceTest {

    @CsvSource({
            "0,LOW",
            "1.4,LOW",
            "1.5,GOOD",
            "4.0,GOOD",
            "4.1,HIGH"
    })
    @ParameterizedTest
    public void rate(final double relationalCohesion,
                     final RelationalCohesionRating rating) {
        assertThat(new RateRelationalCohesionService().rate(relationalCohesion))
                .isEqualTo(rating);
    }
}