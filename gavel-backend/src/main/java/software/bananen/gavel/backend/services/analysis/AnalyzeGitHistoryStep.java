package software.bananen.gavel.backend.services.analysis;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bananen.gavel.backend.services.domain.*;
import software.bananen.gavel.behavioralanalysis.Author;
import software.bananen.gavel.behavioralanalysis.git.GitService;
import software.bananen.gavel.behavioralanalysis.git.GitUtil;
import software.bananen.gavel.behavioralanalysis.git.Mailmap;
import software.bananen.gavel.domain.model.ClassStatus;
import software.bananen.gavel.infrastructure.javaparser.JavaParserMeasureClassFileStatisticsService;
import software.bananen.gavel.infrastructure.persistence.jpa.*;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

import static software.bananen.gavel.behavioralanalysis.git.GitUtil.loadMailmap;

public class AnalyzeGitHistoryStep extends AbstractAnalysisStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyzeGitHistoryStep.class);
    private static final JavaParserMeasureClassFileStatisticsService JAVA_PARSER_SERVICE = new JavaParserMeasureClassFileStatisticsService();

    private static final String STEP_NAME = "Analyze git history";

    private final ProjectEntity project;
    private final AuthorService authorService;
    private final PackageService packageService;
    private final ClassService classService;
    private final ClassContributionService classContributionService;
    private final ClassLinesOfCodeService classLinesOfCodeService;
    private final ClassComplexityService classComplexityService;
    private final PackageComplexityService packageComplexityService;
    private final PackageLinesOfCodeService packageLinesOfCodeService;
    private final ProjectFileService projectFileService;
    private final ChangeCouplingRepository changeCouplingRepository;

    /**
     * Creates a new instance.
     */
    public AnalyzeGitHistoryStep(final ProjectEntity project,
                                 final AuthorService authorService,
                                 final PackageService packageService,
                                 final ClassService classService,
                                 final ClassContributionService classContributionService,
                                 final ClassLinesOfCodeService classLinesOfCodeService,
                                 final ClassComplexityService classComplexityService,
                                 final PackageComplexityService packageComplexityService,
                                 final PackageLinesOfCodeService packageLinesOfCodeService,
                                 final ProjectFileService projectFileService,
                                 final ChangeCouplingRepository changeCouplingRepository) {
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runAnalysis() {
        final GitService gitService = new GitService();

        for (final Path path : gitService.locateGitRepositories(List.of(project.getPath()))) {
            try (final Repository repository = gitService.loadRepository(path)) {
                LOGGER.info("Processing repository {}", path);

                final String projectName = path.getName(path.getNameCount() - 1).toString();

                final Mailmap mailmap = loadMailmap(path);

                LOGGER.info(".mailmap loaded with {} entries", mailmap.size());

                try (final Git git = new Git(repository)) {
                    //TODO: Support incremental analysis
                    final Collection<RevCommit> commits = GitUtil.getCommitsFromOldToNew(git);

                    final int numberOfCommits = commits.size();
                    int numberOfProcessedCommits = 0;

                    LOGGER.info("Analyzing {} commits for project {}", numberOfCommits, projectName);

                    for (final RevCommit commit : commits) {
                        LOGGER.info("Processing commit {} [{}/{}]", commit.getName(), ++numberOfProcessedCommits, numberOfCommits);
                        processCommit(commit, mailmap, repository);
                        LOGGER.info("Processed commit {} [{}/{}]", commit.getName(), numberOfProcessedCommits, numberOfCommits);
                    }

                    LOGGER.info("Analyzed {} commits for project {}", numberOfCommits, projectName);
                }
            } catch (final IOException | GitAPIException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Processes the commit.
     *
     * @param commit     The commit.
     * @param mailmap    The mailmap.
     * @param repository The git repository.
     * @throws IOException Might be thrown in case that data could not be read from the repository.
     */
    private void processCommit(final RevCommit commit,
                               final Mailmap mailmap,
                               final Repository repository) throws IOException {
        final Author author = mailmap.map(GitUtil.extractAuthor(commit));
        final LocalDateTime timestamp = GitUtil.extractTimestampFrom(commit);

        //TODO: Measure author contribution to project
        final AuthorEntity authorEntity = authorService.findOrCreate(author);

        final Collection<ClassEntity> modifiedClasses = new ArrayList<>();

        for (final DiffEntry diff : GitUtil.extractDiffEntries(repository, commit)) {
            LOGGER.debug("Processing diff {} file {} => {}", diff.getChangeType(), diff.getOldPath(), diff.getNewPath());

            switch (diff.getChangeType()) {
                case ADD:
                case MODIFY:
                case RENAME:
                case COPY:
                    if (isNotExcludedType(diff.getNewPath())) {
                        processJavaClass(commit, repository, diff, timestamp, authorEntity)
                                .ifPresent(modifiedClasses::add);
                    } else {
                        LOGGER.debug("Skipping excluded file type {}", diff.getNewPath());
                    }
                    break;

                case DELETE:
                    if (isNotExcludedType(diff.getOldPath())) {
                        projectFileService.findByPath(project, diff.getOldPath())
                                .map(ProjectFileEntity::getClassField)
                                .ifPresent(classService::delete);
                    }
                    break;
            }
        }

        //TODO: Measure aggregate metrics for project
        recordChangeCoupling(modifiedClasses);
    }

    /**
     * Records the change coupling metrics for the modified classes.
     *
     * @param modifiedClasses The modified classes.
     */
    private void recordChangeCoupling(final Collection<ClassEntity> modifiedClasses) {
        /*
         * This method is more complex than it should be due to the fact, that
         * hibernate currently does not support querying tuples.
         */
        final Collection<ChangeCouplingRepository.ClassPair> classPairs = new HashSet<>();

        for (final ClassEntity sourceClass : modifiedClasses) {
            for (final ClassEntity targetClass : modifiedClasses) {
                if (!Objects.equals(sourceClass, targetClass)) {
                    classPairs.add(new ChangeCouplingRepository.ClassPair(sourceClass, targetClass));
                }
            }
        }

        if (!classPairs.isEmpty()) {
            final Collection<ClassEntity> sourceClasses =
                    classPairs.stream()
                            .map(ChangeCouplingRepository.ClassPair::sourceClass)
                            .toList();

            final Collection<ClassEntity> targetClasses =
                    classPairs.stream()
                            .map(ChangeCouplingRepository.ClassPair::targetClass)
                            .toList();

            final Collection<ChangeCouplingEntity> changeCouplingEntities =
                    new HashSet<>(changeCouplingRepository.findBySourceClassesAndTargetClasses(sourceClasses, targetClasses));

            for (final ChangeCouplingRepository.ClassPair classPair : classPairs) {
                if (changeCouplingEntities.stream()
                        .noneMatch(e -> Objects.equals(e.getSourceClass(), classPair.sourceClass()) &&
                                Objects.equals(e.getTargetClass(), classPair.targetClass()))) {
                    final ChangeCouplingEntity entity = new ChangeCouplingEntity();

                    entity.setSourceClass(classPair.sourceClass());
                    entity.setTargetClass(classPair.targetClass());
                    entity.setCoupledChanges(0);

                    changeCouplingEntities.add(entity);
                }
            }

            for (final ChangeCouplingEntity changeCouplingEntity :
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

    private Optional<ClassEntity> processJavaClass(final RevCommit commit,
                                                   final Repository repository,
                                                   final DiffEntry diff,
                                                   final LocalDateTime timestamp,
                                                   final AuthorEntity authorEntity) throws IOException {
        final String content =
                GitUtil.loadFileContentFromDiff(repository, commit, diff);

        final Optional<JavaParserMeasureClassFileStatisticsService.ClassFileStatistics> parseResult =
                JAVA_PARSER_SERVICE.measureClassFileStatistics(content);

        if (parseResult.isPresent()) {
            final PackageEntity packageEntity =
                    packageService.findOrCreatePackage(project, parseResult.get().packageName());

            final ClassEntity classEntity =
                    classService.findOrCreateClass(packageEntity, parseResult.get().className());

            if (diff.getChangeType() == DiffEntry.ChangeType.RENAME) {
                final ProjectFileEntity projectFileEntity =
                        projectFileService.saveOrUpdate(project, diff.getNewPath());

                projectFileEntity.setClassField(classEntity);

                classEntity.setName(parseResult.get().className());
                classEntity.getPackageField().getClasses().remove(classEntity);
                classEntity.setPackageField(packageEntity);
                packageEntity.getClasses().add(classEntity);
            }

            classEntity.setStatus(ClassStatus.ACTIVE);

            final Optional<ClassContributionEntity> latestContribution =
                    classContributionService.findLatestContributionTo(classEntity);

            final ClassContributionEntity classContributionEntity =
                    classContributionService.findOrCreate(classEntity, timestamp, commit.getName(), authorEntity);

            classComplexityService.createOrUpdate(classContributionEntity, latestContribution, parseResult.get().complexity());
            classLinesOfCodeService.createOrUpdate(classContributionEntity, latestContribution, parseResult.get().totalLines(), parseResult.get().commentLines(), parseResult.get().commentToCodeRatio());

            packageLinesOfCodeService.createOrUpdate(packageEntity);
            packageComplexityService.createOrUpdate(packageEntity);

            //TODO: Store AST for RAG?
            //DotPrinter yamlPrinter = new DotPrinter(true);
            //System.out.println(yamlPrinter.output(parseResult.get()));
            //System.out.println("-----------------------------------");

            //TODO: Measure author complexity for package
            //TODO: Measure author contribution to class
            //TODO: Measure author contribution to package
            //TODO: Track issue tracking URL/issue references in comments

            LOGGER.debug("Parsed package {} and class {}", parseResult.get().packageName(), parseResult.get().className());
            return Optional.of(classEntity);
        } else {
            LOGGER.error("Failed to parse class from: {}", diff.getNewPath());
        }

        return Optional.empty();
    }

    private boolean isNotExcludedType(final String path) {
        return path.endsWith(".java") &&
                !path.endsWith("module-info.java") &&
                !path.endsWith("package-info.java");
    }
}
