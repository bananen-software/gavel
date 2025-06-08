package software.bananen.gavel.backend.services.analysis;

import software.bananen.gavel.backend.services.domain.ClassService;
import software.bananen.gavel.backend.services.domain.PackageService;
import software.bananen.gavel.domain.ports.service.Severity;
import software.bananen.gavel.domain.ports.service.StaticAnalysisAdapterException;
import software.bananen.gavel.domain.ports.service.StaticAnalysisClassFinding;
import software.bananen.gavel.domain.ports.service.StaticCodeAnalysisAdapter;
import software.bananen.gavel.infrastructure.persistence.jpa.*;

import java.nio.file.Path;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * An analysis step that runs static code analysis tools.
 */
public class RunStaticCodeAnalysisStep extends AbstractAnalysisStep {
    private final StaticCodeAnalysisAdapter adapter;
    private final ProjectEntity project;
    private final PackageService packageService;
    private final ClassService classService;
    private final ClassFindingRepository classFindingRepository;

    /**
     * Creates a new instance.
     *
     * @param adapter
     * @param project
     * @param packageService
     * @param classService
     * @param classFindingRepository
     */
    public RunStaticCodeAnalysisStep(final StaticCodeAnalysisAdapter adapter,
                                     final ProjectEntity project,
                                     final PackageService packageService,
                                     final ClassService classService,
                                     final ClassFindingRepository classFindingRepository) {
        super("Run static code analysis");

        this.adapter =
                requireNonNull(adapter, "The adapter may not be null");
        this.project =
                requireNonNull(project, "The project may not be null");
        this.packageService =
                requireNonNull(packageService, "The package service may not be null");
        this.classService =
                requireNonNull(classService, "The class service may not be null");
        this.classFindingRepository =
                requireNonNull(classFindingRepository, "The class finding repository may not be null");
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

    /**
     * Calculates the defect density.
     *
     * @param findings    The number of findings.
     * @param linesOfCode The lines of code.
     * @return The defect density.
     */
    private static double calculateDefectDensity(final int findings, final int linesOfCode) {
        return (findings * 1000) / (double) linesOfCode;
    }
}
