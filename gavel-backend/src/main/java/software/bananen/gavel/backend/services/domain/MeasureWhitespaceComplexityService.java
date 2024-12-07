package software.bananen.gavel.backend.services.domain;

import org.springframework.stereotype.Service;

/**
 * A service that can be used to measure the whitespace complexity of a given
 * piece of code.
 */
@Service
public class MeasureWhitespaceComplexityService {

    /**
     * Measures the complexity of a line based on the leading whitespaces.
     * <p>
     * This is a technique derived from the books of Adam Tornhill.
     *
     * @param line The line.
     * @return The number of leading whitespaces for the line.
     */
    private int measureWhitespaceComplexity(final String line) {
        int leadingSpaces = 0;

        for (final char c : line.toCharArray()) {
            if (Character.isWhitespace(c)) {
                leadingSpaces++;
            } else {
                break;
            }
        }

        return leadingSpaces;
    }

    /**
     * Measures the whitespace complexity of the given content.
     *
     * @param content The content that should be measured.
     * @return The whitespace complexity.
     */
    public int measure(final String content) {
        return content.lines()
                .map(this::measureWhitespaceComplexity)
                .mapToInt(Integer::intValue)
                .sum();
    }
}
