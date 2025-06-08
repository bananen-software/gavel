package software.bananen.gavel.backend.services.analysis;

import gavel.staticanalysis.adapter.owaspdependencycheck.OWASPDependencyCheckAdapter;
import gavel.staticanalysis.adapter.pmd.PMDAdapter;
import gavel.staticanalysis.adapter.spotbugs.SpotbugsAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.backend.services.domain.*;
import software.bananen.gavel.contextloader.ProjectContext;
import software.bananen.gavel.infrastructure.persistence.jpa.ChangeCouplingRepository;
import software.bananen.gavel.infrastructure.persistence.jpa.ClassFindingRepository;
import software.bananen.gavel.infrastructure.persistence.jpa.ProjectEntity;
import software.bananen.gavel.staticanalysis.*;

import java.util.Collection;
import java.util.List;

/**
 * A factory that can be used to build analysis tasks.
 */
@Service
public class AnalysisTaskFactory {

    private static final DepthOfInheritanceTreeMetricsService DEPTH_OF_INHERITANCE_SERVICE =
            new DepthOfInheritanceTreeMetricsService();

    private static final ComponentVisibilityMetricsService COMPONENT_VISIBILITY_SERVICE =
            new ComponentVisibilityMetricsService();

    private static final CumulativeComponentDependencyMetricsService CUMULATIVE_COMPONENT_DEPENDENCY_SERVICE =
            new CumulativeComponentDependencyMetricsService();

    private static final RelationalCohesionMetricsService RELATIVE_COHESION_SERVICE =
            new RelationalCohesionMetricsService();

    private static final ComponentDependencyMetricsService COMPONENT_DEPENDENCY_METRICS_SERVICE =
            new ComponentDependencyMetricsService();

    private final ProjectService projectService;
    private final AuthorService authorService;
    private final PackageService packageService;
    private final ClassService classService;
    private final ClassContributionService classContributionService;
    private final ClassCohesionService cohesionService;
    private final PackageVisibilityMetricsService packageVisibilityMetricsService;
    private final PackageComponentDependencyMetricsService packageComponentDependencyMetricsService;
    private final PackageCumulativeComponentDependencyMetricsService packageCumulativeComponentDependencyMetricsService;
    private final PackageRelationalCohesionMetricsService relationalCohesionMetricsService;
    private final ClassLinesOfCodeService classLinesOfCodeService;
    private final ClassComplexityService classComplexityService;
    private final PackageComplexityService packageComplexityService;
    private final PackageLinesOfCodeService packageLinesOfCodeService;
    private final ProjectFileService projectFileService;
    private final PMDAdapter pmdAdapter;
    private final SpotbugsAdapter spotbugsAdapter;
    private final OWASPDependencyCheckAdapter owaspDependencyCheckAdapter;
    private final ClassFindingRepository classFindingRepository;
    private final ChangeCouplingRepository changeCouplingRepository;

    public AnalysisTaskFactory(@Autowired final ProjectService projectService,
                               @Autowired final AuthorService authorService,
                               @Autowired final PackageService packageService,
                               @Autowired final ClassService classService,
                               @Autowired final ClassContributionService classContributionService,
                               @Autowired final ClassCohesionService cohesionService,
                               @Autowired final PackageVisibilityMetricsService packageVisibilityMetricsService,
                               @Autowired final PackageComponentDependencyMetricsService packageComponentDependencyMetricsService,
                               @Autowired final PackageCumulativeComponentDependencyMetricsService packageCumulativeComponentDependencyMetricsService,
                               @Autowired final PackageRelationalCohesionMetricsService relationalCohesionMetricsService,
                               @Autowired final ClassLinesOfCodeService classLinesOfCodeService,
                               @Autowired final ClassComplexityService classComplexityService,
                               @Autowired final PackageComplexityService packageComplexityService,
                               @Autowired final PackageLinesOfCodeService packageLinesOfCodeService,
                               @Autowired final ProjectFileService projectFileService,
                               @Autowired final PMDAdapter pmdAdapter,
                               @Autowired final SpotbugsAdapter spotbugsAdapter,
                               @Autowired final OWASPDependencyCheckAdapter owaspDependencyCheckAdapter,
                               @Autowired final ClassFindingRepository classFindingRepository,
                               @Autowired final ChangeCouplingRepository changeCouplingRepository) {
        this.projectService = projectService;
        this.authorService = authorService;
        this.packageService = packageService;
        this.classService = classService;
        this.classContributionService = classContributionService;
        this.cohesionService = cohesionService;
        this.packageVisibilityMetricsService = packageVisibilityMetricsService;
        this.packageComponentDependencyMetricsService = packageComponentDependencyMetricsService;
        this.packageCumulativeComponentDependencyMetricsService = packageCumulativeComponentDependencyMetricsService;
        this.relationalCohesionMetricsService = relationalCohesionMetricsService;
        this.classLinesOfCodeService = classLinesOfCodeService;
        this.classComplexityService = classComplexityService;
        this.packageComplexityService = packageComplexityService;
        this.packageLinesOfCodeService = packageLinesOfCodeService;
        this.projectFileService = projectFileService;
        this.pmdAdapter = pmdAdapter;
        this.spotbugsAdapter = spotbugsAdapter;
        this.owaspDependencyCheckAdapter = owaspDependencyCheckAdapter;
        this.classFindingRepository = classFindingRepository;
        this.changeCouplingRepository = changeCouplingRepository;
    }

    /**
     * Assembles the steps that are executed to analyze the project.
     *
     * @param projectContext The project context.
     * @return The assembled steps.
     */
    public Collection<AbstractAnalysisStep> assembleSteps(final ProjectContext projectContext,
                                                          final ProjectEntity project) {
        return List.of(
                new AnalyzeGitHistoryStep(
                        project,
                        authorService,
                        packageService,
                        classService,
                        classContributionService,
                        classLinesOfCodeService,
                        classComplexityService,
                        packageComplexityService,
                        packageLinesOfCodeService,
                        projectFileService,
                        changeCouplingRepository
                ),
                new RunStaticCodeAnalysisStep(
                        pmdAdapter,
                        project,
                        packageService,
                        classService,
                        classFindingRepository
                ),
                new RunStaticCodeAnalysisStep(
                        spotbugsAdapter,
                        project,
                        packageService,
                        classService,
                        classFindingRepository
                ),
                new RunDependencyCheckStep(
                        project,
                        projectService,
                        owaspDependencyCheckAdapter
                ),
                new AnalyzeLCOM4MetricStep(
                        new LCOM4MetricsService(),
                        projectContext,
                        project,
                        packageService,
                        classService,
                        cohesionService
                ),
                new AnalyzeComponentVisibilityStep(
                        COMPONENT_VISIBILITY_SERVICE,
                        projectContext,
                        project,
                        packageService,
                        packageVisibilityMetricsService
                ),
                new AnalyzeDepthOfInheritanceTreeStep(
                        DEPTH_OF_INHERITANCE_SERVICE,
                        projectContext,
                        project
                ),
                new AnalyzeCumulativeComponentDependencyStep(
                        CUMULATIVE_COMPONENT_DEPENDENCY_SERVICE,
                        projectContext,
                        project,
                        packageService,
                        packageCumulativeComponentDependencyMetricsService
                ),
                new AnalyzeRelationalCohesionStep(
                        RELATIVE_COHESION_SERVICE,
                        projectContext,
                        project,
                        packageService,
                        relationalCohesionMetricsService
                ),
                new AnalyzeComponentDependenciesStep(
                        COMPONENT_DEPENDENCY_METRICS_SERVICE,
                        projectContext,
                        project,
                        packageService,
                        packageComponentDependencyMetricsService
                )
        );
    }
}
