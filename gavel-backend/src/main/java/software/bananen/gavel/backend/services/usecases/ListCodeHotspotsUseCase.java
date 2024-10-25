package software.bananen.gavel.backend.services.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.backend.entity.ClassEntity;
import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.backend.repository.ProjectRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class ListCodeHotspotsUseCase {

    private final ProjectRepository projectRepository;

    public ListCodeHotspotsUseCase(@Autowired final ProjectRepository projectRepository) {
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
                            classEntity.getComplexityRating(),
                            classEntity.getTotalLinesOfCode(),
                            classEntity.getSize(),
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
