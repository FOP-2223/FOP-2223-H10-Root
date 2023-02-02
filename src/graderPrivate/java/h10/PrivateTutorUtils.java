package h10;

import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;
import org.tudalgo.algoutils.tutor.general.assertions.Context;

import java.util.List;

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

}
