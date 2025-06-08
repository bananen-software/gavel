package software.bananen.gavel.backend.services.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.infrastructure.persistence.jpa.ClassEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaProjectRepository;
import software.bananen.gavel.infrastructure.persistence.jpa.PackageEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.ProjectEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class ListCodeHotspotsUseCase {

    private final JpaProjectRepository projectRepository;

    public ListCodeHotspotsUseCase(@Autowired final JpaProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Optional<Collection<CodeHotspotResponseModel>> load() {
        final Collection<CodeHotspotResponseModel> results = new ArrayList<>();

        for (final ProjectEntity projectEntity : projectRepository.findAll()) {
            for (final PackageEntity packageEntity : projectEntity.getPackages()) {
                for (final ClassEntity classEntity : packageEntity.getActiveClasses()) {
                    results.add(new CodeHotspotResponseModel(
                            packageEntity.getPackageName(),
                            classEntity.getName(),
                            classEntity.getNumberOfChanges(),
                            classEntity.getComplexity(),
                            classEntity.getComplexityRating().name(),
                            classEntity.getTotalLinesOfCode(),
                            classEntity.getSize().name(),
                            classEntity.getLastModified(),
                            classEntity.getNumberOfAuthors(),
                            classEntity.getDefectDensity()
                    ));
                }
            }
        }

        return Optional.of(results);
    }
}
