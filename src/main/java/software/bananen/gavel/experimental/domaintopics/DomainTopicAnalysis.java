package software.bananen.gavel.experimental.domaintopics;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Predicate;

public class DomainTopicAnalysis {

    private static final Collection<String> BLACKLIST = List.of("package-info");

    public void doTheThing(Set<JavaClass> classes) {
        DomainTopics domainTopics = new DomainTopics();

        for (final JavaClass aClass :
                classes.stream().filter(isNotBlacklisted()).toList()) {
            String[] words = StringUtils.splitByCharacterTypeCamelCase(aClass.getSimpleName());

            for (String word : words) {
                domainTopics.addWord(aClass.getPackageName(), word);
            }

            for (JavaMethod method :
                    aClass.getMethods()
                            .stream()
                            .filter(method -> method.getModifiers().contains(JavaModifier.PUBLIC))
                            .toList()) {
                for (String word : StringUtils.splitByCharacterTypeCamelCase(method.getName())) {
                    domainTopics.addWord(aClass.getPackageName(), word);
                }
            }
        }

        for (Map.Entry<String, Collection<Word>> entry : domainTopics.entries()) {
            String packageName = entry.getKey();

            System.out.println(packageName);
            System.out.println("---------");
            Collection<Word> words =
                    entry.getValue().stream().sorted(Comparator.comparing(Word::count).reversed()).toList();
            for (Word word : words) {
                System.out.println("\t" + word.getWord() + ": " + word.count());
            }

            System.out.println();
        }
    }

    private static Predicate<JavaClass> isNotBlacklisted() {
        return clazz -> !BLACKLIST.contains(clazz.getSimpleName());
    }
}
