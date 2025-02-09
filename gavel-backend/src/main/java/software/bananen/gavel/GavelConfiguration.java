package software.bananen.gavel;

import gavel.staticanalysis.adapter.owaspdependencycheck.OWASPDependencyCheckAdapter;
import gavel.staticanalysis.adapter.pmd.PMDAdapter;
import gavel.staticanalysis.adapter.spotbugs.SpotbugsAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import software.bananen.gavel.behavioralanalysis.git.GitService;
import software.bananen.gavel.contextloader.ProjectContextLoader;
import software.bananen.gavel.domain.ports.repositories.ProjectRepository;
import software.bananen.gavel.domain.service.MeasureWhitespaceComplexityService;
import software.bananen.gavel.domain.service.WorkspaceService;
import software.bananen.gavel.infrastructure.git.GitLocateProjectsInWorkspaceService;
import software.bananen.gavel.infrastructure.persistence.WorkspaceRepositoryAdapter;
import software.bananen.gavel.ports.usecases.CreateWorkspaceUseCase;
import software.bananen.gavel.ports.usecases.LocateProjectsInWorkspaceUseCase;
import software.bananen.gavel.ports.usecases.ScheduleProjectAnalysisUseCase;

import java.nio.file.Path;

@EnableScheduling
@Configuration
public class GavelConfiguration {

    @Value("${gavel.spotbugs.java-home}")
    Path javaHome;
    @Value("${gavel.owasp.dependencycheck.data-directory}")
    String dataDirectory;
    @Value("${gavel.owasp.dependencycheck.nvd-api-key}")
    String nvdApiKey;
    @Value("${gavel.owasp.dependencycheck.yarn.enabled}")
    boolean enableYarn;
    @Value("${gavel.owasp.dependencycheck.pnpm.enabled}")
    boolean enablePnpm;

    @Autowired
    private WorkspaceRepositoryAdapter workspaceRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Bean
    public PMDAdapter pmdAdapter() {
        return new PMDAdapter();
    }

    @Bean
    public SpotbugsAdapter spotbugsAdapter() {
        return new SpotbugsAdapter(javaHome);
    }

    @Bean
    public OWASPDependencyCheckAdapter owaspDependencyCheckAdapter() {
        return new OWASPDependencyCheckAdapter(dataDirectory, nvdApiKey, enableYarn, enablePnpm);
    }

    @Bean
    public GitService gitService() {
        return new GitService();
    }

    @Bean
    public ProjectContextLoader projectContextLoader() {
        return new ProjectContextLoader();
    }

    @Bean
    public CreateWorkspaceUseCase createWorkspaceUseCase() {
        return new CreateWorkspaceUseCase(workspaceRepository);
    }

    @Bean
    public WorkspaceService workspaceService() {
        return new WorkspaceService(workspaceRepository);
    }

    @Bean
    public LocateProjectsInWorkspaceUseCase locateProjectsInWorkspaceUseCase() {
        return new LocateProjectsInWorkspaceUseCase(
                workspaceService(),
                new GitLocateProjectsInWorkspaceService(gitService()));
    }

    @Bean
    public MeasureWhitespaceComplexityService measureWhitespaceComplexityService() {
        return new MeasureWhitespaceComplexityService();
    }

    @Bean
    public ScheduleProjectAnalysisUseCase scheduleProjectAnalysisUseCase() {
        return new ScheduleProjectAnalysisUseCase(projectRepository);
    }
}
