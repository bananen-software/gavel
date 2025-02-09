package software.bananen.gavel.backend.services.usecases;


import java.time.LocalDateTime;

public record CodeHotspotResponseModel(String packageName,
                                       String className,
                                       Integer numberOfChanges,
                                       Integer complexity,
                                       String complexityRating,
                                       Integer totalLinesOfCode,
                                       String size,
                                       LocalDateTime lastModified,
                                       Integer numberOfAuthors,
                                       double defectDensity) {
}
