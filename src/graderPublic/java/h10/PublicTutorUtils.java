package h10;

import org.junit.jupiter.api.DisplayName;
import org.sourcegrade.jagr.api.rubric.Criterion;
import org.sourcegrade.jagr.api.rubric.Grader;
import org.sourcegrade.jagr.api.rubric.JUnitTestRef;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.match.BasicStringMatchers;
import org.tudalgo.algoutils.tutor.general.match.MatcherFactories;
import org.tudalgo.algoutils.tutor.general.reflections.BasicPackageLink;
import org.tudalgo.algoutils.tutor.general.reflections.MethodLink;
import org.tudalgo.algoutils.tutor.general.reflections.PackageLink;
import org.tudalgo.algoutils.tutor.general.reflections.TypeLink;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.contextBuilder;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions3.assertMethodExists;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions3.assertTypeExists;
import static org.tudalgo.algoutils.tutor.general.match.BasicReflectionMatchers.hasModifiers;
import static org.tudalgo.algoutils.tutor.general.reflections.Modifier.NON_STATIC;

/**
 * Defines the public utility methods for the testing purposes for the tasks of the assignment H10.
 *
 * @author Nhan Huynh
 */
public class PublicTutorUtils {

    /**
     * Link to the package of the assignment H10.
     */
    public static final PackageLink LINK_TO_PACKAGE = BasicPackageLink.of("h10");

    /**
     * A string matcher factory used to create a subject for a context.
     */
    private static final MatcherFactories.StringMatcherFactory STRING_MATCHER_FACTORY = BasicStringMatchers::identical;

    /**
     * The probability to always add new element to the list.
     */
    public static final Probability PROBABILITY_ALWAYS_ADD = () -> true;

    /**
     * Don't let anyone instantiate this class.
     */
    private PublicTutorUtils() {
    }

    /**
     * Returns the {@link Criterion} for the given class.
     *
     * @param description the description of the criterion
     * @param sourceClass the class to search for test methods
     *
     * @return the {@link Criterion} for the given class
     */
    public static Criterion criterion(String description, Class<?> sourceClass) {
        return Criterion.builder()
            .shortDescription(description)
            .addChildCriteria(
                // Get all test methods from the given class -> create a criterion for each test method
                // -> add the criterion to the list of child criteria
                Arrays.stream(sourceClass.getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(DisplayName.class))
                    .sorted(Comparator.comparing(method -> method.getAnnotation(DisplayName.class).value()))
                    .map(method ->
                        // Skip display name prefix: XX | Description
                        Criterion.builder().shortDescription(method.getAnnotation(DisplayName.class).value().substring(5))
                            .grader(
                                Grader.testAwareBuilder()
                                    .requirePass(JUnitTestRef.ofMethod(method))
                                    .pointsPassedMax()
                                    .pointsFailedMin()
                                    .build()
                            )
                            .build()
                    )
                    .toArray(Criterion[]::new)
            )
            .build();
    }

    /**
     * Returns a link to the class for this assignment.
     *
     * @return a link to the class for this assignment
     */
    public static TypeLink linkClass() {
        return assertTypeExists(
            LINK_TO_PACKAGE,
            STRING_MATCHER_FACTORY.matcher(SkipList.class.getSimpleName())
        );
    }

    /**
     * Returns a link to the method with the specified name.
     *
     * @param methodName the name of the method
     *
     * @return a link to the method with the specified name
     */
    public static MethodLink linkMethod(String methodName) {
        return assertMethodExists(
            linkClass(),
            STRING_MATCHER_FACTORY.matcher(methodName).and(hasModifiers(NON_STATIC))
        );
    }

    /**
     * Returns a basic context for a skip list.
     *
     * @param list the list to access information from
     * @param <T>  the type of the list
     *
     * @return the basic context for a skip list
     */
    public static <T> Context contextList(SkipList<T> list) {
        return contextBuilder()
            .subject(linkClass())
            .add("Comparator", list.cmp)
            .add("Max Height", list.maxHeight)
            .add("Probability", list.getProbability())
            .add("Elements", list)
            .add("Size", list.size)
            .add("Current Height", list.getHeight())
            .build();
    }

    /**
     * Creates a context for the given list operation {@link SkipList#contains(Object)}.
     *
     * @param list the list to execute the operation on
     * @param key  the element to search for
     *
     * @return the context for the given list operation
     */
    public static <T> Context contextH1(SkipList<T> list, T key) {
        return contextBuilder()
            .subject(linkMethod("contains"))
            .add("Element to search for", key)
            .add("SkipList properties", contextList(list))
            .build();
    }

    /**
     * Creates a context for the given list operation {@link SkipList#add(Object)}.
     *
     * @param list the list to execute the operation on
     * @param key  the element to search for
     *
     * @return the context for the given list operation
     */
    public static <T> Context contextH2(SkipList<T> list, T key) {
        Context.Builder<?> builder = contextBuilder()
            .subject(linkMethod("add"))
            .add("Element to add", key)
            .add("Before insertion", contextList(list));
        list.add(key);
        return builder.add("After insertion", contextList(list))
            .build();
    }

    /**
     * Creates a context for the given list operation {@link SkipList#remove(Object)}.
     *
     * @param list the list to execute the operation on
     * @param key  the element to search for
     *
     * @return the context for the given list operation
     */
    public static <T> Context contextH3(SkipList<T> list, T key) {
        Context.Builder<?> builder = contextBuilder()
            .subject(linkMethod("add"))
            .add("Element to remove", key)
            .add("Before removal", contextList(list));
        list.remove(key);
        return builder.add("After removal", contextList(list))
            .build();
    }

    /**
     * Creates a copy of a skip list.
     *
     * @param list the skip list to copy
     * @param <T>  the type of the elements in the skip list
     *
     * @return a copy of the skip list
     */
    public static <T> SkipList<T> copy(SkipList<T> list) {
        // References to the upper level
        Map<T, ListItem<ExpressNode<T>>> references = new HashMap<>();
        ListItem<ExpressNode<T>> head = null;
        ListItem<ExpressNode<T>> tail = null;

        for (ListItem<ExpressNode<T>> currentLevel = list.head; currentLevel != null;
             currentLevel = currentLevel.key.down) {
            // Create a new level with its nodes, each level starts with a sentinel node
            ListItem<ExpressNode<T>> sentinel = new ListItem<>();
            sentinel.key = new ExpressNode<>();

            // Link nodes to the upper level
            linkUpperLevel(references, sentinel);

            if (head == null) {
                head = sentinel;
            } else {
                tail.key.down = sentinel;
                sentinel.key.up = tail;
            }
            tail = sentinel;

            // Fill nodes to the current level
            ListItem<ExpressNode<T>> currentLevelTail = sentinel;
            for (ListItem<ExpressNode<T>> node = currentLevel.next; node != null; node = node.next) {
                ListItem<ExpressNode<T>> item = new ListItem<>();
                item.key = new ExpressNode<>();
                item.key.value = node.key.value;
                currentLevelTail.next = item;
                item.key.prev = currentLevelTail;
                currentLevelTail = currentLevelTail.next;

                // Link nodes to the upper level
                linkUpperLevel(references, item);
            }
        }
        SkipList<T> copy = new SkipList<>(list.cmp, list.maxHeight, list.getProbability());
        copy.head = head;
        copy.height = list.height;
        copy.size = list.size;
        return copy;
    }

    /**
     * Links the given node to the upper level.
     *
     * @param references the map of references to the upper level
     * @param item       the node to link
     * @param <T>        the type of the node
     */
    public static <T> void linkUpperLevel(Map<T, ListItem<ExpressNode<T>>> references,
                                          ListItem<ExpressNode<T>> item) {
        if (references.containsKey(item.key.value)) {
            ListItem<ExpressNode<T>> upperLevel = references.get(item.key.value);
            upperLevel.key.down = item;
            item.key.up = upperLevel;
        }
        references.put(item.key.value, item);
    }

    /**
     * Copies all list item references levels to a list of lists.
     *
     * @param head the head of the list
     * @param <T>  the type of the elements in this list
     *
     * @return a list of lists containing all list item references
     */
    public static <T> List<List<ListItem<ExpressNode<T>>>> listItemAsList(ListItem<ExpressNode<T>> head) {
        List<List<ListItem<ExpressNode<T>>>> levels = new ArrayList<>();
        ListItem<ExpressNode<T>> walkerDown = head;
        while (walkerDown != null) {
            List<ListItem<ExpressNode<T>>> level = new ArrayList<>();
            ListItem<ExpressNode<T>> walker = walkerDown;
            while (walker != null) {
                level.add(walker);
                walker = walker.next;
            }
            levels.add(level);
            walkerDown = walkerDown.key.down;
        }
        return levels;
    }

}
