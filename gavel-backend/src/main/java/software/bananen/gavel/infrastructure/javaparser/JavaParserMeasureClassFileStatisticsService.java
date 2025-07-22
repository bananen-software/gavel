package software.bananen.gavel.infrastructure.javaparser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import org.apache.commons.codec.digest.DigestUtils;
import software.bananen.gavel.domain.ports.service.MeasureClassFileStatisticsService;
import software.bananen.gavel.domain.service.MeasureCommentToCodeRatioService;
import software.bananen.gavel.domain.service.MeasureWhitespaceComplexityService;
import software.bananen.gavel.domain.service.RateClassComplexityService;
import software.bananen.gavel.domain.service.RateClassSizeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class defines a service that can be used to parse java source code files
 * for further analysis.
 */
public class JavaParserMeasureClassFileStatisticsService
        implements MeasureClassFileStatisticsService {

    private static final JavaParser JAVA_PARSER = initJavaParser();
    private static final MeasureWhitespaceComplexityService COMPLEXITY_SERVICE =
            new MeasureWhitespaceComplexityService();

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

            if (compilationUnit.getPackageDeclaration().isPresent() && compilationUnit.getTypes().isNonEmpty()) {
                return Optional.of(compilationUnit);
            }
        }

        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ClassFileStatistics> measureClassFileStatistics(final String content) {
        final Optional<CompilationUnit> parseResult = parse(content);
        final var contentLines = content.split("\n");

        return parseResult.map(cu -> {
            final Collection<MethodStatistics> methodStatistics = new ArrayList<>();
            final String packageName = getPackageNameFrom(cu);
            final String className = getClassNameFrom(cu);

            for (final MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
                final var methodBegin = method.getBegin().get().line;
                final var methodEnd = method.getEnd().get().line;

                final var methodBody = Stream.of(contentLines)
                        .skip(methodBegin - 1)
                        .limit(methodEnd - methodBegin + 1)
                        .collect(Collectors.joining("\n"));

                final var md5Hash = DigestUtils.md5Hex(methodBody);

                methodStatistics.add(new MethodStatistics(method.getName().asString(),
                        method.getSignature().asString(),
                        methodBody.split("\n").length,
                        new MeasureWhitespaceComplexityService().measure(methodBody),
                        md5Hash));
            }

            final Integer complexity = COMPLEXITY_SERVICE.measure(content);
            final int commentLines = countCommentLines(parseResult.get());
            final int totalLines = Math.toIntExact(content.lines().count());

            final double commentToCodeRatio =
                    new MeasureCommentToCodeRatioService().measure(totalLines, commentLines);

            return new ClassFileStatistics(
                    className,
                    packageName,
                    complexity,
                    commentLines,
                    totalLines,
                    commentToCodeRatio,
                    new RateClassSizeService().rate(totalLines),
                    new RateClassComplexityService().rate(complexity),
                    methodStatistics
            );
        });
    }


    /**
     * Counts the comment lines in the given compilation unit.
     *
     * @param compilationUnit The compilation unit.
     * @return The number of comment lines.
     */
    private Integer countCommentLines(final CompilationUnit compilationUnit) {
        return compilationUnit
                .getAllComments()
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
        return compilationUnit.getPackageDeclaration().get().getName().asString();
    }

    /**
     * Retrieves the class value from the given compilation unit.
     *
     * @param compilationUnit The compilation unit.
     * @return The class value.
     */
    private String getClassNameFrom(final CompilationUnit compilationUnit) {
        return compilationUnit.getType(0).getName().asString();
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
