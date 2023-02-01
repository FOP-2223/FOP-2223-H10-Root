package h10;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.junitpioneer.jupiter.json.Property;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.conversion.ArrayConverter;

import java.util.List;

import static h10.PrivateTutorUtils.assertComparisons;
import static h10.PrivateTutorUtils.assertNoConstructorCalls;
import static h10.PrivateTutorUtils.convert;
import static h10.PublicTutorUtils.contextH1;
import static h10.PublicTutorUtils.listItemAsList;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertTrue;

/**
 * Defines the private JUnit test cases related to the task H1.
 *
 * @author Nhan Huynh
 * @see SkipList#contains(Object)
 */
@DisplayName("H1")
@TestForSubmission
public class H1_PrivateTests {

    /**
     * Asserts that the call of the method contains() returns true and the path of comparisons is correct.
     *
     * @param object      the skip list to be tested
     * @param key         the key to be searched
     * @param comparisons the expected comparisons path
     */
    private static void assertContains(Object object, Integer key, Integer[][] comparisons) {
        VisitorSkipList<Integer> list = convert(object);
        List<List<ListItem<ExpressNode<VisitorNode<Integer>>>>> nodes = listItemAsList(list.head);
        VisitorNode<Integer> node = new VisitorNode<>(key);
        Context context = contextH1(list, node);
        assertTrue(
            list.contains(node),
            context,
            result -> String.format("The call of the method contains(%s) returned %s instead of true.",
                key, result.object())
        );
        assertComparisons(nodes, comparisons, context);
    }

    /**
     * Tests the mandatory requirements of the task H1.
     */
    @Test
    @DisplayName("Verbindliche Anforderungen")
    public void testRequirements() {
        assertNoConstructorCalls("contains");
    }


    /**
     * Tests if the {@link SkipList#contains(Object)} method returns {@code true} if the given element is in the list
     * on the first level and if the path of the comparisons to find the element is correct.
     *
     * <p>The parameters are read from the json file with the following structure:
     * <pre>{@code
     *     {
     *         "list": {
     *             "levels": 2D array of integers,
     *             ("maxHeight": integer)
     *         }
     *         "key": integer,
     *         "comparisons": 2D array of integers
     *     }
     * }</pre>
     *
     * @param object      the list to test
     * @param key         the element to search for
     * @param comparisons the expected comparisons path
     */
    @DisplayName("03 | Methode findet die Elemente mit einer minimalen Anzahl an Vergleichen in der obersten Ebene.")
    @ParameterizedTest(name = "Test {index}: Element {1} mit minimalen Vergleichen {2}.")
    @JsonClasspathSource({"h1/first/22.json", "h1/first/59.json", "h1/first/70.json", "h1/first/80.json"})
    public void testContainsFirstLevel(
        @Property("list") @ConvertWith(VisitorSkipListConverter.class) Object object,
        @Property("key") Integer key,
        @Property("comparisons") @ConvertWith(ArrayConverter.Auto.class) Integer[][] comparisons
    ) {
        assertContains(object, key, comparisons);
    }

    /**
     * Tests if the {@link SkipList#contains(Object)} method returns {@code true} if the given element is in the list
     * between the first and the last level and if the path of the comparisons to find the element is correct.
     *
     * <p>The parameters are read from the json file with the following structure:
     * <pre>{@code
     *     {
     *         "list": {
     *             "levels": 2D array of integers,
     *             ("maxHeight": integer)
     *         }
     *         "keys": array of integers,
     *         "comparisons": 2D array of integers
     *     }
     * }</pre>
     *
     * @param object      the list to test
     * @param key         the element to search for
     * @param comparisons the expected comparisons path
     */
    @DisplayName("04 | Methode findet die Elemente mit einer minimalen Anzahl an Vergleichen in den Zwischenebenen.")
    @ParameterizedTest(name = "Test {index}: Elemente {1} mit minimalen Vergleichen {2}.")
    @JsonClasspathSource({
        "h1/between/30.json", "h1/between/148.json", "h1/between/151.json", "h1/between/33.json", "h1/between/34.json",
        "h1/between/37.json", "h1/between/51.json", "h1/between/160.json", "h1/between/179.json"
    })
    public void testContainsBetweenLevels(
        @Property("list") @ConvertWith(VisitorSkipListConverter.class) Object object,
        @Property("key") Integer key,
        @Property("comparisons") @ConvertWith(ArrayConverter.Auto.class) Integer[][] comparisons
    ) {
        assertContains(object, key, comparisons);
    }

    /**
     * Tests if the {@link SkipList#contains(Object)} method returns {@code true} if the given element is in the list
     * on the last level and if the path of the comparisons to find the element is correct.
     *
     * <p>The parameters are read from the json file with the following structure:
     * <pre>{@code
     *     {
     *         "list": {
     *             "levels": 2D array of integers,
     *             ("maxHeight": integer)
     *         }
     *         "key": integer,
     *         "comparisons": 2D array of integers
     *     }
     * }</pre>
     *
     * @param object      the list to test
     * @param key         the element to search for
     * @param comparisons the expected comparisons path
     */
    @DisplayName("05 | Methode findet die Elemente mit einer minimalen Anzahl an Vergleichen in der untersten Ebene.")
    @ParameterizedTest(name = "Test {index}: Elemente {1} mit minimalen Vergleichen {2}.")
    @JsonClasspathSource({
        "h1/lowest/13.json", "h1/lowest/21.json", "h1/lowest/44.json", "h1/lowest/47.json", "h1/lowest/57.json",
        "h1/lowest/65.json", "h1/lowest/75.json", "h1/lowest/90.json", "h1/lowest/132.json", "h1/lowest/139.json",
        "h1/lowest/147.json", "h1/lowest/162.json", "h1/lowest/196.json", "h1/lowest/198.json"
    })
    public void testContainsLowestLevel(
        @Property("list") @ConvertWith(VisitorSkipListConverter.class) Object object,
        @Property("key") Integer key,
        @Property("comparisons") @ConvertWith(ArrayConverter.Auto.class) Integer[][] comparisons
    ) {
        assertContains(object, key, comparisons);
    }
}
