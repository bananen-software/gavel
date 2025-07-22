package software.bananen.gavel.backend.services.usecases;

import gavel.adapter.persistence.jpa.JpaPackageEntity;
import gavel.adapter.persistence.jpa.JpaProjectEntity;
import gavel.adapter.persistence.jpa.JpaVisibilityMetricEntity;
import gavel.adapter.persistence.jpa.JpaWorkspaceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaWorkspaceRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class LoadVisibilityMetricsUseCase {

    private final JpaWorkspaceRepository workspaceRepository;

    public LoadVisibilityMetricsUseCase(
            @Autowired final JpaWorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Transactional
    public Optional<Collection<LoadVisibilityMetricsResponseModel>> load() {
        //TODO: Support multiple workspaces
        final Optional<JpaWorkspaceEntity> workspace =
                workspaceRepository.findAll().stream().findFirst();

        if (workspace.isPresent()) {
            final Collection<LoadVisibilityMetricsResponseModel> result =
                    new ArrayList<>();

            for (final JpaProjectEntity project : workspace.get().getProjects()) {
                for (final JpaPackageEntity pkg : project.getPackages()) {
                    final Optional<JpaVisibilityMetricEntity> visibilityMetricsEntity =
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
