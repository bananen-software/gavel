package software.bananen.gavel.domain.ports.service;

import software.bananen.gavel.domain.model.ClassComplexityRating;
import software.bananen.gavel.domain.model.Size;

import java.util.Optional;

public interface MeasureClassFileStatisticsService {

    Optional<ClassFileStatistics> measureClassFileStatistics(final String content);

    record ClassFileStatistics(String className,
                               String packageName,
                               Integer complexity,
                               int commentLines,
                               int totalLines,
                               double commentToCodeRatio,
                               Size size,
                               ClassComplexityRating complexityRating) {
    }
}
