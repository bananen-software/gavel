package software.bananen.gavel.metrics.git;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import software.bananen.gavel.metrics.Author;

import static org.assertj.core.api.Assertions.assertThat;

class MailmapTest {

    @Test
    public void parse_comment() {
        final Mailmap mailmap = new Mailmap();

        mailmap.parse("# this is a comment");

        assertThat(mailmap.isEmpty()).isTrue();
    }

    @Test
    public void parse_example() {
        final Mailmap mailmap = new Mailmap();

        mailmap.parse("Correct Name <correct.email@example.com> Incorrect Name <incorrect.email@example.com>");

        final SoftAssertions assertions = new SoftAssertions();

        assertions.assertThat(mailmap.isEmpty()).isFalse();
        assertions.assertThat(mailmap.size()).isEqualTo(1);
        assertions.assertThat(
                        mailmap.map(
                                new Author("Incorrect Name", "incorrect.email@example.com")))
                .isEqualTo(
                        new Author("Correct Name", "correct.email@example.com"));

        assertions.assertAll();
    }
}