package h10;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.junitpioneer.jupiter.json.Property;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.conversion.ArrayConverter;

import static h10.PublicTutorUtils.contextH1;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertFalse;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertTrue;

/**
 * Defines the public JUnit test cases related to the task H1.
 *
 * @author Nhan Huynh
 * @see SkipList#contains(Object)
 */
@DisplayName("H1")
@TestForSubmission
@SuppressWarnings("unchecked")
public final class H1_PublicTests {

    /**
     * Tests if the {@link SkipList#contains(Object)} method returns {@code true} if the given element is in list.
     *
     * <p>The parameters are read from the json file with the following structure:
     *
     * <pre>{@code
     *     {
     *         "list": {
     *             "levels": 2D array of integers,
     *             ("maxHeight": integer)
     *         }
     *         "keys": array of integers
     *     }
     * }</pre>
     *
     * @param object the list to test
     * @param keys   the element to search for
     */
    @DisplayName("01 | Methode funktoniert für Elemente, die in der Liste sind, korrekt.")
    @ParameterizedTest(name = "Test {index}: Elemente: {1} werden gefunden.")
    @JsonClasspathSource("h1/true_cases.json")
    public void testContainsTrue(
        @Property("list") @ConvertWith(SkipListConverter.class) Object object,
        @Property("keys") @ConvertWith(ArrayConverter.Auto.class) Integer[] keys) {
        SkipList<Integer> list = (SkipList<Integer>) object;
        for (Integer key : keys) {
            assertTrue(
                list.contains(key),
                contextH1(list, key),
                result -> String.format("The call of the method contains(%s) returned %s, but the list contains the "
                    + "element %s.", key, result.object(), key)
            );
        }
    }

    /**
     * Tests if the {@link SkipList#contains(Object)} method returns {@code false} if the given element is not in list.
     *
     * <p>The parameters are read from the json file with the following structure:
     *
     * <pre>{@code
     *     {
     *         "list": {
     *             "levels": 2D array of integers,
     *             ("maxHeight": integer)
     *         }
     *         "keys": array of integers
     *     }
     * }</pre>
     *
     * @param object the list to test
     * @param keys   the element to search for
     */
    @DisplayName("02 | Methode funktoniert für Elemente, die nicht in der Liste sind, korrekt.")
    @ParameterizedTest(name = "Test {index}: Elemente: {1} werden nicht gefunden.")
    @JsonClasspathSource("h1/false_cases.json")
    public void testContainsFalse(
        @Property("list") @ConvertWith(SkipListConverter.class) Object object,
        @Property("keys") @ConvertWith(ArrayConverter.Auto.class) Integer[] keys) {
        SkipList<Integer> list = (SkipList<Integer>) object;
        for (Integer key : keys) {
            assertFalse(
                list.contains(key),
                contextH1(list, key),
                result -> String.format("The call of the method contains(%s) returned %s, but the list contains the "
                    + "element %s.", key, result.object(), key)
            );
        }
    }

}
