package software.bananen.gavel.domain.service;

/**
 * A service that can be used to measure the comment to code ratio.
 */
public final class MeasureCommentToCodeRatioService {

    /**
     * Measures the code to comment ratio.
     *
     * @param totalLoc   The total lines of code.
     * @param commentLoc The lines of code that are comments.
     * @return The comment to code ratio.
     */
    public double measure(final int totalLoc, final int commentLoc) {
        return totalLoc > 0 ? commentLoc / (double) totalLoc : 0;
    }
}
