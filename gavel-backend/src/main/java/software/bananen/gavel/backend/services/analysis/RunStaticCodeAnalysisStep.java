package software.bananen.gavel.backend.services.analysis;

import gavel.staticanalysis.adapter.StaticAnalysisAdapterException;
import gavel.staticanalysis.adapter.StaticAnalysisClassFinding;
import gavel.staticanalysis.adapter.pmd.PMDAdapter;
import gavel.staticanalysis.adapter.spotbugs.SpotbugsAdapter;
import software.bananen.gavel.backend.entity.ClassEntity;
import software.bananen.gavel.backend.entity.ClassFindingEntity;
import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.backend.repository.ClassFindingRepository;
import software.bananen.gavel.backend.services.domain.ClassService;
import software.bananen.gavel.backend.services.domain.PackageService;

import java.nio.file.Path;
import java.util.List;

public class RunStaticCodeAnalysisStep extends AbstractAnalysisStep {
    private final PMDAdapter pmdAdapter;
    private final SpotbugsAdapter spotbugsAdapter;
    private final ProjectEntity project;
    private final PackageService packageService;
    private final ClassService classService;
    private final ClassFindingRepository classFindingRepository;

    /**
     * Creates a new instance.
     *
     * @param taskId                 The ID of the task that the step belongs to.
     * @param classFindingRepository
     */
    public RunStaticCodeAnalysisStep(final String taskId,
                                     final PMDAdapter pmdAdapter,
                                     final SpotbugsAdapter spotbugsAdapter,
                                     final ProjectEntity project,
                                     final PackageService packageService,
                                     final ClassService classService,
                                     final ClassFindingRepository classFindingRepository) {
        super(taskId, "Run static code analysis");
        this.pmdAdapter = pmdAdapter;
        this.spotbugsAdapter = spotbugsAdapter;
        this.project = project;
        this.packageService = packageService;
        this.classService = classService;
        this.classFindingRepository = classFindingRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runAnalysis() {
        List.of(pmdAdapter, spotbugsAdapter).forEach(adapter -> {
            try {
                for (final StaticAnalysisClassFinding finding : adapter.analyze(Path.of(project.getPath()))) {
                    final PackageEntity packageEntity =
                            packageService.findOrCreatePackage(project, finding.packageName());
                    final ClassEntity classEntity =
                            classService.findOrCreateClass(packageEntity, finding.className());

                    final ClassFindingEntity entity = new ClassFindingEntity();

                    entity.setClassField(classEntity);
                    entity.setDescription(finding.description());
                    entity.setSeverity(finding.severity());
                    entity.setTool(finding.tool());
                    entity.setRuleName(finding.ruleName());
                    entity.setRuleDescription(finding.ruleDescription());

                    classEntity.getClassFindingEntities().add(entity);
                    classFindingRepository.save(entity);
                }
            } catch (final StaticAnalysisAdapterException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
