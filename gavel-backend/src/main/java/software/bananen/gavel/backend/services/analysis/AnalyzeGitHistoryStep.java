package software.bananen.gavel.backend.services.analysis;

import gavel.adapter.language.generic.GenericSourceFileMeasurementAdapter;
import gavel.adapter.language.java.JavaSourceFileMeasurementAdapter;
import gavel.adapter.persistence.jpa.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bananen.gavel.backend.services.domain.AuthorService;
import software.bananen.gavel.backend.services.domain.ClassComplexityService;
import software.bananen.gavel.backend.services.domain.ClassContributionService;
import software.bananen.gavel.backend.services.domain.ClassLinesOfCodeService;
import software.bananen.gavel.backend.services.domain.ClassService;
import software.bananen.gavel.backend.services.domain.FileService;
import software.bananen.gavel.backend.services.domain.PackageComplexityService;
import software.bananen.gavel.backend.services.domain.PackageLinesOfCodeService;
import software.bananen.gavel.backend.services.domain.PackageService;
import software.bananen.gavel.behavioralanalysis.git.GitVersionControlSystemAdapter;
import software.bananen.gavel.domain.model.ClassStatus;
import software.bananen.gavel.domain.model.Commit;
import software.bananen.gavel.domain.model.DiffType;
import software.bananen.gavel.domain.model.FileDiff;
import software.bananen.gavel.domain.model.Size;
import software.bananen.gavel.domain.model.Stratum;
import software.bananen.gavel.domain.ports.driven.VersionControlRepository;
import software.bananen.gavel.domain.ports.driven.VersionControlSystemException;
import software.bananen.gavel.domain.service.MeasureWhitespaceComplexityService;
import software.bananen.gavel.infrastructure.javaparser.JavaParserMeasureClassFileStatisticsService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static software.bananen.gavel.domain.ports.service.MeasureClassFileStatisticsService.MethodStatistics;
import static software.bananen.gavel.infrastructure.javaparser.JavaParserMeasureClassFileStatisticsService.ClassFileStatistics;

public class AnalyzeGitHistoryStep extends AbstractAnalysisStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyzeGitHistoryStep.class);
    private static final JavaParserMeasureClassFileStatisticsService JAVA_PARSER_SERVICE = new JavaParserMeasureClassFileStatisticsService();

    private static final GenericSourceFileMeasurementAdapter GENERIC_MEASUREMENT = new GenericSourceFileMeasurementAdapter(new MeasureWhitespaceComplexityService());
    private static final JavaSourceFileMeasurementAdapter JAVA_MEASUREMENT = new JavaSourceFileMeasurementAdapter(new MeasureWhitespaceComplexityService());

    private static final String STEP_NAME = "Analyze git history";

    private final JpaProjectEntity project;
    private final AuthorService authorService;
    private final PackageService packageService;
    private final ClassService classService;
    private final ClassContributionService classContributionService;
    private final ClassLinesOfCodeService classLinesOfCodeService;
    private final ClassComplexityService classComplexityService;
    private final PackageComplexityService packageComplexityService;
    private final PackageLinesOfCodeService packageLinesOfCodeService;
    private final FileService projectFileService;
    private final JpaChangeCouplingRepository changeCouplingRepository;
    private final JpaProjectRepository projectRepository;
    private final JpaClassRepository classRepository;

    /**
     * Creates a new instance.
     */
    public AnalyzeGitHistoryStep(final JpaProjectEntity project,
                                 final AuthorService authorService,
                                 final PackageService packageService,
                                 final ClassService classService,
                                 final ClassContributionService classContributionService,
                                 final ClassLinesOfCodeService classLinesOfCodeService,
                                 final ClassComplexityService classComplexityService,
                                 final PackageComplexityService packageComplexityService,
                                 final PackageLinesOfCodeService packageLinesOfCodeService,
                                 final FileService fileService,
                                 final JpaChangeCouplingRepository changeCouplingRepository,
                                 final JpaProjectRepository projectRepository,
                                 final JpaClassRepository classRepository) {
        super(STEP_NAME);
        this.project = project;
        this.authorService = authorService;
        this.packageService = packageService;
        this.classService = classService;
        this.classContributionService = classContributionService;
        this.classLinesOfCodeService = classLinesOfCodeService;
        this.classComplexityService = classComplexityService;
        this.packageComplexityService = packageComplexityService;
        this.packageLinesOfCodeService = packageLinesOfCodeService;
        this.projectFileService = fileService;
        this.changeCouplingRepository = changeCouplingRepository;
        this.projectRepository = projectRepository;
        this.classRepository = classRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runAnalysis() {
        final var vcs = new GitVersionControlSystemAdapter();

        try {
            for (final VersionControlRepository repository : vcs.findRepositoriesIn(Paths.get(project.getPath()))) {
                LOGGER.info("Analyzing {} commits for project {}", repository.commits().size(), repository.projectName());

                final var commits = repository.commitsAfter(project.getLastProcessedCommit());

                final int numberOfCommits = commits.size();
                int numberOfProcessedCommits = 1;
                String lastProcessedCommit = null;
                LocalDateTime lastProcessedCommitTimestamp = null;

                for (final Commit commit : commits) {
                    lastProcessedCommit = commit.identifier();
                    lastProcessedCommitTimestamp = commit.timestamp();
                    //TODO: Measure author contribution to project
                    LOGGER.info("Processing commit {} [{}/{}]", commit.identifier(), numberOfProcessedCommits, numberOfCommits);
                    processCommit(commit);
                    LOGGER.info("Processed commit {} [{}/{}]", commit.identifier(), numberOfProcessedCommits, numberOfCommits);
                    numberOfProcessedCommits++;
                }

                if (lastProcessedCommit != null) {
                    project.setLastProcessedCommit(lastProcessedCommit);
                }

                if (lastProcessedCommitTimestamp != null) {
                    project.setLastProcessedCommitTimestamp(lastProcessedCommitTimestamp);
                }

                final var numberOfRecentChangesBoundary =
                        project.getLastProcessedCommitTimestamp()
                                .minusDays(90)
                                .withHour(0)
                                .withMinute(0)
                                .withSecond(0);

                final Collection<JpaClassEntity> updatedClasses = new ArrayList<>();

                for (final JpaPackageEntity aPackage : project.getPackages()) {
                    if (Objects.equals(aPackage.getSize(), Size.EMPTY)) {
                        aPackage.setStratum(Stratum.NONE);
                    } else {
                        final var totalNumberOfChangesToPackage =
                                aPackage.getActiveClasses()
                                        .stream()
                                        .collect(Collectors.summarizingInt(JpaClassEntity::getNumberOfChanges))
                                        .getSum();
                        final var daysSinceLastChangeToPackage =
                                aPackage.getLastModified().until(project.getLastProcessedCommitTimestamp(), ChronoUnit.DAYS);
                        final var packageAgeDays = aPackage.getCreated().until(aPackage.getLastModified(), ChronoUnit.DAYS);
                        final var authors = new LinkedHashSet<>();
                        final var recentChanges = new LinkedHashSet<>();
                        for (final JpaClassEntity activeClass : aPackage.getActiveClasses()) {
                            for (final JpaClassContributionEntity classContribution : activeClass.getClassContributions()) {
                                authors.add(classContribution.getAuthor().getId());
                                if (classContribution.getTimestamp().isAfter(numberOfRecentChangesBoundary)) {
                                    recentChanges.add(classContribution.getVcsIdentifier());
                                }
                            }
                        }
                        final var authorCountForPackage = authors.size();
                        final var numberOfRecentChangesToPackage = recentChanges.size();

                        aPackage.setNumberOfAuthors(authorCountForPackage);
                        aPackage.setNumberOfChanges((int) totalNumberOfChangesToPackage);

                        final var packageStabilityScore = ((double) daysSinceLastChangeToPackage / packageAgeDays) * 100.0;
                        final var activityDensityForPackage = ((double) totalNumberOfChangesToPackage) / (packageStabilityScore / 365.0);

                        final var authorDiversityForPackage = Math.min(authorCountForPackage / 5.0, 1.0);
                        final var recencyFactorForPackage = Math.max(0, (365.0 - daysSinceLastChangeToPackage / 365.0));

                        if ((packageStabilityScore < 30 && numberOfRecentChangesToPackage > 3) ||
                                (recencyFactorForPackage > 0.7 && activityDensityForPackage > 1.0) ||
                                daysSinceLastChangeToPackage < 60) {
                            aPackage.setStratum(Stratum.SURFACE);
                        } else if (30 <= packageStabilityScore &&
                                packageStabilityScore <= 70 &&
                                0.3 <= activityDensityForPackage &&
                                activityDensityForPackage <= 2.0 &&
                                (numberOfRecentChangesToPackage > 0 || daysSinceLastChangeToPackage < 365)) {
                            aPackage.setStratum(Stratum.INTERMEDIATE);
                        } else if (packageStabilityScore > 80 && activityDensityForPackage < 0.2 && authorDiversityForPackage > 0.6) {
                            aPackage.setStratum(Stratum.SEDIMENT);
                        } else {
                            aPackage.setStratum(Stratum.DEEP);
                        }

                        for (final JpaClassEntity activeClass : aPackage.getActiveClasses()) {
                            final var totalNumberOfChanges = activeClass.getNumberOfChanges();
                            final var daysSinceLastChange =
                                    activeClass.getLastModified()
                                            .until(project.getLastProcessedCommitTimestamp(), ChronoUnit.DAYS);
                            final var fileAgeDays = activeClass.getCreated()
                                    .until(activeClass.getLastModified(), ChronoUnit.DAYS);
                            final var authorCount = activeClass.getNumberOfAuthors();
                            final var numberOfRecentChanges =
                                    activeClass.getClassContributions()
                                            .stream()
                                            .filter(c -> c.getTimestamp().isAfter(numberOfRecentChangesBoundary))
                                            .count();

                            final var stabilityScore = ((double) daysSinceLastChange / fileAgeDays) * 100.0;
                            final var activityDensity = totalNumberOfChanges.doubleValue() / (fileAgeDays / 365.0);

                            final var authorDiversity = Math.min(authorCount / 5.0, 1.0);
                            final var recencyFactor = Math.max(0, (365.0 - daysSinceLastChange / 365.0));

                            if ((stabilityScore < 30 && numberOfRecentChanges > 3) ||
                                    (recencyFactor > 0.7 && activityDensity > 1.0) ||
                                    daysSinceLastChange < 60) {
                                activeClass.setStratum(Stratum.SURFACE);
                            } else if (30 <= stabilityScore &&
                                    stabilityScore <= 70 &&
                                    0.3 <= activityDensity &&
                                    activityDensity <= 2.0 &&
                                    (numberOfRecentChanges > 0 || daysSinceLastChange < 365)) {
                                activeClass.setStratum(Stratum.INTERMEDIATE);
                            } else if (stabilityScore > 80 && activityDensity < 0.2 && authorDiversity > 0.6) {
                                activeClass.setStratum(Stratum.SEDIMENT);
                            } else {
                                activeClass.setStratum(Stratum.DEEP);
                            }

                            updatedClasses.add(activeClass);
                        }
                    }
                }

                classRepository.saveAll(updatedClasses);
                projectRepository.save(project);

                LOGGER.info("Analyzed {} commits for project {}", numberOfCommits, repository.projectName());
            }
        } catch (final VersionControlSystemException e) {
            throw new RuntimeException(e);
        }
    }

    private void processCommit(final Commit commit) throws VersionControlSystemException {
        final JpaAuthorEntity authorEntity = authorService.findOrCreate(commit.author());

        //TODO: Add change coupling on a file level
        final Collection<JpaClassEntity> modifiedClasses = new ArrayList<>();

        for (final FileDiff diff : commit.diffs()) {
            LOGGER.debug("Processing diff {} file {} => {}", diff.type(), diff.oldPath(), diff.newPath());
            final var detectedLanguage = new SimpleLanguageDetector().detectLanguage(diff.oldPath());

            if (detectedLanguage.isPresent() && fileIsNotBlacklisted(diff.oldPath())) {
                switch (diff.type()) {
                    case ADDED, CHANGED, MOVED -> {
                        final var languageAdapter =
                                Objects.equals(detectedLanguage.get(), "Java")
                                        ? JAVA_MEASUREMENT
                                        : GENERIC_MEASUREMENT;

                        final var metrics = languageAdapter.generateFor(diff);

                        projectFileService.recordContribution(project, authorEntity, metrics, commit, diff, detectedLanguage.get());
                    }

                    case DELETED -> {
                        //TODO: Add a contribution to the file and class that removes the complexity and LOC
                        projectFileService.findByPath(project, diff.oldPath())
                                .ifPresent(file -> projectFileService.delete(file, commit, authorEntity));

                        projectFileService.findByPath(project, diff.oldPath())
                                .map(JpaFileEntity::getClasses)
                                .ifPresent(classService::deleteAll);
                    }
                }
            } else {
                LOGGER.debug("Skipping file {}, due to irrelevant file ending", diff.newPath());
            }
        }

        recordChangeCoupling(modifiedClasses);
    }

    /**
     * Checks whether the given file is not blacklisted.
     *
     * @param path The path that should be checked.
     * @return True if the file is not blacklisted, otherwise false.
     */
    private boolean fileIsNotBlacklisted(final String path) {
        return Stream.of("pnpm-lock.yaml",
                        "module-info.java",
                        "package-info.java",
                        "package.json",
                        "package-lock.json",
                        "pom.xml")
                .noneMatch(path::endsWith);

    }

    /**
     * Records the change coupling metrics for the modified classes.
     *
     * @param modifiedClasses The modified classes.
     */
    private void recordChangeCoupling(final Collection<JpaClassEntity> modifiedClasses) {
        /*
         * This method is more complex than it should be due to the fact, that
         * hibernate currently does not support querying tuples.
         */
        final Collection<JpaChangeCouplingRepository.ClassPair> classPairs = new HashSet<>();

        for (final JpaClassEntity sourceClass : modifiedClasses) {
            for (final JpaClassEntity targetClass : modifiedClasses) {
                if (!Objects.equals(sourceClass, targetClass)) {
                    classPairs.add(new JpaChangeCouplingRepository.ClassPair(sourceClass, targetClass));
                }
            }
        }

        if (!classPairs.isEmpty()) {
            final Collection<JpaClassEntity> sourceClasses =
                    classPairs.stream()
                            .map(JpaChangeCouplingRepository.ClassPair::sourceClass)
                            .toList();

            final Collection<JpaClassEntity> targetClasses =
                    classPairs.stream()
                            .map(JpaChangeCouplingRepository.ClassPair::targetClass)
                            .toList();

            final Collection<JpaChangeCouplingEntity> changeCouplingEntities =
                    new HashSet<>(changeCouplingRepository.findBySourceClassesAndTargetClasses(sourceClasses, targetClasses));

            for (final JpaChangeCouplingRepository.ClassPair classPair : classPairs) {
                if (changeCouplingEntities.stream()
                        .noneMatch(e -> Objects.equals(e.getSourceClass(), classPair.sourceClass()) &&
                                Objects.equals(e.getTargetClass(), classPair.targetClass()))) {
                    final JpaChangeCouplingEntity entity = new JpaChangeCouplingEntity();

                    entity.setSourceClass(classPair.sourceClass());
                    entity.setTargetClass(classPair.targetClass());
                    entity.setCoupledChanges(0);

                    changeCouplingEntities.add(entity);
                }
            }

            for (final JpaChangeCouplingEntity changeCouplingEntity :
                    changeCouplingEntities.stream()
                            .filter(e ->
                                    classPairs.stream().anyMatch(p -> Objects.equals(e.getSourceClass(), p.sourceClass()) &&
                                            Objects.equals(e.getTargetClass(), p.targetClass())))
                            .toList()) {
                changeCouplingEntity.setTotalChanges(changeCouplingEntity.getSourceClass().getNumberOfChanges());
                changeCouplingEntity.setCoupledChanges(changeCouplingEntity.getCoupledChanges() + 1);
                changeCouplingEntity.setChangeCoupling(
                        changeCouplingEntity.getCoupledChanges() / (double) changeCouplingEntity.getTotalChanges());
            }

            changeCouplingRepository.saveAll(changeCouplingEntities);
        }
    }

    private Optional<JpaClassEntity> processJavaClass(final JpaFileEntity file,
                                                      final Commit commit,
                                                      final FileDiff diff,
                                                      final JpaAuthorEntity authorEntity) throws IOException, VersionControlSystemException {
        final String content = new String(diff.loadContent(), StandardCharsets.UTF_8);

        final Optional<ClassFileStatistics> parseResult =
                JAVA_PARSER_SERVICE.measureClassFileStatistics(content);

        if (parseResult.isPresent()) {
            final JpaPackageEntity packageEntity =
                    packageService.findOrCreatePackage(project, parseResult.get().packageName());

            if (packageEntity.getCreated().isAfter(commit.timestamp())) {
                packageEntity.setCreated(commit.timestamp());
            }

            packageEntity.setLastModified(commit.timestamp());

            final JpaClassEntity classEntity =
                    classService.findOrCreateClass(packageEntity, parseResult.get().className());

            if (diff.type() == DiffType.MOVED) {
                file.getClasses().add(classEntity);

                classEntity.setName(parseResult.get().className());
                classEntity.getPackageField().getClasses().remove(classEntity);
                classEntity.setPackageField(packageEntity);
                packageEntity.getClasses().add(classEntity);
            }

            final Collection<String> currentMethodSignatures =
                    parseResult.get().methods().stream().map(MethodStatistics::signature).collect(Collectors.toSet());

            for (final MethodStatistics method : parseResult.get().methods()) {
                final JpaMethodEntity methodEntity =
                        classEntity.getMethods()
                                .stream()
                                .filter(m -> Objects.equals(m.getSignature(), method.signature()))
                                .findFirst()
                                .orElse(new JpaMethodEntity());

                if (!Objects.equals(methodEntity.getMd5Hash(), method.md5Hash())) {
                    if (methodEntity.getId() == null) {
                        methodEntity.setCreated(commit.timestamp());
                        methodEntity.setSignature(method.signature());
                        methodEntity.setName(method.methodName());
                        classEntity.getMethods().add(methodEntity);
                    }

                    methodEntity.setLastModified(commit.timestamp());
                    methodEntity.setMd5Hash(method.md5Hash());
                    methodEntity.setLinesOfCode(method.linesOfCode());
                    methodEntity.setComplexity(method.complexity());
                    methodEntity.setStatus(ClassStatus.ACTIVE);
                    methodEntity.setClassField(classEntity);

                    final JpaMethodContributionEntity contribution = new JpaMethodContributionEntity();

                    contribution.setTimestamp(commit.timestamp());
                    contribution.setVcsIdentifier(commit.identifier());
                    contribution.setAuthorEntity(authorEntity);
                    contribution.setMethod(methodEntity);

                    methodEntity.getContributions().add(contribution);
                    methodEntity.setNumberOfChanges(methodEntity.getContributions().size());
                    methodEntity.setNumberOfAuthors(methodEntity.getContributions()
                            .stream()
                            .map(JpaMethodContributionEntity::getAuthorEntity)
                            .map(JpaAuthorEntity::getId)
                            .collect(Collectors.toSet()).size());
                }
            }

            classEntity.getMethods()
                    .stream()
                    .filter(m -> !currentMethodSignatures.contains(m.getSignature()))
                    .forEach(m -> {
                        m.setStatus(ClassStatus.DELETED);
                        m.setLastModified(commit.timestamp());
                    });

            classEntity.setStatus(ClassStatus.ACTIVE);

            final Optional<JpaClassContributionEntity> latestContribution =
                    classContributionService.findLatestContributionTo(classEntity);

            final JpaClassContributionEntity classContributionEntity =
                    classContributionService.findOrCreate(classEntity, commit.timestamp(), commit.identifier(), authorEntity);

            classComplexityService.createOrUpdate(classContributionEntity, latestContribution, parseResult.get().complexity());
            classLinesOfCodeService.createOrUpdate(classContributionEntity, latestContribution, parseResult.get().totalLines(), parseResult.get().commentLines(), parseResult.get().commentToCodeRatio());

            packageLinesOfCodeService.createOrUpdate(packageEntity);
            packageComplexityService.createOrUpdate(packageEntity);

            LOGGER.debug("Parsed package {} and class {}", parseResult.get().packageName(), parseResult.get().className());
            return Optional.of(classEntity);
        } else {
            LOGGER.error("Failed to parse class from: {}", diff.newPath());
        }

        return Optional.empty();
    }
}
