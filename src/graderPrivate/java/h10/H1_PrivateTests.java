package h10;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.junitpioneer.jupiter.json.Property;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.conversion.ArrayConverter;

import static h10.PublicTutorUtils.contextH1;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertEquals;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertTrue;

/**
 * Defines the private JUnit test cases related to the task H1.
 *
 * @author Nhan Huynh
 * @see SkipList#contains(Object)
 */
@DisplayName("H1")
@TestForSubmission
@SuppressWarnings("unchecked")
public final class H1_PrivateTests {

    /**
     * Tests if the {@link SkipList#contains(Object)} method returns {@code true} if the given element is in the list
     * and the number of comparisons to find the element is correct.
     *
     * @param object      the list to test
     * @param keys        the element to search for
     * @param comparisons the expected number of comparisons
     */
    private static void assertComparisons(Object object, Integer[] keys, Integer[] comparisons) {
        TutorSkipList<Integer> list = (TutorSkipList<Integer>) object;
        for (int i = 0; i < keys.length; i++) {
            Integer key = keys[i];
            Integer comparison = comparisons[i];
            Context context = contextH1(list, key);
            assertTrue(
                list.contains(key),
                context,
                result -> String.format("The call of the method contains(%s) returned %s, but the list contains the "
                    + "element %s.", key, result.object(), key)
            );
            assertEquals(
                comparison,
                list.getComparisonCount(),
                context,
                result -> String.format("The call of the method contains(%s) required %s comparisons, but the expected "
                    + "number of comparisons is %s.", key, result.object(), comparison)
            );
        }
    }

    /**
     * Tests if the {@link SkipList#contains(Object)} method returns {@code true} if the given element is in the list
     * on the first level and if the number of comparisons to find the element is correct.
     *
     * <p>The elements will be searched between the first and last level only.
     *
     * <p>The parameters are read from the json file with the following structure:
     * <pre>{@code
     *     {
     *         "list": {
     *             "levels": 2D array of integers,
     *             ("maxHeight": integer)
     *         }
     *         "keys": array of integers,
     *         "comparisons": array of integers
     *     }
     * }</pre>
     *
     * @param object      the list to test
     * @param keys        the element to search for
     * @param comparisons the expected number of comparisons
     */
    @DisplayName("03 | Methode findet die Elemente mit einer minimalen Anzahl an Vergleichen in der obersten Ebene.")
    @ParameterizedTest(name = "Test {index}: Elemente {1} mit minimalen Vergleichen {2}.")
    @JsonClasspathSource("h1/first_level.json")
    public void testContainsFirstLevel(
        @Property("list") @ConvertWith(TutorSkipListConverter.class) Object object,
        @Property("keys") @ConvertWith(ArrayConverter.Auto.class) Integer[] keys,
        @Property("comparisons") @ConvertWith(ArrayConverter.Auto.class) Integer[] comparisons
    ) {
        assertComparisons(object, keys, comparisons);
    }

    /**
     * Tests if the {@link SkipList#contains(Object)} method returns {@code true} if the given element is in the list
     * between the first and the last level and if the number of comparisons to find the element is correct.
     *
     * <p>The elements will be searched between the first and last level only.
     *
     * <p>The parameters are read from the json file with the following structure:
     * <pre>{@code
     *     {
     *         "list": {
     *             "levels": 2D array of integers,
     *             ("maxHeight": integer)
     *         }
     *         "keys": array of integers,
     *         "comparisons": array of integers
     *     }
     * }</pre>
     *
     * @param object      the list to test
     * @param keys        the element to search for
     * @param comparisons the expected number of comparisons
     */
    @DisplayName("04 | Methode findet die Elemente mit einer minimalen Anzahl an Vergleichen in den Zwischenebenen.")
    @ParameterizedTest(name = "Test {index}: Elemente {1} mit minimalen Vergleichen {2}.")
    @JsonClasspathSource("h1/between_levels.json")
    public void testContainsBetweenLevels(
        @Property("list") @ConvertWith(TutorSkipListConverter.class) Object object,
        @Property("keys") @ConvertWith(ArrayConverter.Auto.class) Integer[] keys,
        @Property("comparisons") @ConvertWith(ArrayConverter.Auto.class) Integer[] comparisons
    ) {
        assertComparisons(object, keys, comparisons);
    }

    /**
     * Tests if the {@link SkipList#contains(Object)} method returns {@code true} if the given element is in the list
     * on the last level and if the number of comparisons to find the element is correct.
     *
     * <p>The elements will be searched between the first and last level only.
     *
     * <p>The parameters are read from the json file with the following structure:
     * <pre>{@code
     *     {
     *         "list": {
     *             "levels": 2D array of integers,
     *             ("maxHeight": integer)
     *         }
     *         "keys": array of integers,
     *         "comparisons": array of integers
     *     }
     * }</pre>
     *
     * @param object      the list to test
     * @param keys        the element to search for
     * @param comparisons the expected number of comparisons
     */
    @DisplayName("05 | Methode findet die Elemente mit einer minimalen Anzahl an Vergleichen in der untersten Ebene.")
    @ParameterizedTest(name = "Test {index}: Elemente {1} mit minimalen Vergleichen {2}.")
    @JsonClasspathSource("h1/lowest_level.json")
    public void testContainsLowestLevel(
        @Property("list") @ConvertWith(TutorSkipListConverter.class) Object object,
        @Property("keys") @ConvertWith(ArrayConverter.Auto.class) Integer[] keys,
        @Property("comparisons") @ConvertWith(ArrayConverter.Auto.class) Integer[] comparisons
    ) {
        assertComparisons(object, keys, comparisons);
    }

}
