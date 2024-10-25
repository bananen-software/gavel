package software.bananen.gavel.backend.services.usecases;

import software.bananen.gavel.backend.domain.ComplexityRating;
import software.bananen.gavel.backend.domain.Size;

import java.time.LocalDateTime;

public record CodeHotspotResponseModel(String packageName,
                                       String className,
                                       Integer numberOfChanges,
                                       Integer complexity,
                                       ComplexityRating complexityRating,
                                       Integer totalLinesOfCode,
                                       Size size,
                                       LocalDateTime lastModified,
                                       Integer numberOfAuthors,
                                       double defectDensity) {
}
