package software.bananen.gavel.ports.usecases;

import gavel.staticanalysis.adapter.ProjectDependencyCheckAdapter;
import gavel.staticanalysis.adapter.StaticAnalysisAdapterException;

import static java.util.Objects.requireNonNull;

/**
 * A use case that can be used to refresh the sources of the dependency check.
 */
public final class RefreshDependencyCheckSourcesUseCase {

    private final ProjectDependencyCheckAdapter dependencyCheck;

    /**
     * Creates a new instance.
     *
     * @param dependencyCheck The dependency check that should be used.
     */
    public RefreshDependencyCheckSourcesUseCase(final ProjectDependencyCheckAdapter dependencyCheck) {
        this.dependencyCheck =
                requireNonNull(dependencyCheck, "The dependency check adapter may not be null");
    }

    /**
     * Refreshes the sources.
     *
     * @param request The request.
     * @return The response to the refresh request.
     */
    public RefreshDependencyCheckSourcesResponseModel refreshSources(RefreshDependencyCheckSourcesRequest request) {
        if (request == null) {
            return new RefreshDependencyCheckSourcesResponseModel.Failure("The request may not be null");
        }

        try {
            dependencyCheck.updateSources();
            return new RefreshDependencyCheckSourcesResponseModel.Success();
        } catch (final StaticAnalysisAdapterException e) {
            return new RefreshDependencyCheckSourcesResponseModel.Failure(e.getMessage());
        }
    }
}
