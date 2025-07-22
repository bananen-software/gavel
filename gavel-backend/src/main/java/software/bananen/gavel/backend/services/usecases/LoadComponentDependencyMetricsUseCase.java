package software.bananen.gavel.backend.services.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.bananen.gavel.infrastructure.persistence.jpa.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class LoadComponentDependencyMetricsUseCase {

    private final JpaWorkspaceRepository workspaceRepository;

    public LoadComponentDependencyMetricsUseCase(
            @Autowired JpaWorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Transactional
    public Optional<Collection<LoadComponentDependencyMetricsResponseModel>> load() {
        //TODO: Support multiple workspaces
        final Optional<JpaWorkspaceEntity> workspace =
                workspaceRepository.findAll().stream().findFirst();

        if (workspace.isPresent()) {
            final Collection<LoadComponentDependencyMetricsResponseModel> result =
                    new ArrayList<>();

            for (final JpaProjectEntity project : workspace.get().getProjects()) {
                for (final JpaPackageEntity pkg : project.getPackages()) {
                    final Optional<JpaComponentDependencyMetricEntity> componentDependencyMetricEntity =
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
