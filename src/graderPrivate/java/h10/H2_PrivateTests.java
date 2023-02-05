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
import static h10.PrivateTutorUtils.assertUseOnlyConstructorCalls;
import static h10.PrivateTutorUtils.convert;
import static h10.PublicTutorUtils.PROBABILITY_ALWAYS_ADD;
import static h10.PublicTutorUtils.contextH2;
import static h10.PublicTutorUtils.listItemAsList;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertEquals;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertNotNull;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertTrue;

/**
 * Defines the private JUnit test cases related to the task H2.
 *
 * @author Nhan Huynh
 * @see SkipList#add(Object)
 */
@DisplayName("H2")
@TestForSubmission
public class H2_PrivateTests {

    /**
     * Tests whether the elements before insertion are still in the list after insertion.
     *
     * @param before  the list before insertion
     * @param after   the list after insertion
     * @param context the context of the test
     */
    private void assertNodesExists(
        List<List<ListItem<ExpressNode<VisitorNode<Integer>>>>> before,
        List<List<ListItem<ExpressNode<VisitorNode<Integer>>>>> after,
        Context context
    ) {
        for (List<ListItem<ExpressNode<VisitorNode<Integer>>>> level : before) {
            ListItem<ExpressNode<VisitorNode<Integer>>> first = level.get(0);
            List<ListItem<ExpressNode<VisitorNode<Integer>>>> current = after.stream()
                .filter(list -> list.get(0).equals(level.get(0))).findFirst()
                .orElse(null);
            String name = first.getClass().getName() + "@" + Integer.toHexString(first.hashCode());
            assertNotNull(current, context, result -> String.format("Could not find level with sentinel node %s in the "
                + "list after the call to add(%s).", name, result.object()));
            for (ListItem<ExpressNode<VisitorNode<Integer>>> item : level) {
                assert current != null;
                assertTrue(
                    current.contains(item),
                    context,
                    result -> String.format("The call to add(%s) should not remove any nodes.", item.key.value)
                );
            }
        }
    }

    /**
     * Tests if the {@link SkipList#add(Object)}  method adds element on levels correctly and if the path of
     * comparison to find the correct insertion point is correct.
     *
     * @param object                the list to test
     * @param key                   the element to add
     * @param comparisons           the expected comparisons path
     * @param numberOfElementsLevel the number of elements on each level
     * @param refs                  the indices of the added elements
     */
    private void assertAddOnLevels(Object object, Integer key, Integer[][] comparisons, Integer[] numberOfElementsLevel,
                                   Integer[] refs) {
        VisitorSkipList<Integer> list = convert(object);
        list.setProbability(PROBABILITY_ALWAYS_ADD);
        List<List<ListItem<ExpressNode<VisitorNode<Integer>>>>> nodes = listItemAsList(list.head);
        VisitorNode<Integer> toAdd = new VisitorNode<>(key);
        Context context = contextH2(list, toAdd);
        List<List<ListItem<ExpressNode<VisitorNode<Integer>>>>> nodesAfterAction = listItemAsList(list.head);

        assertComparisons(nodes, comparisons, context);
        assertNodesExists(nodes, nodesAfterAction, context);

        List<List<ListItem<ExpressNode<VisitorNode<Integer>>>>> itemRefs = listItemAsList(list.head);

        for (int i = 0; i < numberOfElementsLevel.length; i++) {
            int level = i;
            List<ListItem<ExpressNode<VisitorNode<Integer>>>> node = itemRefs.get(i);
            assertEquals(
                numberOfElementsLevel[i] + 1,
                node.size(),
                context,
                result -> String.format("The call to add(%s) should have contain %d elements on level %d, but given %d "
                    + "instead.", toAdd, numberOfElementsLevel[level] + 1, level, result.object())
            );
            assertEquals(
                toAdd,
                node.get(refs[i] + 1).key.value,
                context,
                result -> String.format("The call to add(%s) should have added %s on level %d, but added %s instead.",
                    toAdd, toAdd, level, result.object())
            );
        }
    }

    /**
     * Tests the mandatory requirements of the task H2.
     */
    @Test
    @DisplayName("Verbindliche Anforderungen")
    public void testRequirements() {
        assertUseOnlyConstructorCalls("add", ListItem.class, ExpressNode.class);
    }

    /**
     * Tests if the {@link SkipList#add(Object)}  method creates new levels correctly and if the number of comparison
     * to find the correct insertion point is correct.
     *
     * <p>The parameters are read from the json file with the following structure:
     *
     * <pre>{@code
     *     {
     *         "list": {
     *             "levels": 2D array of integers,
     *             ("maxHeight": integer)
     *         }
     *         "key": integer,
     *         "comparisons": 2D array of integers,
     *         "numberOfElementsLevel": array of integers,
     *         "refs": array of integers
     *     }
     * }</pre>
     *
     * @param object                the list to test
     * @param key                   the element to add
     * @param comparisons           the expected comparisons path
     * @param numberOfElementsLevel the number of elements on each level
     * @param refs                  the indices of the added elements
     */
    @DisplayName("12 | Methode erstellt neue Ebenen korrekt.")
    @ParameterizedTest(name = "Test {index}: Einfügen von Element [1} mit minimalen Vergleich {2}.")
    @JsonClasspathSource("h2/create_new_levels.json")
    public void testAddCreateNewLevels(
        @Property("list") @ConvertWith(VisitorSkipListConverter.class) Object object,
        @Property("key") Integer key,
        @Property("comparisons") @ConvertWith(ArrayConverter.Auto.class) Integer[][] comparisons,
        @Property("numberOfElementsLevel") @ConvertWith(ArrayConverter.Auto.class) Integer[] numberOfElementsLevel,
        @Property("refs") @ConvertWith(ArrayConverter.Auto.class) Integer[] refs
    ) {
        assertAddOnLevels(object, key, comparisons, numberOfElementsLevel, refs);
    }

    @DisplayName("13 | Methode fügt Element in vorhandenen Ebenen korrekt.")
    @ParameterizedTest(name = "Test {index}: Einfügen von Element [1} mit minimalen Vergleich {2}.")
    @JsonClasspathSource("h2/on_each_level.json")
    public void testAddOnEachLevel(
        @Property("list") @ConvertWith(VisitorSkipListConverter.class) Object object,
        @Property("key") Integer key,
        @Property("comparisons") @ConvertWith(ArrayConverter.Auto.class) Integer[][] comparisons,
        @Property("numberOfElementsLevel") @ConvertWith(ArrayConverter.Auto.class) Integer[] numberOfElementsLevel,
        @Property("refs") @ConvertWith(ArrayConverter.Auto.class) Integer[] refs
    ) {
        assertAddOnLevels(object, key, comparisons, numberOfElementsLevel, refs);
    }

    /**
     * Tests if the {@link SkipList#add(Object)}  method adds elements on levels correctly and if the number of
     * comparison to find the correct insertion point is correct.
     *
     * <p>The parameters are read from the json file with the following structure:
     *
     * <pre>{@code
     *     {
     *         "list": {
     *             "levels": 2D array of integers,
     *             ("maxHeight": integer)
     *         }
     *         "key": integer,
     *         "comparisons": 2D array of integers,
     *         "numberOfElementsLevel": array of integers,
     *         "refs": array of integers
     *     }
     * }</pre>
     *
     * @param object                the list to test
     * @param key                   the element to add
     * @param comparisons           the expected comparisons path
     * @param numberOfElementsLevel the number of elements on each level
     * @param refs                  the indices of the added elements
     */
    @DisplayName("14 | Methode fügt Element in allen Ebenen korrekt.")
    @ParameterizedTest(name = "Test {index}: Einfügen von Element [1} mit minimalen Vergleich {2}.")
    @JsonClasspathSource("h2/on_each_level_max.json")
    public void testAddOnEachLevelMax(
        @Property("list") @ConvertWith(VisitorSkipListConverter.class) Object object,
        @Property("key") Integer key,
        @Property("comparisons") @ConvertWith(ArrayConverter.Auto.class) Integer[][] comparisons,
        @Property("numberOfElementsLevel") @ConvertWith(ArrayConverter.Auto.class) Integer[] numberOfElementsLevel,
        @Property("refs") @ConvertWith(ArrayConverter.Auto.class) Integer[] refs
    ) {
        assertAddOnLevels(object, key, comparisons, numberOfElementsLevel, refs);
    }

    /**
     * Tests if the {@link SkipList#add(Object)}  method adds elements on levels and create new levels correctly and if
     * the number of comparison to find the correct insertion point is correct.
     *
     * <p>The parameters are read from the json file with the following structure:
     *
     * <pre>{@code
     *     {
     *         "list": {
     *             "levels": 2D array of integers,
     *             ("maxHeight": integer)
     *         }
     *         "key": integer,
     *         "comparisons": 2D array of integers,
     *         "numberOfElementsLevel": array of integers,
     *         "refs": array of integers
     *     }
     * }</pre>
     *
     * @param object                the list to test
     * @param key                   the element to add
     * @param comparisons           the expected comparisons path
     * @param numberOfElementsLevel the number of elements on each level
     * @param refs                  the indices of the added elements
     */
    @DisplayName("15 | Methode fügt Element vorne/hinten korrekt.")
    @ParameterizedTest(name = "Test {index}: Einfügen von Element [1} mit minimalen Vergleich {2}.")
    @JsonClasspathSource({"h2/first.json", "h2/last.json"})
    public void testAddFirstLast(
        @Property("list") @ConvertWith(VisitorSkipListConverter.class) Object object,
        @Property("key") Integer key,
        @Property("comparisons") @ConvertWith(ArrayConverter.Auto.class) Integer[][] comparisons,
        @Property("numberOfElementsLevel") @ConvertWith(ArrayConverter.Auto.class) Integer[] numberOfElementsLevel,
        @Property("refs") @ConvertWith(ArrayConverter.Auto.class) Integer[] refs
    ) {
        assertAddOnLevels(object, key, comparisons, numberOfElementsLevel, refs);
    }

    /**
     * Tests if the {@link SkipList#add(Object)}  method sets the references up and down correctly.
     *
     * <p>The parameters are read from the json file with the following structure:
     *
     * <pre>{@code
     *     {
     *         "list": {
     *             "levels": 2D array of integers,
     *             ("maxHeight": integer)
     *         }
     *         "key": integer,
     *         "refs": array of integers
     *     }
     * }</pre>
     *
     * @param object the list to test
     * @param key    the element to add
     * @param refs   the indices of the added elements
     */
    @DisplayName("16 | Methode setzt die Referenzen nach oben/unten korrekt.")
    @ParameterizedTest(name = "Test {index}: Setzen des Verweise nach oben/unten beim Einfügen von {2}.")
    @JsonClasspathSource("h2/references_up_down.json")
    public void testAddReferencesUpDown(
        @Property("list") @ConvertWith(SkipListConverter.class) Object object,
        @Property("key") Integer key,
        @Property("refs") @ConvertWith(ArrayConverter.Auto.class) Integer[] refs) {
        SkipList<Integer> list = convert(object);
        list.setProbability(PROBABILITY_ALWAYS_ADD);

        Context context = contextH2(list, key);

        List<List<ListItem<ExpressNode<Integer>>>> itemRefs = listItemAsList(list.head);
        for (int i = 0; i < refs.length; i++) {
            int level = i;
            ListItem<ExpressNode<Integer>> node = itemRefs.get(i).get(refs[i] + 1);
            assert node != null;
            if (i != 0) {
                assert node.key.up != null;
                assertEquals(
                    key,
                    node.key.up.key.value,
                    context,
                    result -> String.format("The call of add(%d) should set the up reference of the node on level %d "
                        + "correct, but given %s.", key, level, node.key.up.key.value)
                );
            }
            if (i != refs.length - 1) {
                assert node.key.down != null;
                assertEquals(
                    key,
                    node.key.down.key.value,
                    context,
                    result -> String.format("The call of add(%d) should set the down reference of the node on level %d "
                        + "correct, but given %s.", key, level, node.key.down.key.value)
                );
            }
        }
    }

}
