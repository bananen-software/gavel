package gavel.adapter.language.java;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bananen.gavel.domain.model.FileDiff;
import software.bananen.gavel.domain.ports.driven.ClassCodeUnitMetrics;
import software.bananen.gavel.domain.ports.driven.CodeUnitMetrics;
import software.bananen.gavel.domain.ports.driven.MethodCodeUnitMetrics;
import software.bananen.gavel.domain.ports.driven.SourceFileMeasurementPort;
import software.bananen.gavel.domain.ports.driven.SourceFileMetrics;
import software.bananen.gavel.domain.ports.driven.VersionControlSystemException;
import software.bananen.gavel.domain.service.MeasureWhitespaceComplexityService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * An adapter that measures java source files.
 */
public final class JavaSourceFileMeasurementAdapter
        implements SourceFileMeasurementPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaSourceFileMeasurementAdapter.class);

    private static final JavaParser JAVA_PARSER = initJavaParser();

    private final MeasureWhitespaceComplexityService whitespaceService;

    /**
     * Creates a new instance.
     *
     * @param whitespaceService A service that is used to determine the whitespace complexity for files.
     */
    public JavaSourceFileMeasurementAdapter(final MeasureWhitespaceComplexityService whitespaceService) {
        this.whitespaceService =
                requireNonNull(whitespaceService, "The whitespace service must not be null.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceFileMetrics generateFor(final FileDiff diff) throws VersionControlSystemException {
        final var content = new String(diff.loadContent(), StandardCharsets.UTF_8);
        final var contentLines = content.split("\n");
        final var complexity = whitespaceService.measure(content);
        final Collection<CodeUnitMetrics> codeUnits = new ArrayList<>();

        final Optional<CompilationUnit> parsed = parse(content);

        if (parsed.isPresent()) {
            final var packageName = getPackageNameFrom(parsed.get());

            final Optional<ClassOrInterfaceDeclaration> outerClass =
                    parsed.get()
                            .findFirst(ClassOrInterfaceDeclaration.class);

            outerClass.ifPresent(classOrInterfaceDeclaration ->
                    codeUnits.add(measureClass(packageName, classOrInterfaceDeclaration)));
        }

        return new SourceFileMetrics(
                contentLines.length,
                complexity,
                codeUnits
        );
    }

    private ClassCodeUnitMetrics measureClass(
            final String packageName,
            final ClassOrInterfaceDeclaration outerClass) {
        final var hash = DigestUtils.md5Hex(outerClass.toString());
        final var contentLines = outerClass.toString().split("\n");
        final var linesOfComments = countCommentLines(outerClass);
        final var complexity = whitespaceService.measure(outerClass.toString());

        final Collection<CodeUnitMetrics> children = new ArrayList<>();

        outerClass.getChildNodes()
                .stream()
                .filter(child -> child instanceof ClassOrInterfaceDeclaration)
                .map(child -> (ClassOrInterfaceDeclaration) child)
                .map(clazz -> measureClass(packageName, clazz))
                .forEach(children::add);

        outerClass.getChildNodes()
                .stream()
                .filter(child -> child instanceof MethodDeclaration)
                .map(child -> (MethodDeclaration) child)
                .map(this::measureMethod)
                .forEach(children::add);

        return new ClassCodeUnitMetrics(
                outerClass.getNameAsString(),
                packageName,
                children,
                hash,
                contentLines.length,
                linesOfComments,
                complexity
        );
    }

    /**
     * Measures the given method declaration.
     *
     * @param method The method.
     * @return The metrics.
     */
    private CodeUnitMetrics measureMethod(final MethodDeclaration method) {
        final var methodBody = method.toString();
        final var methodHash = DigestUtils.md5Hex(methodBody);
        final var complexity = whitespaceService.measure(methodBody);

        final var lineOfComments =
                method.getAllContainedComments()
                        .stream()
                        .map(Comment::asString)
                        .mapToInt(comment -> Math.toIntExact(comment.lines().count()))
                        .sum();

        return new MethodCodeUnitMetrics(
                method.getName().asString(),
                method.getSignature().asString(),
                methodHash,
                methodBody.split("\n").length,
                lineOfComments,
                complexity
        );
    }

    /**
     * Parses the given content of a java class into a {@link CompilationUnit}.
     *
     * @param content The file content.
     * @return The parsed {@link CompilationUnit} or {@link Optional#empty()} if
     * the class could not be parsed.
     */
    private Optional<CompilationUnit> parse(final String content) {
        final ParseResult<CompilationUnit> parseResult = JAVA_PARSER.parse(content);

        if (parseResult.isSuccessful()) {
            final CompilationUnit compilationUnit = parseResult.getResult().get();

            if (compilationUnit.getTypes().isNonEmpty()) {
                return Optional.of(compilationUnit);
            }
        } else {
            LOGGER.error("Failed to parse source file: {}", parseResult.getProblems());
        }

        return Optional.empty();
    }

    /**
     * Counts the comment lines in the given compilation unit.
     *
     * @param clazz The compilation unit.
     * @return The number of comment lines.
     */
    private Integer countCommentLines(final ClassOrInterfaceDeclaration clazz) {
        return clazz
                .getAllContainedComments()
                .stream()
                .map(Comment::asString)
                .mapToInt(comment -> Math.toIntExact(comment.lines().count()))
                .sum();
    }

    /**
     * Retrieves the package value from the given compilation unit.
     *
     * @param compilationUnit The compilation unit.
     * @return The package value.
     */
    private String getPackageNameFrom(final CompilationUnit compilationUnit) {
        return compilationUnit.getPackageDeclaration()
                .map(PackageDeclaration::getNameAsString)
                .orElse("");
    }

    /**
     * Initializes the java parser.
     *
     * @return The java parser.
     */
    private static JavaParser initJavaParser() {
        final ParserConfiguration config = new ParserConfiguration();
        config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21);
        return new JavaParser(config);
    }
}
