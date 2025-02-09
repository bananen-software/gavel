package software.bananen.gavel.infrastructure.git;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.behavioralanalysis.git.GitService;
import software.bananen.gavel.domain.model.ProjectNameValueObject;
import software.bananen.gavel.domain.model.ProjectPathValueObject;
import software.bananen.gavel.domain.model.WorkspaceAggregate;
import software.bananen.gavel.domain.ports.service.LocateProjectsInWorkspaceService;

import java.util.Collection;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * An implementation of the {@link LocateProjectsInWorkspaceService} that
 * searches for git repositories to locate projects.
 */
@Service
public final class GitLocateProjectsInWorkspaceService implements LocateProjectsInWorkspaceService {

    private final GitService gitService;

    /**
     * Creates a new instance.
     *
     * @param gitService The git service.
     */
    public GitLocateProjectsInWorkspaceService(@Autowired final GitService gitService) {
        this.gitService = requireNonNull(gitService, "The git service may not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<LocatedProject> locateProjectsInWorkspace(final WorkspaceAggregate workspace) {
        return gitService.locateGitRepositories(List.of(workspace.getAggregateRoot().path().asStringValue()))
                .stream()
                .map(path -> new LocatedProject(
                        ProjectNameValueObject.fromPath(path),
                        new ProjectPathValueObject(path)
                )).toList();
    }
}
