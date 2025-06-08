package software.bananen.gavel.domain.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import software.bananen.gavel.domain.model.ClassComplexityRating;

import static org.assertj.core.api.Assertions.assertThat;

class RateClassComplexityServiceTest {


    @Test
    public void rate_emptyPackage() {
        final var rating = new RateClassComplexityService().rate(0);

        assertThat(rating).isEqualTo(ClassComplexityRating.EMPTY);
    }

    @Test
    public void rate_lowComplexityPackages() {
        assertRange(1, 200, ClassComplexityRating.LOW);
    }

    @Test
    public void rate_mediumComplexityPackages() {
        assertRange(201, 500, ClassComplexityRating.MEDIUM);
    }

    @Test
    public void rate_highComplexityPackages() {
        assertRange(501, 1000, ClassComplexityRating.HIGH);
    }

    @Test
    public void rate_veryHighComplexityPackages() {
        assertRange(1001, 1001, ClassComplexityRating.VERY_HIGH);
    }

    /**
     * Performs an assertion for a range of values.
     *
     * @param lower          The lower bound.
     * @param upper          The upper bound.
     * @param expectedRating The expected rating.
     * @throws AssertionError Will be thrown in case the assertions do not hold up.
     */
    private static void assertRange(final int lower,
                                    final int upper,
                                    final ClassComplexityRating expectedRating)
            throws AssertionError {
        final var assertions = new SoftAssertions();

        final var service = new RateClassComplexityService();

        for (int value = lower; value <= upper; value++) {
            final var actualRating = service.rate(value);

            assertions.assertThat(actualRating)
                    .withFailMessage("Expected " + value + " to be a complexity rating of " + expectedRating + " but got " + actualRating)
                    .isEqualTo(expectedRating);
        }

        assertions.assertAll();
    }
}