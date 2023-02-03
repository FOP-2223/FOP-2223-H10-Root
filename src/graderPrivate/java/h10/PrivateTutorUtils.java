package h10;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sourcegrade.jagr.api.rubric.Criterion;
import org.sourcegrade.jagr.api.rubric.Gradable;
import org.sourcegrade.jagr.api.rubric.Grader;
import org.sourcegrade.jagr.api.rubric.JUnitTestRef;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.reflections.BasicConstructorLink;
import org.tudalgo.algoutils.tutor.general.reflections.BasicMethodLink;
import org.tudalgo.algoutils.tutor.general.reflections.BasicTypeLink;
import org.tudalgo.algoutils.tutor.general.reflections.ConstructorLink;
import org.tudalgo.algoutils.tutor.general.reflections.Link;
import org.tudalgo.algoutils.tutor.general.reflections.MethodLink;
import org.tudalgo.algoutils.tutor.general.reflections.TypeLink;
import org.tudalgo.algoutils.tutor.general.reflections.WithName;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtExecutableReference;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static h10.PublicTutorUtils.linkMethod;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertEquals;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.contextBuilder;

/**
 * Defines the private utility methods for the testing purposes for the tasks of the assignment H10.
 *
 * @author Nhan Huynh
 */
public class PrivateTutorUtils {

    /**
     * Don't let anyone instantiate this class.
     */
    private PrivateTutorUtils() {
    }

    /**
     * Returns a 2D array of booleans that represents the visited nodes of the given skip list where each array
     * represents a level and each element of the array represents a node.
     *
     * @param nodes the skip list
     * @param <T>   the type of the elements in the skip list
     *
     * @return a 2D array of booleans that represents the visited nodes of the given skip list
     */
    public static <T> boolean[][] getVisitedNodes(List<List<ListItem<ExpressNode<VisitorNode<T>>>>> nodes) {
        boolean[][] visits = new boolean[nodes.size()][];
        for (int level = 0; level < nodes.size(); level++) {
            visits[level] = new boolean[nodes.get(level).size()];
            for (int listIndex = 0; listIndex < nodes.get(level).size(); listIndex++) {
                ListItem<ExpressNode<VisitorNode<T>>> node = nodes.get(level).get(listIndex);
                if (node.key.value == null) {
                    continue;
                }
                visits[level][listIndex] = node.key.value.isVisited();
            }
        }
        return visits;
    }

    /**
     * Returns a 2D array of booleans that represents the expected visited nodes of the given skip list where each array
     * represents a level and each element of the array represents a node.
     *
     * @param nodes       the skip list
     * @param comparisons the expected comparisons path
     * @param <T>         the type of the elements in the skip list
     *
     * @return a 2D array of booleans that represents the visited nodes of the given skip list
     */
    public static <T> boolean[][] getVisitedNodesComparisons(
        List<List<ListItem<ExpressNode<VisitorNode<T>>>>> nodes,
        Integer[][] comparisons
    ) {
        boolean[][] visits = new boolean[nodes.size()][];
        for (int level = 0; level < nodes.size(); level++) {
            visits[level] = new boolean[nodes.get(level).size()];
            if (comparisons.length <= level) {
                continue;
            }
            for (int listIndex = 0, comparisonIndex = 0; listIndex < nodes.get(level).size(); listIndex++) {
                if (comparisonIndex >= comparisons[level].length) {
                    break;
                }
                int comparison = comparisons[level][comparisonIndex];
                if (comparison == listIndex) {
                    visits[level][listIndex] = true;
                    comparisonIndex++;
                }
            }
        }
        return visits;
    }

    /**
     * Tests whether the comparisons path of the given skip list is correct.
     *
     * @param nodes       the skip list to test
     * @param comparisons the expected comparisons path
     * @param context     the context of the test
     * @param <T>         the type of the elements in the skip list
     */
    public static <T> void assertComparisons(
        List<List<ListItem<ExpressNode<VisitorNode<T>>>>> nodes,
        Integer[][] comparisons,
        Context context
    ) {
        boolean[][] actualVisits = getVisitedNodes(nodes);
        boolean[][] expectedVisits = getVisitedNodesComparisons(nodes, comparisons);
        for (int level = 0; level < expectedVisits.length; level++) {
            for (int i = 0; i < expectedVisits[level].length; i++) {
                int currentLevel = level;
                int index = i;
                if (expectedVisits[level][i]) {
                    Assertions2.assertTrue(
                        actualVisits[level][i],
                        context,
                        result -> String.format("Expected to visit node at level %s and index %s, but did not.",
                            currentLevel, nodes.get(currentLevel).get(index)
                        )
                    );
                } else {
                    Assertions2.assertFalse(
                        actualVisits[level][i],
                        context,
                        result -> String.format("Expected to not visit node at level %s and index %s, but did.",
                            currentLevel, nodes.get(currentLevel).get(index)
                        )
                    );
                }
            }
        }
    }

    /**
     * Converts the given object to the given type.
     *
     * @param object the object to convert
     * @param <T>    the type to convert to
     *
     * @return the converted object
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(Object object) {
        return (T) object;
    }

    /**
     * Returns the {@link Criterion} for the given class.
     *
     * @param description the description of the criterion
     * @param sourceClasses the classes to search for test methods
     *
     * @return the {@link Criterion} for the given class
     */
    public static Criterion criterion(String description, Class<?>... sourceClasses) {
        // Get all test methods from the given class -> create a criterion for each test method
        // -> add the criterion to the list of child criteria
        List<Criterion> criteria = Arrays.stream(sourceClasses)
            .flatMap(sourceClass -> Arrays.stream(sourceClass.getMethods()))
            .filter(method -> method.isAnnotationPresent(DisplayName.class))
            .filter(method -> !method.isAnnotationPresent(Test.class))
            .sorted(Comparator.comparing(method -> method.getAnnotation(DisplayName.class).value()))
            .map(method -> {
                // Skip display name prefix: XX | Description
                String shortDescription = method.getAnnotation(DisplayName.class).value();
                return Criterion.builder().shortDescription(shortDescription.substring(5))
                    .grader(
                        Grader.testAwareBuilder()
                            .requirePass(JUnitTestRef.ofMethod(method))
                            .pointsPassedMax()
                            .pointsFailedMin()
                            .build()
                    )
                    .build();
            }).collect(Collectors.toList());

        int maxPoints = criteria.stream().mapToInt(Gradable::getMaxPoints).sum();

        List<JUnitTestRef> criterionRequirements = Arrays.stream(sourceClasses)
            .flatMap(sourceClass -> Arrays.stream(sourceClass.getMethods()))
            .filter(method -> method.getName().equals("testRequirements"))
            .map(JUnitTestRef::ofMethod)
            .toList();
        Grader.TestAwareBuilder builderRequirements = Grader.testAwareBuilder();
        criterionRequirements.forEach(builderRequirements::requirePass);


        Criterion requirements = Criterion.builder().shortDescription("Verbindliche Anforderungen")
            .grader(
                builderRequirements
                    .pointsPassedMax()
                    .pointsFailedMin()
                    .build()
            )
            .maxPoints(0)
            .minPoints(-maxPoints)
            .build();

        criteria.add(requirements);
        return Criterion.builder()
            .shortDescription(description)
            .addChildCriteria(criteria.toArray(Criterion[]::new))
            .minPoints(0)
            .build();
    }

    /**
     * Collects all constructor calls from the given method link.
     *
     * @param methodLink the method link to collect the constructor calls from
     *
     * @return the collected constructor calls
     */
    public static Set<ConstructorLink> collectConstructorCalls(BasicMethodLink methodLink) {
        return collectConstructorCalls(methodLink.getCtElement(), new HashSet<>(), new HashSet<>());
    }

    /**
     * Collects all constructor calls from the element and its children.
     *
     * @param element the element to collect the constructor calls from
     * @param calls   the collected constructor calls so far
     * @param visited the visited elements so far to avoid checking the same element twice
     *
     * @return the collected constructor calls
     */
    private static Set<ConstructorLink> collectConstructorCalls(
        CtElement element,
        Set<ConstructorLink> calls,
        Set<Link> visited) {
        // Current location
        element.getElements(CtConstructorCall.class::isInstance).stream()
            .map(CtConstructorCall.class::cast)
            .map(CtConstructorCall::getExecutable)
            // null if the constructor is not found
            .filter(Predicate.not(Objects::isNull))
            .map(CtExecutableReference::getActualConstructor)
            .map(BasicConstructorLink::of)
            .filter(Predicate.not(visited::contains))
            .distinct()
            .peek(visited::add)
            .forEach(calls::add);


        // Method invocations check for constructor calls
        Set<BasicMethodLink> methodLinks = element.getElements(CtInvocation.class::isInstance).stream()
            .map(CtInvocation.class::cast)
            .map(CtInvocation::getExecutable)
            .map(CtExecutableReference::getActualMethod)
            // null if the method is not found
            .filter(Predicate.not(Objects::isNull))
            .map(BasicMethodLink::of)
            .filter(Predicate.not(visited::contains))
            .distinct()
            .peek(visited::add)
            .collect(Collectors.toSet());

        if (!methodLinks.isEmpty()) {
            methodLinks.forEach(link -> {
                try {
                    if (link.isCtElementAvailable()) {
                        collectConstructorCalls(link.getCtElement(), calls, visited);
                    }
                } catch (NullPointerException e) {
                    // ignore
                    if (!e.getMessage().equals("Cannot invoke \"String.toCharArray()\" because \"this.content\" is "
                        + "null")) {
                        throw e;
                    }
                }
            });
        }
        return calls;
    }

    /**
     * Returns the name of the given links.
     *
     * @param elements the links to get the names from
     *
     * @return the names of the given links
     */
    private static Collection<String> names(Collection<? extends WithName> elements) {
        return elements.stream().map(WithName::name).collect(Collectors.toList());
    }

    /**
     * Tests whether the method only uses the given constructor calls.
     *
     * @param methodName     the name of the method to check
     * @param allowedClasses the allowed classes
     */
    public static void assertUseOnlyConstructorCalls(String methodName, List<? extends TypeLink> allowedClasses) {
        MethodLink methodLink = linkMethod(methodName);
        Set<ConstructorLink> calls = collectConstructorCalls((BasicMethodLink) methodLink);
        Context context = contextBuilder()
            .subject(methodLink)
            .add(
                "Allowed creation of classes",
                allowedClasses.stream().map(WithName::name).collect(Collectors.toSet())
            )
            .build();
        Set<TypeLink> found = calls.stream()
            .map(BasicConstructorLink.class::cast)
            .map(BasicConstructorLink::reflection)
            .map(Constructor::getDeclaringClass)
            .distinct()
            .map(BasicTypeLink::of)
            .collect(Collectors.toSet());
        allowedClasses.forEach(found::remove);
        assertEquals(
            0,
            found.size(),
            context,
            result -> String.format(
                "Expected only constructor calls from %s, but found %s",
                names(allowedClasses),
                names(found)
            )
        );
    }

    /**
     * Tests whether the method only uses the given constructor calls.
     *
     * @param methodName     the name of the method to check
     * @param allowedClasses the allowed classes
     */
    public static void assertUseOnlyConstructorCalls(String methodName, TypeLink... allowedClasses) {
        assertUseOnlyConstructorCalls(methodName, List.of(allowedClasses));
    }

    /**
     * Tests whether the method only uses the given constructor calls.
     *
     * @param methodName     the name of the method to check
     * @param allowedClasses the allowed classes
     */
    public static void assertUseOnlyConstructorCalls(String methodName, Class<?>... allowedClasses) {
        assertUseOnlyConstructorCalls(methodName, Arrays.stream(allowedClasses).map(BasicTypeLink::of).toList());
    }

    /**
     * Tests whether the method only uses no constructor calls.
     *
     * @param methodName the name of the method to check
     */
    public static void assertNoConstructorCalls(String methodName) {
        assertUseOnlyConstructorCalls(methodName, List.of());
    }

}
