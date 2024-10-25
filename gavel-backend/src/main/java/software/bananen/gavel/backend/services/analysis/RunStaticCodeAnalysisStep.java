package software.bananen.gavel.backend.services.analysis;

import gavel.staticanalysis.adapter.Severity;
import gavel.staticanalysis.adapter.StaticAnalysisAdapterException;
import gavel.staticanalysis.adapter.StaticAnalysisClassFinding;
import gavel.staticanalysis.adapter.StaticCodeAnalysisAdapter;
import software.bananen.gavel.backend.entity.ClassEntity;
import software.bananen.gavel.backend.entity.ClassFindingEntity;
import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.backend.repository.ClassFindingRepository;
import software.bananen.gavel.backend.services.domain.ClassService;
import software.bananen.gavel.backend.services.domain.PackageService;

import java.nio.file.Path;
import java.util.Objects;

public class RunStaticCodeAnalysisStep extends AbstractAnalysisStep {
    private final StaticCodeAnalysisAdapter adapter;
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
                                     final StaticCodeAnalysisAdapter adapter,
                                     final ProjectEntity project,
                                     final PackageService packageService,
                                     final ClassService classService,
                                     final ClassFindingRepository classFindingRepository) {
        super(taskId, "Run static code analysis");
        this.adapter = adapter;
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

                classEntity.setTotalNumberOfFindings(classEntity.getClassFindingEntities().size());
                classEntity.setNumberOfHighPriorityFindings(
                        Math.toIntExact(classEntity.getClassFindingEntities()
                                .stream()
                                .filter(f -> Objects.equals(f.getSeverity(), Severity.HIGH))
                                .count()));

                if (classEntity.getTotalLinesOfCode() > 0) {
                    classEntity.setDefectDensity(
                            calculateDefectDensity(classEntity.getTotalNumberOfFindings(),
                                    classEntity.getTotalLinesOfCode()));
                    classEntity.setHighDefectDensity(
                            calculateDefectDensity(classEntity.getNumberOfHighPriorityFindings(),
                                    classEntity.getTotalLinesOfCode()));
                }

                classService.save(classEntity);

                packageEntity.setTotalNumberOfFindings(
                        packageEntity.getActiveClasses()
                                .stream()
                                .mapToInt(ClassEntity::getTotalNumberOfFindings)
                                .sum());

                packageEntity.setHighDefectDensity(
                        packageEntity.getActiveClasses()
                                .stream()
                                .mapToInt(ClassEntity::getNumberOfHighPriorityFindings)
                                .sum());

                if (packageEntity.getLinesOfCode() > 0) {
                    packageEntity.setDefectDensity(
                            calculateDefectDensity(packageEntity.getTotalNumberOfFindings(),
                                    packageEntity.getLinesOfCode()));
                    packageEntity.setHighDefectDensity(
                            calculateDefectDensity(packageEntity.getNumberOfHighPriorityFindings(),
                                    packageEntity.getLinesOfCode()));
                }

                packageService.save(packageEntity);
            }

        } catch (final StaticAnalysisAdapterException e) {
            throw new RuntimeException(e);
        }
    }

    private static double calculateDefectDensity(final int findings, final int linesOfCode) {
        return (findings * 1000) / (double) linesOfCode;
    }
}
