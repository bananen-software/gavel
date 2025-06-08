package software.bananen.gavel.domain.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import software.bananen.gavel.domain.model.PackageComplexityRating;

import static org.assertj.core.api.Assertions.assertThat;

class RatePackageComplexityServiceTest {

    @CsvSource("""
            0,0,0,0
            """)
    @ParameterizedTest
    public void rate_emptyPackage(final Integer lowComplexityCount,
                                  final Integer mediumComplexityCount,
                                  final Integer highComplexityCount,
                                  final Integer veryHighComplexityCount) {
        final var rating = new RatePackageComplexityService().rate(lowComplexityCount,
                mediumComplexityCount,
                highComplexityCount,
                veryHighComplexityCount);

        assertThat(rating).isEqualTo(PackageComplexityRating.EMPTY);
    }

    @CsvSource("""
            4,4,3,0
            """)
    @ParameterizedTest
    public void rate_mostlySimplePackages(final Integer lowComplexityCount,
                                          final Integer mediumComplexityCount,
                                          final Integer highComplexityCount,
                                          final Integer veryHighComplexityCount) {
        final var rating = new RatePackageComplexityService().rate(lowComplexityCount,
                mediumComplexityCount,
                highComplexityCount,
                veryHighComplexityCount);

        assertThat(rating).isEqualTo(PackageComplexityRating.MOSTLY_SIMPLE);
    }

    @CsvSource("""
            9,0,5,5
            """)
    @ParameterizedTest
    public void rate_veryHighlyComplexPackages(final Integer lowComplexityCount,
                                               final Integer mediumComplexityCount,
                                               final Integer highComplexityCount,
                                               final Integer veryHighComplexityCount) {
        final var rating = new RatePackageComplexityService().rate(lowComplexityCount,
                mediumComplexityCount,
                highComplexityCount,
                veryHighComplexityCount);

        assertThat(rating).isEqualTo(PackageComplexityRating.HIGHLY_COMPLEX);
    }

    @CsvSource("""
            10,0,5,5
            """)
    @ParameterizedTest
    public void rate_highlyComplexPackages(final Integer lowComplexityCount,
                                           final Integer mediumComplexityCount,
                                           final Integer highComplexityCount,
                                           final Integer veryHighComplexityCount) {
        final var rating = new RatePackageComplexityService().rate(lowComplexityCount,
                mediumComplexityCount,
                highComplexityCount,
                veryHighComplexityCount);

        assertThat(rating).isEqualTo(PackageComplexityRating.COMPLEX);
    }

    @CsvSource("""
            7,7,3,3
            """)
    @ParameterizedTest
    public void rate_balancedPackages(final Integer lowComplexityCount,
                                      final Integer mediumComplexityCount,
                                      final Integer highComplexityCount,
                                      final Integer veryHighComplexityCount) {
        final var rating = new RatePackageComplexityService().rate(lowComplexityCount,
                mediumComplexityCount,
                highComplexityCount,
                veryHighComplexityCount);

        assertThat(rating).isEqualTo(PackageComplexityRating.BALANCED);
    }
}