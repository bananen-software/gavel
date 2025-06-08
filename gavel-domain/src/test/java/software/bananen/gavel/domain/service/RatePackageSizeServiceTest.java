package software.bananen.gavel.domain.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import software.bananen.gavel.domain.model.Size;

class RatePackageSizeServiceTest {

    @CsvSource({
            "0,0,EMPTY",
            "1,1000,SMALL",
            "1001,10000,MEDIUM",
            "10001,100000,LARGE",
            "100001,100001,VERY_LARGE"
    })
    @ParameterizedTest
    public void ratePackage(final int lowerBound,
                            final int upperBound,
                            final Size expectedSize) {
        final var assertions = new SoftAssertions();
        final var service = new RatePackageSizeService();

        for (int loc = lowerBound; loc <= upperBound; loc++) {
            assertions.assertThat(service.rate(loc))
                    .describedAs("Size of " + loc + " should be " + expectedSize)
                    .isEqualTo(expectedSize);
        }

        assertions.assertAll();
    }
}