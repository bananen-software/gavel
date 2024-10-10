package software.bananen.gavel.backend.services.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.backend.entity.VisibilityMetricEntity;
import software.bananen.gavel.backend.entity.WorkspaceEntity;
import software.bananen.gavel.backend.repository.WorkspaceRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class LoadVisibilityMetricsUseCase {

    private final WorkspaceRepository workspaceRepository;

    public LoadVisibilityMetricsUseCase(
            @Autowired final WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Transactional
    public Optional<Collection<LoadVisibilityMetricsResponseModel>> load() {
        //TODO: Support multiple workspaces
        final Optional<WorkspaceEntity> workspace =
                workspaceRepository.findAll().stream().findFirst();

        if (workspace.isPresent()) {
            final Collection<LoadVisibilityMetricsResponseModel> result =
                    new ArrayList<>();

            for (final ProjectEntity project : workspace.get().getProjects()) {
                for (final PackageEntity pkg : project.getPackages()) {
                    final Optional<VisibilityMetricEntity> visibilityMetricsEntity =
                            pkg.getVisibilityMetrics()
                                    .stream()
                                    .findFirst();

                    visibilityMetricsEntity.ifPresent(value ->
                            result.add(new LoadVisibilityMetricsResponseModel(
                                    pkg.getPackageName(),
                                    value.getAverageRelativeVisibility(),
                                    value.getRelativeVisibility(),
                                    value.getGlobalRelativeVisibility()
                            )));
                }
            }

            return Optional.of(result);
        } else {
            return Optional.empty();
        }
    }
}
