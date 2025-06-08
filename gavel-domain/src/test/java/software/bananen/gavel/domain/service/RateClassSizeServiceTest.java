package software.bananen.gavel.domain.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import software.bananen.gavel.domain.model.Size;

class RateClassSizeServiceTest {


    @CsvSource({
            "0,0,EMPTY",
            "1,100,SMALL",
            "101,500,MEDIUM",
            "501,1000,LARGE",
            "1001,1001,VERY_LARGE"
    })
    @ParameterizedTest
    public void rate(final int lowerBound,
                     final int upperBound,
                     final Size expectedSize) {

        final var assertions = new SoftAssertions();
        final var service = new RateClassSizeService();

        for (int loc = lowerBound; loc <= upperBound; loc++) {
            assertions.assertThat(service.rate(loc))
                    .describedAs("Size of " + loc + " should be " + expectedSize)
                    .isEqualTo(expectedSize);
        }

        assertions.assertAll();
    }
}