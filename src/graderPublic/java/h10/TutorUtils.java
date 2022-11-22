package h10;

import org.junit.jupiter.api.DisplayName;
import org.sourcegrade.jagr.api.rubric.Criterion;
import org.sourcegrade.jagr.api.rubric.Grader;
import org.sourcegrade.jagr.api.rubric.JUnitTestRef;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;
import org.tudalgo.algoutils.tutor.general.assertions.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines the utility methods for the testing purposes for the tasks of the assignment H10.
 *
 * @author Nhan Huynh
 */
public class TutorUtils {

    /**
     * The probability to always add new element to the list.
     */
    public static final Probability PROBABILITY_ALWAYS_ADD = () -> true;

    /**
     * Don't let anyone instantiate this class.
     */
    private TutorUtils() {
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
     * Creates a base context builder for the given skip list containing all information about the lost.
     *
     * @param list    the skip list to create the context builder for
     * @param subject the subject of the context
     *
     * @return the context builder for the given skip list
     */
    public static Context.Builder<?> contextBuilderList(SkipList<Integer> list, Object subject) {
        return Assertions2.contextBuilder()
            .subject(subject)
            .add("Comparator", list.cmp)
            .add("Max Level", list.getMaxLevel())
            .add("Probability", list.getProbability())
            .add("Elements", list)
            .add("Size", list.size)
            .add("Current Max Level", list.getCurrentMaxLevel());
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

        for (ListItem<ExpressNode<T>> currentLevel = list.head; currentLevel != null; currentLevel = currentLevel.key.down) {
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
        SkipList<T> copy = new SkipList<>(list.cmp, list.getMaxLevel(), list.getProbability());
        copy.head = head;
        copy.currentMaxLevel = list.currentMaxLevel;
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
