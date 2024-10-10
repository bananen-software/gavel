package software.bananen.gavel.metrics;

import com.tngtech.archunit.core.domain.*;

import java.util.*;
import java.util.function.Predicate;

/**
 * A service that can be used to measure the weighted interface complexity of
 * the codebase.
 * <p>
 * The metrics aim to measure how hard it is to use the interfaces.
 * <p>
 * Currently, the algorithm is only partially implemented, because from the
 * available resources it is not clear how to determine the fourth measurement
 * called the Maximum_Nesting_Depth_Parameter_Objects.
 */
public final class WeightedInterfaceComplexityMetricsService {

    /**
     * Measures the weighted interface complexity for the given classes.
     *
     * @param classes The classes.
     * @return The weighted interface complexity.
     */
    public Collection<WeightedInterfaceComplexity> measure(final JavaClasses classes) {
        final Collection<WeightedInterfaceComplexity> measurements = new ArrayList<>();

        for (final JavaClass aClass : classes) {
            measurements.add(determineInterfaceComplexity(aClass));
        }

        return measurements;
    }

    public WeightedInterfaceComplexity determineInterfaceComplexity(final JavaClass aClass) {
        final Collection<JavaMethod> publicMethods =
                findAllPublicMethods(aClass);
        final Collection<JavaMember> publicMembers =
                findAllPublicMembers(aClass);

        final int publicMethodAndMemberCountScore =
                score(publicMethods.size() + publicMembers.size());

        int parameterCount = 0;
        final Set<JavaType> datatypes = new HashSet<>();

        for (final JavaMethod publicMethod : publicMethods) {
            parameterCount += publicMethod.getParameters().size();
            //TODO: Return types

            for (final JavaParameter parameter : publicMethod.getParameters()) {
                datatypes.add(parameter.getType());
            }
        }

        final int parameterCountScore = score(parameterCount);
        final int datatypeCountScore = score(datatypes.size());

        //TODO: Determine nesting depth
        final int nestingScore = 1;

        final int score =
                publicMethodAndMemberCountScore *
                        parameterCountScore *
                        datatypeCountScore *
                        nestingScore;

        return new WeightedInterfaceComplexity(
                aClass.getFullName(),
                publicMethodAndMemberCountScore,
                parameterCountScore,
                datatypeCountScore,
                nestingScore,
                score
        );
    }

    /**
     * Finds all the public members of the given class.
     *
     * @param aClass The class.
     * @return The members.
     */
    private static List<JavaMember> findAllPublicMembers(final JavaClass aClass) {
        return aClass.getMembers()
                .stream()
                .filter(isPublicMember())
                .toList();
    }

    /**
     * A predicate that checks a {@link JavaMember} whether it is public.
     *
     * @return The predicate.
     */
    private static Predicate<JavaMember> isPublicMember() {
        return method -> method.getModifiers().contains(JavaModifier.PUBLIC);
    }

    /**
     * Finds all the public methods of a class.
     *
     * @param aClass The class.
     * @return The public methods.
     */
    private static List<JavaMethod> findAllPublicMethods(final JavaClass aClass) {
        return aClass.getMethods()
                .stream()
                .filter(isPublicMember())
                .toList();
    }

    /**
     * Determines the score based on the algorithm.
     *
     * @param value The value.
     * @return The score.
     */
    public int score(int value) {
        return String.valueOf(value).length();
    }
}
