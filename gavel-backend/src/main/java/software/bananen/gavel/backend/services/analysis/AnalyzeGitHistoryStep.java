package software.bananen.gavel.backend.services.analysis;

import gavel.adapter.persistence.jpa.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bananen.gavel.backend.services.domain.*;
import software.bananen.gavel.behavioralanalysis.git.GitVersionControlSystemAdapter;
import software.bananen.gavel.domain.model.ClassStatus;
import software.bananen.gavel.domain.model.Commit;
import software.bananen.gavel.domain.model.DiffType;
import software.bananen.gavel.domain.model.FileDiff;
import software.bananen.gavel.domain.ports.driven.VersionControlRepository;
import software.bananen.gavel.domain.ports.driven.VersionControlSystemException;
import software.bananen.gavel.infrastructure.javaparser.JavaParserMeasureClassFileStatisticsService;
import gavel.adapter.persistence.jpa.JpaChangeCouplingRepository;
import gavel.adapter.persistence.jpa.JpaProjectRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static software.bananen.gavel.domain.ports.service.MeasureClassFileStatisticsService.MethodStatistics;
import static software.bananen.gavel.infrastructure.javaparser.JavaParserMeasureClassFileStatisticsService.ClassFileStatistics;

public class AnalyzeGitHistoryStep extends AbstractAnalysisStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyzeGitHistoryStep.class);
    private static final JavaParserMeasureClassFileStatisticsService JAVA_PARSER_SERVICE = new JavaParserMeasureClassFileStatisticsService();

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
    private final ProjectFileService projectFileService;
    private final JpaChangeCouplingRepository changeCouplingRepository;
    private final JpaProjectRepository projectRepository;

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
                                 final ProjectFileService projectFileService,
                                 final JpaChangeCouplingRepository changeCouplingRepository,
                                 final JpaProjectRepository projectRepository) {
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
        this.projectFileService = projectFileService;
        this.changeCouplingRepository = changeCouplingRepository;
        this.projectRepository = projectRepository;
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
                int numberOfProcessedCommits = 0;
                String lastProcessedCommit = null;
                LocalDateTime lastProcessedCommitTimestamp = null;

                for (final Commit commit : commits) {
                    lastProcessedCommit = commit.identifier();
                    lastProcessedCommitTimestamp = commit.timestamp();
                    //TODO: Measure author contribution to project
                    final JpaAuthorEntity authorEntity = authorService.findOrCreate(commit.author());

                    final Collection<JpaClassEntity> modifiedClasses = new ArrayList<>();

                    LOGGER.info("Processing commit {} [{}/{}]", commit.identifier(), ++numberOfProcessedCommits, numberOfCommits);
                    for (final FileDiff diff : commit.diffs()) {
                        LOGGER.debug("Processing diff {} file {} => {}", diff.type(), diff.oldPath(), diff.newPath());

                        switch (diff.type()) {
                            case ADDED, CHANGED, MOVED -> {
                                if (isNotExcludedType(diff.newPath())) {
                                    try {
                                        processJavaClass(commit, diff, authorEntity).ifPresent(modifiedClasses::add);
                                    } catch (final IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                } else {
                                    LOGGER.debug("Skipping excluded file type {}", diff.newPath());
                                }
                            }

                            case DELETED -> {
                                if (isNotExcludedType(diff.oldPath())) {
                                    projectFileService.findByPath(project, diff.oldPath())
                                            .map(JpaProjectFileEntity::getClassField)
                                            .ifPresent(classService::delete);
                                }
                            }
                        }
                    }

                    recordChangeCoupling(modifiedClasses);

                    LOGGER.info("Processed commit {} [{}/{}]", commit.identifier(), numberOfProcessedCommits, numberOfCommits);
                }

                if (lastProcessedCommit != null) {
                    project.setLastProcessedCommit(lastProcessedCommit);
                }
                if (lastProcessedCommitTimestamp != null) {
                    project.setLastProcessedCommitTimestamp(lastProcessedCommitTimestamp);
                }
                projectRepository.save(project);

                LOGGER.info("Analyzed {} commits for project {}", numberOfCommits, repository.projectName());
            }
        } catch (final VersionControlSystemException e) {
            throw new RuntimeException(e);
        }
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

    private Optional<JpaClassEntity> processJavaClass(final Commit commit,
                                                      final FileDiff diff,
                                                      final JpaAuthorEntity authorEntity) throws IOException, VersionControlSystemException {
        final String content = new String(diff.loadContent(), StandardCharsets.UTF_8);

        final Optional<ClassFileStatistics> parseResult =
                JAVA_PARSER_SERVICE.measureClassFileStatistics(content);

        if (parseResult.isPresent()) {
            final JpaPackageEntity packageEntity =
                    packageService.findOrCreatePackage(project, parseResult.get().packageName());

            final JpaClassEntity classEntity =
                    classService.findOrCreateClass(packageEntity, parseResult.get().className());

            if (diff.type() == DiffType.MOVED) {
                final JpaProjectFileEntity projectFileEntity =
                        projectFileService.saveOrUpdate(project, diff.newPath());

                projectFileEntity.setClassField(classEntity);

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

    private boolean isNotExcludedType(final String path) {
        return path.endsWith(".java") &&
                !path.endsWith("module-info.java") &&
                !path.endsWith("package-info.java");
    }
}
