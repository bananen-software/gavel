package software.bananen.gavel.domain.service;

import software.bananen.gavel.domain.model.CommentToCodeRating;

/**
 * This service rates the code to comment ratio. This provides a heuristic that can be used to find classes/packages
 * that are not covered with enough comments or has too many comments. This does not take into account whether the
 * comments are meaningful or actually represent what the classes are doing.
 */
public class RateCommentToCodeRatioService {

    /**
     * Determines the code to comment rating for the given code to comment ratio.
     *
     * @param codeToCommentRatio The code to comment ratio.
     * @return The code to comment rating.
     */
    public CommentToCodeRating rate(double codeToCommentRatio) {
        if (codeToCommentRatio <= 0.10) {
            return CommentToCodeRating.LOW;
        } else if (codeToCommentRatio > 0.50) {
            return CommentToCodeRating.HIGH;
        } else {
            return CommentToCodeRating.NORMAL;
        }
    }
}
