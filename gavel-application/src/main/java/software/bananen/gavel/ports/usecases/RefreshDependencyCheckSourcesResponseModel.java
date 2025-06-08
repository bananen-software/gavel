package software.bananen.gavel.ports.usecases;

public sealed interface RefreshDependencyCheckSourcesResponseModel {

    record Success() implements RefreshDependencyCheckSourcesResponseModel {
    }

    record Failure(
            String errorMessage) implements RefreshDependencyCheckSourcesResponseModel {
    }
}
