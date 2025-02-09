package software.bananen.gavel.backend.services.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.bananen.gavel.infrastructure.persistence.jpa.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class LoadRelationalCohesionMetricsUseCase {

    private final WorkspaceRepository workspaceRepository;

    public LoadRelationalCohesionMetricsUseCase(
            @Autowired final WorkspaceRepository repository) {
        this.workspaceRepository = repository;
    }

    @Transactional
    public Optional<Collection<LoadRelationalCohesionMetricsResponseModel>> load() {
        //TODO: Support multiple workspaces
        final Optional<WorkspaceEntity> workspace =
                workspaceRepository.findAll().stream().findFirst();

        if (workspace.isPresent()) {
            final Collection<LoadRelationalCohesionMetricsResponseModel> result =
                    new ArrayList<>();

            for (final ProjectEntity project : workspace.get().getProjects()) {
                for (final PackageEntity pkg : project.getPackages()) {
                    final Optional<RelationalCohesionMetricEntity> entity =
                            pkg.getRelationalCohesionMetrics()
                                    .stream()
                                    .findFirst();

                    entity.ifPresent(value ->
                            result.add(new LoadRelationalCohesionMetricsResponseModel(
                                    pkg.getPackageName(),
                                    value.getRelationalCohesion(),
                                    value.getNumberOfTypes(),
                                    value.getNumberOfInternalRelationships(),
                                    value.getRating().name()
                            )));
                }
            }

            return Optional.of(result);
        } else {
            return Optional.empty();
        }
    }
}
