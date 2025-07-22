package software.bananen.gavel.backend.services.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.bananen.gavel.infrastructure.persistence.jpa.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class LoadCumulativeComponentDependencyMetricsUseCase {

    private final JpaWorkspaceRepository workspaceRepository;

    public LoadCumulativeComponentDependencyMetricsUseCase(
            @Autowired JpaWorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Transactional
    public Optional<Collection<LoadCumulativeComponentDependencyMetricsResponseModel>> load() {
        //TODO: Support multiple workspaces
        final Optional<JpaWorkspaceEntity> workspace =
                workspaceRepository.findAll().stream().findFirst();

        if (workspace.isPresent()) {
            final Collection<LoadCumulativeComponentDependencyMetricsResponseModel> result =
                    new ArrayList<>();

            for (final JpaProjectEntity project : workspace.get().getProjects()) {
                for (final JpaPackageEntity pkg : project.getPackages()) {
                    final Optional<JpaCumulativeComponentDependencyEntity> entity =
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
