package software.bananen.gavel.domain.service;

import org.junit.jupiter.api.Test;
import software.bananen.gavel.domain.model.CommentToCodeRating;

import static org.assertj.core.api.Assertions.assertThat;

class RateCommentToCodeRatioServiceTest {

    private static final RateCommentToCodeRatioService SERVICE = new RateCommentToCodeRatioService();

    @Test
    void noComments() {
        final var actual = SERVICE.rate(0);

        assertThat(actual).isEqualTo(CommentToCodeRating.LOW);
    }

    @Test
    void tenPercent() {
        final var actual = SERVICE.rate(0.10);

        assertThat(actual).isEqualTo(CommentToCodeRating.LOW);
    }

    @Test
    void moreThanTenPercent() {
        final var actual = SERVICE.rate(0.11);

        assertThat(actual).isEqualTo(CommentToCodeRating.NORMAL);
    }

    @Test
    void lessThanFiftyPercent() {
        final var actual = SERVICE.rate(0.49);

        assertThat(actual).isEqualTo(CommentToCodeRating.NORMAL);
    }

    @Test
    void moreThanFiftyPercent() {
        var actual = SERVICE.rate(0.51);

        assertThat(actual).isEqualTo(CommentToCodeRating.HIGH);
    }

    @Test
    void oneHundredPercent() {
        var actual = SERVICE.rate(1);

        assertThat(actual).isEqualTo(CommentToCodeRating.HIGH);
    }
}