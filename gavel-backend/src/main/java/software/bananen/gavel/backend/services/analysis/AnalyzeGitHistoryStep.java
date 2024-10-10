package software.bananen.gavel.backend.services.analysis;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.Comment;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bananen.gavel.backend.domain.ClassContributionAggregate;
import software.bananen.gavel.backend.domain.ProjectAggregate;
import software.bananen.gavel.backend.entity.AuthorEntity;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.backend.repository.AuthorRepository;
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
import java.util.function.Supplier;

import static software.bananen.gavel.behavioralanalysis.git.GitUtil.loadMailmap;

public class AnalyzeGitHistoryStep extends AbstractAnalysisStep {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyzeGitHistoryStep.class);

    private static final String STEP_NAME = "Analyze git history";

    private static final JavaParser JAVA_PARSER = initJavaParser();
    private final ProjectEntity project;
    private final AuthorRepository authorRepository;

    /**
     * Creates a new instance.
     *
     * @param taskId The ID of the task that the step belongs to.
     */
    public AnalyzeGitHistoryStep(final String taskId,
                                 final ProjectEntity project,
                                 final AuthorRepository authorRepository) {
        super(taskId, STEP_NAME);
        this.project = project;
        this.authorRepository = authorRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runAnalysis() {
        final GitService gitService = new GitService();

        for (final Path path : gitService.locateGitRepositories(List.of("/home/dennis/workspace/github/flens-dev/pendenzenliste"))) {
            try (final Repository repository = gitService.loadRepository(path)) {
                LOGGER.info("Processing repository {}", path);

                final String projectName = path.getName(path.getNameCount() - 1).toString();

                final Mailmap mailmap = loadMailmap(path);

                LOGGER.info(".mailmap loaded with {} entries", mailmap.size());

                final Git git = new Git(repository);

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

        final ProjectAggregate projectAggregate = new ProjectAggregate(project);

        //TODO: Measure author contribution to project

        final AuthorEntity authorEntity =
                authorRepository.findByNameAndEmail(author.name(), author.email())
                        .orElseGet(mapToEntity(author));

        authorRepository.save(authorEntity);

        for (final DiffEntry diff : GitUtil.extractDiffEntries(repository, commit)) {
            if (isNotExcludedType(diff)) {
                final String content =
                        GitUtil.loadFileContentFromDiff(repository, commit, diff);

                final Optional<CompilationUnit> parseResult = parse(content);

                if (parseResult.isPresent()) {
                    final String packageName =
                            parseResult.get().getPackageDeclaration().get().getName().asString();
                    final String className =
                            parseResult.get().getType(0).getName().asString();
                    final Integer complexity = measureComplexity(content);

                    final int commentLines = parseResult.get()
                            .getAllComments()
                            .stream()
                            .map(Comment::asString)
                            .map(comment -> comment.split("\n"))
                            .mapToInt(lines -> lines.length)
                            .sum();

                    final int totalLines = content.split("\n").length;

                    final double commentToCodeRatio = commentLines / (double) totalLines;

                    projectAggregate.findPackage(packageName)
                            .flatMap(pkg -> pkg.findClass(className))
                            .ifPresent(clazz -> {


                                final ClassContributionAggregate contribution =
                                        clazz.findOrCreateContribution(timestamp, commit.getName(), authorEntity);

                                contribution.recordComplexityInstance(complexity);
                                contribution.recordLinesOfCode(totalLines, commentLines, commentToCodeRatio);
                            });

                    projectAggregate.findPackage(packageName).ifPresent(pkg -> {
                        pkg.recordPackageComplexity();
                        pkg.recordPackageLines();
                    });

                    for (TypeDeclaration<?> type : parseResult.get().getTypes()) {
                        for (MethodDeclaration method : type.getMethods()) {
                        }
                    }

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
            } else {
                LOGGER.debug("Skipping excluded file type {}", diff.getNewPath());
            }
        }

        //TODO: Measure aggregate metrics for project
    }

    private static Supplier<AuthorEntity> mapToEntity(Author author) {
        return () -> {
            final AuthorEntity authorEntity = new AuthorEntity();

            authorEntity.setName(author.name());
            authorEntity.setEmail(author.email());

            return authorEntity;
        };
    }

    private boolean isNotExcludedType(final DiffEntry diff) {
        return diff.getNewPath().endsWith(".java") && !diff.getNewPath().endsWith("module-info.java");
    }

    private static Integer measureComplexity(final String content) {
        return content.lines()
                .map(GitUtil::calculateWhitespaceComplexity)
                .mapToInt(Integer::intValue)
                .sum();
    }

    private static JavaParser initJavaParser() {
        final ParserConfiguration config = new ParserConfiguration();
        config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21);
        return new JavaParser(config);
    }

    private static Optional<CompilationUnit> parse(final String content) {
        ParseResult<CompilationUnit> parseResult = JAVA_PARSER.parse(content);

        if (parseResult.isSuccessful()) {
            final CompilationUnit compilationUnit = parseResult.getResult().get();

            if (compilationUnit.getPackageDeclaration().isPresent() && compilationUnit.getTypes().isNonEmpty()) {
                return Optional.of(compilationUnit);
            }
        }

        return Optional.empty();
    }
}
