package h10;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.junitpioneer.jupiter.json.Property;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.conversion.ArrayConverter;

import java.util.List;

import static h10.H2_PublicTests.contextPost;
import static h10.H2_PublicTests.contextPre;
import static h10.PublicTutorUtils.PROBABILITY_ALWAYS_ADD;
import static h10.PublicTutorUtils.listItemAsList;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertEquals;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertNotNull;

/**
 * Defines the private JUnit test cases related to the task H2.
 *
 * @author Nhan Huynh
 * @see SkipList#add(Object)
 */
@DisplayName("H2")
@TestForSubmission
@SuppressWarnings({"unchecked", "DuplicatedCode"})
public class H2_PrivateTests {

    /**
     * Tests if the {@link SkipList#add(Object)}  method adds element on  levels correctly and if the number of
     * comparison to find the correct insertion point is correct.
     *
     * @param object                the list to test
     * @param key                   the element to add
     * @param comparison            the number of comparisons to find the correct insertion point
     * @param numberOfElementsLevel the number of elements on each level
     * @param refs                  the indices of the added elements
     */
    @SuppressWarnings("unchecked")
    private void assertAddOnLevels(Object object, Integer key, int comparison, Integer[] numberOfElementsLevel,
                                   Integer[] refs) {
        TutorSkipList<Integer> list = (TutorSkipList<Integer>) object;
        list.setProbability(PROBABILITY_ALWAYS_ADD);

        Context context = contextPre(list, key);
        list.add(key);
        context = contextPost(context, list);

        assertEquals(
            comparison,
            list.getComparisonCount(),
            context,
            result -> String.format("The call to add(%d) should have made %d comparisons, but made %d instead.",
                key, comparison, result.object())
        );
        List<List<ListItem<ExpressNode<Integer>>>> itemRefs = listItemAsList(list.head);

        for (int i = 0; i < numberOfElementsLevel.length; i++) {
            int level = i;
            List<ListItem<ExpressNode<Integer>>> node = itemRefs.get(i);
            assertEquals(
                numberOfElementsLevel[i] + 1,
                node.size(),
                context,
                result -> String.format("The call to add(%d) should have contain %d elements on level %d, but given %d "
                    + "instead.", key, numberOfElementsLevel[level] + 1, level, result.object())
            );
            assertEquals(
                key,
                node.get(refs[i] + 1).key.value,
                context,
                result -> String.format("The call to add(%d) should have added %d on level %d, but added %d instead.",
                    key, key, level, result.object())
            );
        }
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
     *             ("maxLevel": integer)
     *         }
     *         "key": integer,
     *         "comparison": integer,
     *         "numberOfElementsLevel": array of integers,
     *         "refs": array of integers
     *     }
     * }</pre>
     *
     * @param object                the list to test
     * @param key                   the element to add
     * @param comparison            the number of comparisons to find the correct insertion point
     * @param numberOfElementsLevel the number of elements on each level
     * @param refs                  the indices of the added elements
     */
    @DisplayName("12 | Methode erstellt neue Ebenen korrekt.")
    @ParameterizedTest(name = "Test {index}: Einfügen von Element [1} mit minimalen Vergleich {2}.")
    @JsonClasspathSource("h2/create_new_levels.json")
    public void testAddCreateNewLevels(
        @Property("list") @ConvertWith(TutorSkipListConverter.class) Object object,
        @Property("key") Integer key,
        @Property("comparison") int comparison,
        @Property("numberOfElementsLevel") @ConvertWith(ArrayConverter.Auto.class) Integer[] numberOfElementsLevel,
        @Property("refs") @ConvertWith(ArrayConverter.Auto.class) Integer[] refs
    ) {
        assertAddOnLevels(object, key, comparison, numberOfElementsLevel, refs);
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
     *             ("maxLevel": integer)
     *         }
     *         "key": integer,
     *         "comparison": integer,
     *         "numberOfElementsLevel": array of integers,
     *         "refs": array of integers
     *     }
     * }</pre>
     *
     * @param object                the list to test
     * @param key                   the element to add
     * @param comparison            the number of comparisons to find the correct insertion point
     * @param numberOfElementsLevel the number of elements on each level
     * @param refs                  the indices of the added elements
     */
    @DisplayName("13 | Methode fügt Element in vorhandenen Ebenen korrekt.")
    @ParameterizedTest(name = "Test {index}: Einfügen von Element [1} mit minimalen Vergleich {2}.")
    @JsonClasspathSource("h2/on_each_level.json")
    public void testAddOnEachLevel(
        @Property("list") @ConvertWith(TutorSkipListConverter.class) Object object,
        @Property("key") Integer key,
        @Property("comparison") int comparison,
        @Property("numberOfElementsLevel") @ConvertWith(ArrayConverter.Auto.class) Integer[] numberOfElementsLevel,
        @Property("refs") @ConvertWith(ArrayConverter.Auto.class) Integer[] refs
    ) {
        assertAddOnLevels(object, key, comparison, numberOfElementsLevel, refs);
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
     *             ("maxLevel": integer)
     *         }
     *         "key": integer,
     *         "compare": integer,
     *         "numberOfElementsLevel": array of integers,
     *         "refs": array of integers
     *     }
     * }</pre>
     *
     * @param object                the list to test
     * @param key                   the element to add
     * @param comparison            the number of comparisons to find the correct insertion point
     * @param numberOfElementsLevel the number of elements on each level
     * @param refs                  the indices of the added elements
     */
    @DisplayName("14 | Methode fügt Element in allen Ebenen korrekt.")
    @ParameterizedTest(name = "Test {index}: Einfügen von Element [1} mit minimalen Vergleich {2}.")
    @JsonClasspathSource("h2/on_each_level_max.json")
    public void testAddOnEachLevelMax(
        @Property("list") @ConvertWith(TutorSkipListConverter.class) Object object,
        @Property("key") Integer key,
        @Property("comparison") int comparison,
        @Property("numberOfElementsLevel") @ConvertWith(ArrayConverter.Auto.class) Integer[] numberOfElementsLevel,
        @Property("refs") @ConvertWith(ArrayConverter.Auto.class) Integer[] refs
    ) {
        assertAddOnLevels(object, key, comparison, numberOfElementsLevel, refs);
    }

    /**
     * Tests if the {@link SkipList#add(Object)}  method adds elements on the beginning and at the end correctly.
     *
     * <p>The parameters are read from the json file with the following structure:
     *
     * <pre>{@code
     *     {
     *         "list": {
     *             "levels": 2D array of integers,
     *             ("maxLevel": integer)
     *         }
     *         "key": integer,
     *         "compare": integer,
     *         "numberOfElementsLevel": array of integers,
     *         "refs": array of integers
     *     }
     * }</pre>
     *
     * @param object                the list to test
     * @param key                   the element to add
     * @param comparison            the number of comparisons to find the correct insertion point
     * @param numberOfElementsLevel the number of elements on each level
     * @param refs                  the indices of the added elements
     */
    @DisplayName("15 | Methode fügt Element vorne/hinten korrekt.")
    @ParameterizedTest(name = "Test {index}: Einfügen von Element [1} mit minimalen Vergleich {2}.")
    @JsonClasspathSource({"h2/first.json", "h2/last.json"})
    public void testAddFirstLast(
        @Property("list") @ConvertWith(TutorSkipListConverter.class) Object object,
        @Property("key") Integer key,
        @Property("comparison") int comparison,
        @Property("numberOfElementsLevel") @ConvertWith(ArrayConverter.Auto.class) Integer[] numberOfElementsLevel,
        @Property("refs") @ConvertWith(ArrayConverter.Auto.class) Integer[] refs
    ) {
        assertAddOnLevels(object, key, comparison, numberOfElementsLevel, refs);
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
     *             ("maxLevel": integer)
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
        SkipList<Integer> list = (SkipList<Integer>) object;
        list.setProbability(PROBABILITY_ALWAYS_ADD);

        Context context = contextPre(list, key);
        list.add(key);
        context = contextPost(context, list);

        List<List<ListItem<ExpressNode<Integer>>>> itemRefs = listItemAsList(list.head);
        for (int i = 0; i < refs.length; i++) {
            int level = i;
            ListItem<ExpressNode<Integer>> node = itemRefs.get(i).get(refs[i] + 1);
            assert node != null;
            if (i != 0) {
                assertNotNull(
                    node.key.up,
                    context,
                    result -> String.format("The call of add(%d) should set the up reference of the node on level %d, "
                        + " but no up reference exists.", key, level)
                );
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
                assertNotNull(
                    node.key.down,
                    context,
                    result -> String.format("The call of add(%d) should set the down reference of the node on level "
                        + "%d, but no down reference exists.", key, level)
                );
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
