package software.bananen.gavel.domain.service;

import software.bananen.gavel.domain.model.CommentToCodeRating;

/**
 * This service rates the code to comment ratio. This provides a heuristic that can be used to find classes/packages
 * that are not covered with enough comments or has too many comments. This does not take into account whether the
 * comments are meaningful or actually represent what the classes are doing.
 */
public class RateCommentToCodeRatioService {

    private static final double LOW_CODE_TO_COMMENT_THRESHOLD = 0.10;
    private static final double HIGH_CODE_TO_COMMENT_THRESHOLD = 0.50;
    
    /**
     * Determines the code to comment rating for the given code to comment ratio.
     *
     * @param codeToCommentRatio The code to comment ratio.
     * @return The code to comment rating.
     */
    public CommentToCodeRating rate(double codeToCommentRatio) {
        if (codeToCommentRatio <= LOW_CODE_TO_COMMENT_THRESHOLD) {
            return CommentToCodeRating.LOW;
        } else if (codeToCommentRatio > HIGH_CODE_TO_COMMENT_THRESHOLD) {
            return CommentToCodeRating.HIGH;
        } else {
            return CommentToCodeRating.NORMAL;
        }
    }
}
