package software.bananen.gavel.backend.services.analysis;

import com.github.javaparser.ast.CompilationUnit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bananen.gavel.backend.domain.ClassStatus;
import software.bananen.gavel.backend.entity.*;
import software.bananen.gavel.backend.services.domain.*;
import software.bananen.gavel.backend.services.technical.JavaParserService;
import software.bananen.gavel.behavioralanalysis.Author;
import software.bananen.gavel.behavioralanalysis.git.GitService;
import software.bananen.gavel.behavioralanalysis.git.GitUtil;
import software.bananen.gavel.behavioralanalysis.git.Mailmap;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static software.bananen.gavel.behavioralanalysis.git.GitUtil.loadMailmap;

public class AnalyzeGitHistoryStep extends AbstractAnalysisStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyzeGitHistoryStep.class);
    private static final JavaParserService JAVA_PARSER_SERVICE = new JavaParserService();

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

    /**
     * Creates a new instance.
     *
     * @param taskId The ID of the task that the step belongs to.
     */
    public AnalyzeGitHistoryStep(final String taskId,
                                 final ProjectEntity project,
                                 final AuthorService authorService,
                                 final PackageService packageService,
                                 final ClassService classService,
                                 final ClassContributionService classContributionService,
                                 final ClassLinesOfCodeService classLinesOfCodeService,
                                 final ClassComplexityService classComplexityService,
                                 final PackageComplexityService packageComplexityService,
                                 final PackageLinesOfCodeService packageLinesOfCodeService,
                                 final ProjectFileService projectFileService) {
        super(taskId, STEP_NAME);
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

                final Git git = new Git(repository);

                //TODO: Support incremental analysis
                final Collection<RevCommit> commits = GitUtil.getCommitsFromOldToNew(git);

                LOGGER.info("Analyzing {} commits for project {}", commits.size(), projectName);

                for (final RevCommit commit : commits) {
                    processCommit(commit, mailmap, repository);
                }

                LOGGER.info("Analyzed {} commits for project {}", commits.size(), projectName);
            } catch (final IOException | GitAPIException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void processCommit(final RevCommit commit,
                               final Mailmap mailmap,
                               final Repository repository) throws IOException {
        final Author author = mailmap.map(GitUtil.extractAuthor(commit));
        final LocalDateTime timestamp = GitUtil.extractTimestampFrom(commit);

        //TODO: Measure author contribution to project
        final AuthorEntity authorEntity = authorService.findOrCreate(author);

        LOGGER.info("{} processing commit", commit.getName());

        for (final DiffEntry diff : GitUtil.extractDiffEntries(repository, commit)) {
            LOGGER.debug("Processing diff {} file {} => {}", diff.getChangeType(), diff.getOldPath(), diff.getNewPath());

            switch (diff.getChangeType()) {
                case ADD:
                case MODIFY:
                case RENAME:
                case COPY:
                    if (isNotExcludedType(diff.getNewPath())) {
                        processJavaClass(commit, repository, diff, timestamp, authorEntity);
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

        LOGGER.info("{} processed commit", commit.getName());

        //TODO: Measure aggregate metrics for project
    }

    private void processJavaClass(final RevCommit commit,
                                  final Repository repository,
                                  final DiffEntry diff,
                                  final LocalDateTime timestamp,
                                  final AuthorEntity authorEntity) throws IOException {
        final String content =
                GitUtil.loadFileContentFromDiff(repository, commit, diff);

        final Optional<CompilationUnit> parseResult = JAVA_PARSER_SERVICE.parse(content);

        if (parseResult.isPresent()) {
            final String packageName =
                    JAVA_PARSER_SERVICE.getPackageNameFrom(parseResult.get());
            final String className =
                    JAVA_PARSER_SERVICE.getClassNameFrom(parseResult.get());
            final Integer complexity = measureComplexity(content);

            final int commentLines =
                    JAVA_PARSER_SERVICE.countCommentLines(parseResult.get());

            final int totalLines = Math.toIntExact(content.lines().count());

            final double commentToCodeRatio = commentLines / (double) totalLines;

            final PackageEntity packageEntity =
                    packageService.findOrCreatePackage(project, packageName);

            final ClassEntity classEntity;

            if (diff.getChangeType() == DiffEntry.ChangeType.RENAME) {
                final ProjectFileEntity projectFileEntity =
                        projectFileService.saveOrUpdate(project, diff.getNewPath());

                classEntity = classService.findOrCreateClass(packageEntity, className);
                projectFileEntity.setClassField(classEntity);

                classEntity.setName(className);
                classEntity.getPackageField().getClasses().remove(classEntity);
                classEntity.setPackageField(packageEntity);
                packageEntity.getClasses().add(classEntity);
            } else {
                classEntity = classService.findOrCreateClass(packageEntity, className);
            }

            classEntity.setStatus(ClassStatus.ACTIVE);

            final Optional<ClassContributionEntity> latestContribution =
                    classContributionService.findLatestContributionTo(classEntity);

            final ClassContributionEntity classContributionEntity =
                    classContributionService.findOrCreate(classEntity, timestamp, className, authorEntity);

            classComplexityService.createOrUpdate(classContributionEntity, latestContribution, complexity);
            classLinesOfCodeService.createOrUpdate(classContributionEntity, latestContribution, totalLines, commentLines, commentToCodeRatio);

            packageLinesOfCodeService.createOrUpdate(packageEntity);
            packageComplexityService.createOrUpdate(packageEntity);

            //TODO: Store AST for RAG?
            //DotPrinter yamlPrinter = new DotPrinter(true);
            //System.out.println(yamlPrinter.output(parseResult.get()));
            //System.out.println("-----------------------------------");

            //TODO: Measure author complexity for package
            //TODO: Measure author contribution to class
            //TODO: Measure author contribution to package
            //TODO: Track renamed and moved files
            //TODO: Track code/test ratio
            //TODO: Measure change coupling
            //TODO: Track issue tracking URL/issue references in comments

            LOGGER.debug("Parsed package {} and class {}", packageName, className);
        } else {
            LOGGER.error("Failed to parse class from: {}", diff.getNewPath());
        }
    }

    private boolean isNotExcludedType(final String path) {
        return path.endsWith(".java") &&
                !path.endsWith("module-info.java") &&
                !path.endsWith("package-info.java");
    }

    private static Integer measureComplexity(final String content) {
        return content.lines()
                .map(GitUtil::calculateWhitespaceComplexity)
                .mapToInt(Integer::intValue)
                .sum();
    }
}
