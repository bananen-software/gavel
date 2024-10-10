package software.bananen.gavel.backend.services.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.backend.entity.CumulativeComponentDependencyEntity;
import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.backend.entity.WorkspaceEntity;
import software.bananen.gavel.backend.repository.WorkspaceRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class LoadCumulativeComponentDependencyMetricsUseCase {

    private final WorkspaceRepository workspaceRepository;

    public LoadCumulativeComponentDependencyMetricsUseCase(
            @Autowired WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    public Optional<Collection<LoadCumulativeComponentDependencyMetricsResponseModel>> load() {
        //TODO: Support multiple workspaces
        final Optional<WorkspaceEntity> workspace =
                workspaceRepository.findAll().stream().findFirst();

        if (workspace.isPresent()) {
            final Collection<LoadCumulativeComponentDependencyMetricsResponseModel> result =
                    new ArrayList<>();

            for (final ProjectEntity project : workspace.get().getProjects()) {
                for (final PackageEntity pkg : project.getPackages()) {
                    final Optional<CumulativeComponentDependencyEntity> entity =
                            pkg.getCumulativeComponentDependencies()
                                    .stream()
                                    .findFirst();

                    entity.ifPresent(value ->
                            result.add(new LoadCumulativeComponentDependencyMetricsResponseModel(
                                    pkg.getPackageName(),
                                    value.getCumulative(),
                                    value.getAverage(),
                                    value.getRelativeAverage(),
                                    value.getNormalized()
                            )));
                }
            }

            return Optional.of(result);
        } else {
            return Optional.empty();
        }
    }
}
