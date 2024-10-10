package software.bananen.gavel.backend.services.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.bananen.gavel.backend.entity.ComponentDependencyMetricEntity;
import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.backend.entity.WorkspaceEntity;
import software.bananen.gavel.backend.repository.WorkspaceRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class LoadComponentDependencyMetricsUseCase {

    private final WorkspaceRepository workspaceRepository;

    public LoadComponentDependencyMetricsUseCase(
            @Autowired WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Transactional
    public Optional<Collection<LoadComponentDependencyMetricsResponseModel>> load() {
        //TODO: Support multiple workspaces
        final Optional<WorkspaceEntity> workspace =
                workspaceRepository.findAll().stream().findFirst();

        if (workspace.isPresent()) {
            final Collection<LoadComponentDependencyMetricsResponseModel> result =
                    new ArrayList<>();

            for (final ProjectEntity project : workspace.get().getProjects()) {
                for (final PackageEntity pkg : project.getPackages()) {
                    final Optional<ComponentDependencyMetricEntity> componentDependencyMetricEntity =
                            pkg.getComponentDependencyMetrics()
                                    .stream()
                                    .findFirst();

                    componentDependencyMetricEntity.ifPresent(value ->
                            result.add(new LoadComponentDependencyMetricsResponseModel(
                                    pkg.getPackageName(),
                                    value.getAfferentCoupling(),
                                    value.getEfferentCoupling(),
                                    value.getAbstractness(),
                                    value.getInstability(),
                                    value.getDistance()
                            )));
                }
            }

            return Optional.of(result);
        } else {
            return Optional.empty();
        }
    }
}
