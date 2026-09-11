package software.bananen.gavel.backend.services.domain;

import gavel.adapter.persistence.jpa.*;
import org.springframework.stereotype.Service;
import software.bananen.gavel.domain.model.ClassComplexityRating;
import software.bananen.gavel.domain.model.ClassStatus;
import software.bananen.gavel.domain.model.CodeUnitType;
import software.bananen.gavel.domain.model.Commit;
import software.bananen.gavel.domain.model.DiffType;
import software.bananen.gavel.domain.model.FileDiff;
import software.bananen.gavel.domain.model.PackageComplexityRating;
import software.bananen.gavel.domain.ports.driven.ClassCodeUnitMetrics;
import software.bananen.gavel.domain.ports.driven.CodeUnitMetrics;
import software.bananen.gavel.domain.ports.driven.MethodCodeUnitMetrics;
import software.bananen.gavel.domain.ports.driven.SourceFileMetrics;
import software.bananen.gavel.domain.service.RateClassComplexityService;
import software.bananen.gavel.domain.service.RateClassSizeService;
import software.bananen.gavel.domain.service.RateCommentToCodeRatioService;
import software.bananen.gavel.domain.service.RatePackageComplexityService;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * A service that can be used to manage tracked files.
 */
@Service
public class FileService {

    private final JpaProjectFileRepository projectFileRepository;
    private final PackageService packageService;
    private final ClassService classService;
    private final RateClassComplexityService rateClassComplexityService = new RateClassComplexityService();
    private final RateClassSizeService rateClassSizeService = new RateClassSizeService();
    private final RatePackageComplexityService ratePackageComplexityService = new RatePackageComplexityService();
    private final RateCommentToCodeRatioService rateCommentToCodeRatioService = new RateCommentToCodeRatioService();

    /**
     * Creates a new instance.
     *
     * @param projectFileRepository The repository.
     */
    public FileService(final JpaProjectFileRepository projectFileRepository,
                       final PackageService packageService,
                       final ClassService classService) {
        this.projectFileRepository =
                requireNonNull(projectFileRepository, "The repository may not be null");
        this.packageService =
                requireNonNull(packageService, "The package service may not be null");
        this.classService = classService;
    }

    /**
     * Attempts to find a specific file by its project and path.
     *
     * @param projectEntity The project.
     * @param path          The path.
     * @return The file or {@link Optional#empty()}
     */
    public Optional<JpaFileEntity> findByPath(final JpaProjectEntity projectEntity,
                                              final String path) {
        return projectFileRepository.findByProjectAndPath(projectEntity, path);
    }

    /**
     * Finds the file with the given project and path or creates a new instance.
     *
     * @param projectEntity The project.
     * @param path          The path.
     * @return The file.
     */
    public JpaFileEntity findOrCreate(final JpaProjectEntity projectEntity,
                                      final String path) {
        return projectFileRepository.findByProjectAndPath(projectEntity, path)
                .orElseGet(() -> {
                    final var newFile = new JpaFileEntity();
                    newFile.setPath(path);
                    newFile.setProject(projectEntity);
                    newFile.setNumberOfAuthors(0);
                    newFile.setNumberOfChanges(0);
                    newFile.setContentType("");
                    newFile.setStatus(ClassStatus.ACTIVE);

                    return newFile;
                });
    }

    /**
     * Saves the given file.
     *
     * @param file The file that shall be saved.
     * @return The saved file.
     */
    public JpaFileEntity save(final JpaFileEntity file) {
        return projectFileRepository.save(file);
    }

    /**
     * Records the contribution to the file.
     *
     * @param project          The project that the file belongs to.
     * @param authorEntity     The author of the contribution.
     * @param metrics          The metrics that shall be recorded.
     * @param commit           The commit.
     * @param diff             The diff.
     * @param detectedLanguage The detected language for the file.
     */
    public void recordContribution(final JpaProjectEntity project,
                                   final JpaAuthorEntity authorEntity,
                                   final SourceFileMetrics metrics,
                                   final Commit commit,
                                   final FileDiff diff,
                                   final String detectedLanguage) {
        var file = findOrCreate(project, diff.oldPath());

        if (file.getId() == null) {
            file = findOrCreate(project, diff.newPath());
        }

        final var fileHistoryEntry = new JpaFileHistoryEntity();

        fileHistoryEntry.setComplexity(metrics.complexity());
        fileHistoryEntry.setTotalLinesOfCode(metrics.linesOfCode());
        fileHistoryEntry.setAddedLinesOfCode(metrics.linesOfCode() - file.getTotalLinesOfCode());
        fileHistoryEntry.setAddedComplexity(metrics.complexity() - file.getComplexity());
        fileHistoryEntry.setAuthor(authorEntity);
        fileHistoryEntry.setVcsIdentifier(commit.identifier());
        fileHistoryEntry.setTimestamp(commit.timestamp());

        file.setStatus(ClassStatus.ACTIVE);
        file.setPath(diff.newPath());
        file.setContentType(detectedLanguage);
        file.getFileHistoryEntities().add(fileHistoryEntry);
        file.setNumberOfChanges(file.getFileHistoryEntities().size());
        if (file.getCreated() == null) {
            file.setCreated(commit.timestamp());
        }
        file.setComplexity(metrics.complexity());
        file.setTotalLinesOfCode(metrics.linesOfCode());
        file.setLastModified(commit.timestamp());
        file.setNumberOfAuthors(
                file.getFileHistoryEntities()
                        .stream()
                        .map(JpaFileHistoryEntity::getAuthor)
                        .map(JpaAuthorEntity::getId)
                        .collect(Collectors.toSet())
                        .size());
        fileHistoryEntry.setFile(file);

        if (file.getId() == null || diff.hasType(DiffType.MOVED)) {
            // Saving file to prevent duplicates?
            file = save(file);
        }

        for (final CodeUnitMetrics codeUnit : metrics.codeUnits()) {
            if (codeUnit instanceof ClassCodeUnitMetrics classMetrics) {
                final var packageEntity =
                        packageService.findOrCreatePackage(project, classMetrics.packageName());

                final var classEntity =
                        classService.findOrCreateClass(packageEntity, classMetrics.name());

                final Integer complexityBefore = classEntity.getComplexity();
                final Integer linesOfCodeBefore = classEntity.getTotalLinesOfCode();
                final Integer linesOfCommentsBefore = classEntity.getTotalLinesOfComments();

                classEntity.setComplexity(classMetrics.complexity());
                classEntity.setComplexityRating(rateClassComplexityService.rate(classMetrics.complexity()));
                classEntity.setTotalLinesOfCode(classMetrics.linesOfCode());
                classEntity.setTotalLinesOfComments(classMetrics.linesOfComments());
                classEntity.setSize(rateClassSizeService.rate(classMetrics.linesOfCode()));

                classEntity.setFile(file);
                if (classEntity.getCreated() == null) {
                    classEntity.setCreated(commit.timestamp());
                }
                classEntity.setLastModified(commit.timestamp());
                file.getClasses().add(classEntity);
                packageEntity.getClasses().add(classEntity);

                final JpaClassContributionEntity classContributionEntity = new JpaClassContributionEntity();

                classContributionEntity.setVcsIdentifier(commit.identifier());
                classContributionEntity.setTimestamp(commit.timestamp());
                classContributionEntity.setAuthor(authorEntity);
                classContributionEntity.setClassField(classEntity);

                final JpaClassComplexityEntity classComplexityEntity = new JpaClassComplexityEntity();

                classComplexityEntity.setAddedComplexity(classEntity.getComplexity() - complexityBefore);
                classComplexityEntity.setComplexity(classEntity.getComplexity());
                classComplexityEntity.setComplexityRating(classEntity.getComplexityRating());
                classComplexityEntity.setContribution(classContributionEntity);

                final JpaClassLinesOfCodeEntity classLinesOfCodeEntity = new JpaClassLinesOfCodeEntity();

                classLinesOfCodeEntity.setAddedLinesOfCode(classEntity.getTotalLinesOfCode() - linesOfCodeBefore);
                classLinesOfCodeEntity.setAddedLinesOfComment(classEntity.getTotalLinesOfComments() - linesOfCommentsBefore);
                classLinesOfCodeEntity.setCommentToCodeRatio(classEntity.getCommentToCodeRatio());
                classLinesOfCodeEntity.setSize(classEntity.getSize());
                classLinesOfCodeEntity.setTotalLinesOfCode(classEntity.getTotalLinesOfCode());
                classLinesOfCodeEntity.setTotalLinesOfComment(classEntity.getTotalLinesOfComments());
                classLinesOfCodeEntity.setContribution(classContributionEntity);

                classContributionEntity.getClassComplexities().add(classComplexityEntity);
                classContributionEntity.getClassLinesOfCodes().add(classLinesOfCodeEntity);

                classEntity.getClassContributions().add(classContributionEntity);

                classEntity.setNumberOfChanges(classEntity.getClassContributions().size());
                classEntity.setNumberOfAuthors(classEntity.getClassContributions()
                        .stream()
                        .map(c -> c.getAuthor().getId())
                        .collect(Collectors.toSet())
                        .size());

                final var numberOfLines =
                        packageEntity.getClasses()
                                .stream()
                                .map(JpaClassEntity::getTotalLinesOfCode)
                                .reduce(0, Integer::sum);
                final var numberOfComments =
                        packageEntity.getClasses()
                                .stream()
                                .map(JpaClassEntity::getTotalLinesOfComments)
                                .reduce(0, Integer::sum);
                final var complexity = packageEntity.getClasses()
                        .stream()
                        .map(JpaClassEntity::getComplexity)
                        .reduce(0, Integer::sum);

                if (packageEntity.getCreated() == null) {
                    packageEntity.setCreated(commit.timestamp());
                }
                packageEntity.setLastModified(commit.timestamp());
                packageEntity.setLinesOfCode(numberOfLines);
                packageEntity.setLinesOfComments(numberOfComments);
                packageEntity.setCommentToCodeRatio((double) numberOfComments / numberOfLines);
                packageEntity.setCommentToCodeRating(
                        rateCommentToCodeRatioService.rate(packageEntity.getCommentToCodeRatio()));

                packageEntity.setComplexity(complexity);

                final Map<ClassComplexityRating, Integer> complexityCounts = new ConcurrentHashMap<>();

                for (final JpaClassEntity aClass : packageEntity.getClasses()) {
                    final Integer count =
                            complexityCounts.getOrDefault(aClass.getComplexityRating(), 0) + 1;

                    complexityCounts.put(aClass.getComplexityRating(), count);
                }

                packageEntity.setNumberOfVeryHighComplexityTypes(
                        complexityCounts.getOrDefault(ClassComplexityRating.VERY_HIGH, 0));
                packageEntity.setNumberOfHighComplexityTypes(
                        complexityCounts.getOrDefault(ClassComplexityRating.HIGH, 0));
                packageEntity.setNumberOfMediumComplexityTypes(
                        complexityCounts.getOrDefault(ClassComplexityRating.MEDIUM, 0));
                packageEntity.setNumberOfLowComplexityTypes(
                        complexityCounts.getOrDefault(ClassComplexityRating.LOW, 0));

                final PackageComplexityRating packageComplexityRating =
                        ratePackageComplexityService.rate(packageEntity.getNumberOfLowComplexityTypes(),
                                packageEntity.getNumberOfMediumComplexityTypes(),
                                packageEntity.getNumberOfHighComplexityTypes(),
                                packageEntity.getNumberOfVeryHighComplexityTypes());

                packageEntity.setComplexityRating(packageComplexityRating);

                final String className = classMetrics.name();
                final CodeUnitType classType = CodeUnitType.CLASS;
                final LocalDateTime timestamp = commit.timestamp();
                final JpaCodeUnitEntity parent = null;

                final var codeUnitEntity =
                        file.getCodeUnits()
                                .stream()
                                .filter(cu ->
                                        Objects.equals(cu.getName(), classMetrics.name()) &&
                                                Objects.equals(cu.getType(), classType))
                                .findFirst()
                                .orElseGet(buildDefaultCodeUnitEntity(
                                        file, className, classType, parent, timestamp));

                processClassUnit(authorEntity, commit, classMetrics, codeUnitEntity, file);
            }
        }

        projectFileRepository.save(file);
    }

    private static void processClassUnit(
            final JpaAuthorEntity authorEntity,
            final Commit commit,
            final ClassCodeUnitMetrics classMetrics,
            final JpaCodeUnitEntity codeUnitEntity,
            final JpaFileEntity file) {
        final var contribution = new JpaCodeUnitContributionEntity();

        contribution.setCodeUnit(codeUnitEntity);
        contribution.setTimestamp(commit.timestamp());
        contribution.setVcsIdentifier(commit.identifier());
        contribution.setAuthor(authorEntity);
        contribution.setAddedLoc(classMetrics.linesOfCode() - codeUnitEntity.getLoc());
        contribution.setAddedLocComments(classMetrics.linesOfComments() - codeUnitEntity.getLocComments());
        contribution.setAddedComplexity(classMetrics.complexity() - codeUnitEntity.getComplexity());

        codeUnitEntity.setStatus(ClassStatus.ACTIVE);
        codeUnitEntity.setLastModified(commit.timestamp());
        codeUnitEntity.setHash(classMetrics.hash());
        codeUnitEntity.setLoc(classMetrics.linesOfCode());
        codeUnitEntity.setLocComments(classMetrics.linesOfComments());
        codeUnitEntity.setLocRelative(((double) classMetrics.linesOfCode()) / file.getTotalLinesOfCode());
        codeUnitEntity.setCommentToCodeRatio(((double) classMetrics.linesOfComments()) / classMetrics.linesOfCode());
        codeUnitEntity.setComplexity(classMetrics.complexity());
        codeUnitEntity.setRelativeComplexity(((double) classMetrics.complexity()) / file.getComplexity());

        codeUnitEntity.getCodeUnitContributions().add(contribution);
        file.getCodeUnits().add(codeUnitEntity);

        codeUnitEntity.setChangeCount(codeUnitEntity.getCodeUnitContributions().size());
        codeUnitEntity.setAuthorCount(codeUnitEntity.getCodeUnitContributions()
                .stream()
                .map(JpaCodeUnitContributionEntity::getAuthor)
                .map(JpaAuthorEntity::getId)
                .collect(Collectors.toSet())
                .size());

        final var removedClassCodeUnits =
                file.getCodeUnits()
                        .stream()
                        .filter(cu -> !Objects.equals(cu.getLastModified(), file.getLastModified()))
                        .toList();

        for (final JpaCodeUnitEntity removedClassCodeUnit : removedClassCodeUnits) {
            markCodeUnitAsDeleted(commit, removedClassCodeUnit);
        }

        final Set<JpaCodeUnitEntity> activeChildren = new LinkedHashSet<>();

        for (final CodeUnitMetrics child : classMetrics.children()) {
            if (child instanceof ClassCodeUnitMetrics ccum) {
                final var childCodeUnitEntity =
                        codeUnitEntity.getCodeUnits()
                                .stream()
                                .filter(cu ->
                                        Objects.equals(cu.getName(), classMetrics.name()) &&
                                                Objects.equals(cu.getType(), CodeUnitType.CLASS))
                                .findFirst()
                                .orElseGet(buildDefaultCodeUnitEntity(
                                        file, ccum.name(), CodeUnitType.CLASS, codeUnitEntity, commit.timestamp()));

                processClassUnit(authorEntity, commit, ccum, childCodeUnitEntity, file);
                activeChildren.add(childCodeUnitEntity);
            } else if (child instanceof MethodCodeUnitMetrics mcum) {
                final var methodCodeUnitEntity =
                        codeUnitEntity.getCodeUnits()
                                .stream()
                                .filter(cu ->
                                        Objects.equals(cu.getName(), mcum.name()) &&
                                                Objects.equals(cu.getType(), CodeUnitType.METHOD))
                                .findFirst()
                                .orElseGet(buildDefaultCodeUnitEntity(
                                        file, mcum.signature(), CodeUnitType.METHOD, codeUnitEntity, commit.timestamp()));

                processMethodUnit(authorEntity, commit, mcum, methodCodeUnitEntity, file);
                activeChildren.add(methodCodeUnitEntity);
            }
        }

        for (final JpaCodeUnitEntity codeUnit : codeUnitEntity.getCodeUnits()) {
            if (!activeChildren.contains(codeUnit)) {
                markCodeUnitAsDeleted(commit, codeUnit);
            }
        }

        codeUnitEntity.setChildCount((int) activeChildren.stream()
                .filter(c -> Objects.equals(c.getStatus(), ClassStatus.ACTIVE))
                .count());
    }

    private static void markCodeUnitAsDeleted(final Commit commit, final JpaCodeUnitEntity removedClassCodeUnit) {
        removedClassCodeUnit.setStatus(ClassStatus.DELETED);
        removedClassCodeUnit.setLastModified(commit.timestamp());
        removedClassCodeUnit.setComplexity(0);
        removedClassCodeUnit.setCommentToCodeRatio(0.0);
        removedClassCodeUnit.setLoc(0);
        removedClassCodeUnit.setLocRelative(0.0);
        removedClassCodeUnit.setRelativeComplexity(0.0);
    }

    private static Supplier<JpaCodeUnitEntity> buildDefaultCodeUnitEntity(final JpaFileEntity file,
                                                                          final String className,
                                                                          final CodeUnitType classType,
                                                                          final JpaCodeUnitEntity parent,
                                                                          final LocalDateTime timestamp) {
        return () -> {
            final var cu = new JpaCodeUnitEntity();

            cu.setFile(file);
            cu.setName(className);
            cu.setType(classType);
            cu.setParent(parent);
            cu.setCreated(timestamp);
            cu.setChildCount(0);
            cu.setAuthorCount(0);
            cu.setChangeCount(0);
            cu.setDefectCount(0);
            cu.setHighDefectCount(0);
            cu.setLoc(0);
            cu.setLocComments(0);
            cu.setLocRelative(0.0);
            cu.setCommentToCodeRatio(0.0);
            cu.setComplexity(0);
            cu.setRelativeComplexity(0.0);

            return cu;
        };
    }

    /**
     * Deletes the given file.
     *
     * @param file   The file that should be deleted.
     * @param commit The commit.
     */
    public void delete(final JpaFileEntity file,
                       final Commit commit,
                       final JpaAuthorEntity authorEntity) {
        final JpaFileHistoryEntity fileHistoryEntry = new JpaFileHistoryEntity();

        fileHistoryEntry.setComplexity(0);
        fileHistoryEntry.setTotalLinesOfCode(0);
        fileHistoryEntry.setAddedLinesOfCode(file.getTotalLinesOfCode() * -1);
        fileHistoryEntry.setAddedComplexity(file.getComplexity() * -1);
        fileHistoryEntry.setAuthor(authorEntity);
        fileHistoryEntry.setVcsIdentifier(commit.identifier());
        fileHistoryEntry.setTimestamp(commit.timestamp());
        fileHistoryEntry.setFile(file);

        file.setStatus(ClassStatus.DELETED);
        file.setTotalLinesOfCode(0);
        file.setComplexity(0);
        file.setLastModified(commit.timestamp());
        file.getFileHistoryEntities().add(fileHistoryEntry);
        file.setNumberOfChanges(file.getFileHistoryEntities().size());

        projectFileRepository.save(file);
    }

    private static void processMethodUnit(
            final JpaAuthorEntity authorEntity,
            final Commit commit,
            final MethodCodeUnitMetrics mcum,
            final JpaCodeUnitEntity codeUnitEntity,
            final JpaFileEntity file) {

        final var contribution = new JpaCodeUnitContributionEntity();

        contribution.setCodeUnit(codeUnitEntity);
        contribution.setTimestamp(commit.timestamp());
        contribution.setVcsIdentifier(commit.identifier());
        contribution.setAuthor(authorEntity);
        contribution.setAddedLoc(mcum.linesOfCode() - codeUnitEntity.getLoc());
        contribution.setAddedLocComments(mcum.linesOfComments() - codeUnitEntity.getLocComments());
        contribution.setAddedComplexity(mcum.complexity() - codeUnitEntity.getComplexity());

        codeUnitEntity.setStatus(ClassStatus.ACTIVE);
        codeUnitEntity.setLastModified(commit.timestamp());
        codeUnitEntity.setHash(mcum.hash());
        codeUnitEntity.setLoc(mcum.linesOfCode());
        codeUnitEntity.setLocComments(mcum.linesOfComments());
        codeUnitEntity.setLocRelative(((double) mcum.linesOfCode()) / file.getTotalLinesOfCode());
        codeUnitEntity.setCommentToCodeRatio(((double) mcum.linesOfComments()) / mcum.linesOfCode());
        codeUnitEntity.setComplexity(mcum.complexity());
        codeUnitEntity.setRelativeComplexity(((double) mcum.complexity()) / file.getComplexity());

        codeUnitEntity.getCodeUnitContributions().add(contribution);
        file.getCodeUnits().add(codeUnitEntity);

        codeUnitEntity.setChangeCount(codeUnitEntity.getCodeUnitContributions().size());
        codeUnitEntity.setAuthorCount(codeUnitEntity.getCodeUnitContributions()
                .stream()
                .map(JpaCodeUnitContributionEntity::getAuthor)
                .map(JpaAuthorEntity::getId)
                .collect(Collectors.toSet())
                .size());
    }
}
