package software.bananen.gavel.backend.services.technical;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.Comment;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JavaParserService {

    private static final JavaParser JAVA_PARSER = initJavaParser();

    public Optional<CompilationUnit> parse(final String content) {
        final ParseResult<CompilationUnit> parseResult = JAVA_PARSER.parse(content);

        if (parseResult.isSuccessful()) {
            final CompilationUnit compilationUnit = parseResult.getResult().get();

            if (compilationUnit.getPackageDeclaration().isPresent() && compilationUnit.getTypes().isNonEmpty()) {
                return Optional.of(compilationUnit);
            }
        }

        return Optional.empty();
    }

    public Integer countCommentLines(final CompilationUnit compilationUnit) {
        return compilationUnit
                .getAllComments()
                .stream()
                .map(Comment::asString)
                .mapToInt(comment -> Math.toIntExact(comment.lines().count()))
                .sum();
    }

    public String getPackageNameFrom(final CompilationUnit compilationUnit) {
        return compilationUnit.getPackageDeclaration().get().getName().asString();
    }

    public String getClassNameFrom(final CompilationUnit compilationUnit) {
        return compilationUnit.getType(0).getName().asString();
    }

    private static JavaParser initJavaParser() {
        final ParserConfiguration config = new ParserConfiguration();
        config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21);
        return new JavaParser(config);
    }
}
