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

import static h10.PublicTutorUtils.contextH3;
import static h10.PublicTutorUtils.listItemAsList;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertEquals;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertNotNull;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertNull;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertSame;

/**
 * Defines the public JUnit test cases related to the task H3.
 *
 * @author Nhan Huynh
 * @see SkipList#remove(Object)
 */
@DisplayName("H3")
@TestForSubmission
@SuppressWarnings("unchecked")
public final class H3_PublicTests {

    /**
     * Tests if the {@link SkipList#remove(Object)} method sets the size of the list correctly.
     *
     * <p>The parameters are read from the json file with the following structure:
     *
     * <pre>{@code
     *     {
     *         "list": {
     *             "levels": 2D array of integers,
     *             ("maxHeight": integer)
     *         }
     *         "keys": array of integers,
     *         "sizes": array of integers,
     *     }
     * }</pre>
     *
     * @param object the list to test
     * @param keys   the elements to add
     * @param sizes  the expected sizes of the list
     */
    @DisplayName("17 | Methode setzt die Größe korrekt.")
    @ParameterizedTest(name = "Test {index}: Größe der Liste nach dem Entfernen von {1} ist {2}.")
    @JsonClasspathSource("h3/size.json")
    public void testRemoveSize(
        @Property("list") @ConvertWith(SkipListConverter.class) Object object,
        @Property("keys") @ConvertWith(ArrayConverter.Auto.class) Integer[] keys,
        @Property("sizes") @ConvertWith(ArrayConverter.Auto.class) Integer[] sizes) {
        SkipList<Integer> list = (SkipList<Integer>) object;
        for (int i = 0; i < keys.length; i++) {
            Integer key = keys[i];
            Integer size = sizes[i];

            Context context = contextH3(list, key);

            assertEquals(
                size,
                list.size(),
                context,
                result -> String.format("The call of the method remove(%s) possibly modified the size to %s, but "
                    + "expected %s.", key, result.object(), size)
            );
        }
    }

    /**
     * Tests if the {@link SkipList#remove(Object)} method sets the height of the list correctly.
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
     *         "height": integer
     *     }
     * }</pre>
     *
     * @param object          the list to test
     * @param key             the element to add
     * @param height the expected height of the list
     */
    @DisplayName("18 | Methode setzt die aktuelle Maximum Level korrekt.")
    @ParameterizedTest(name = "Test {index}: Aktuelle Höchstebene der Liste nach dem Entfernen von {1} ist {2}.")
    @JsonClasspathSource("h3/current_max_level.json")
    public void testRemoveCurrentMaxLevel(
        @Property("list") @ConvertWith(SkipListConverter.class) Object object,
        @Property("key") Integer key,
        @Property("height") int height) {
        SkipList<Integer> list = (SkipList<Integer>) object;

        Context context = contextH3(list, key);

        assertEquals(
            height,
            list.getHeight(),
            context,
            result -> String.format("The call of the method remove(%s) possibly modified the height to %s, "
                + "but expected %s.", key, result.object(), height)
        );
    }

    /**
     * Tests if the {@link SkipList#remove(Object)} method removes a single element list correctly.
     *
     * <p>The parameters are read from the json file with the following structure:
     *
     * <pre>{@code
     *     {
     *         "list": {
     *             "levels": 2D array of integers,
     *             ("maxHeight": integer)
     *         }
     *         "key": integer
     *     }
     * }</pre>
     *
     * @param object the list to test
     * @param key    the element to add
     */
    @DisplayName("19 | Methode entfernt eine Ebene mit einem Element korrekt.")
    @ParameterizedTest(name = "Test {index}: Entfernung von einer Liste mit einem Element {1}.")
    @JsonClasspathSource("h3/single_element_list_head.json")
    public void testRemoveSingleElementListHead(
        @Property("list") @ConvertWith(SkipListConverter.class) Object object,
        @Property("key") Integer key) {
        SkipList<Integer> list = (SkipList<Integer>) object;

        Context context = contextH3(list, key);

        assertNull(
            list.head,
            context,
            result -> String.format("The call of the method remove(%s) possibly modified the head to %s, but expected "
                + "null since we are removing a list containing one element.", key, result.object())
        );
    }

    /**
     * Tests if the {@link SkipList#remove(Object)} method removes a single element lists correctly.
     *
     * <p>The parameters are read from the json file with the following structure:
     *
     * <pre>{@code
     *     {
     *         "list": {
     *             "levels": 2D array of integers,
     *             ("maxHeight": integer)
     *         }
     *         "key": integer
     *     }
     * }</pre>
     *
     * @param object          the list to test
     * @param key             the element to add
     * @param height the expected height of the list after removals
     */
    @DisplayName("20 | Methode entfernt Ebenen mit einem Element korrekt.")
    @ParameterizedTest(name = "Test {index}: Entfernung von Listen mit einem Element {1}.")
    @JsonClasspathSource("h3/single_element_lists.json")
    public void testRemoveSingleElementLists(
        @Property("list") @ConvertWith(SkipListConverter.class) Object object,
        @Property("key") Integer key,
        @Property("height") int height) {
        SkipList<Integer> list = (SkipList<Integer>) object;
        List<List<ListItem<ExpressNode<Integer>>>> itemRefs = listItemAsList(list.head);
        int currentMaxLevelBefore = list.getHeight();

        Context context = contextH3(list, key);

        // Starting level to test
        int start = currentMaxLevelBefore - height;
        int i = 0;
        for (ListItem<ExpressNode<Integer>> walker = list.head; walker != null; walker = walker.key.down, i++) {
            ListItem<ExpressNode<Integer>> expectedNode = itemRefs.get(start + i).get(0);
            assertSame(
                expectedNode,
                walker,
                context,
                result -> String.format("The call of the method remove(%s) possibly modified the head to %s, but "
                    + "expected %s.", key, result.object(), expectedNode)
            );
        }
    }

    /**
     * Tests if the {@link SkipList#remove(Object)} method removes the first element correctly.
     *
     * <p>The parameters are read from the json file with the following structure:
     *
     * <pre>{@code
     *     {
     *         "list": {
     *             "levels": 2D array of integers,
     *             ("maxHeight": integer)
     *         }
     *     }
     * }</pre>
     *
     * @param object the list to test
     */
    @DisplayName("21 | Methode entfernt das erste Element korrekt.")
    @ParameterizedTest(name = "Test {index}: Entfernung des ersten Elements.")
    @JsonClasspathSource("h3/first_element.json")
    public void testRemoveFirstElement(
        @Property("list") @ConvertWith(SkipListConverter.class) Object object) {
        SkipList<Integer> list = (SkipList<Integer>) object;
        List<List<ListItem<ExpressNode<Integer>>>> itemRefs = listItemAsList(list.head);
        Integer key = itemRefs.get(itemRefs.size() - 1).get(1).key.value;

        Context context = contextH3(list, key);

        assertNotNull(
            list.head,
            context,
            result -> String.format("The call of the method remove(%s) possibly modified the head to %s, but expected "
                + "a non-null value.", key, result.object())
        );
        assert list.head != null;
        ListItem<ExpressNode<Integer>> expectedNode = itemRefs.get(itemRefs.size() - 1).get(2);
        assertSame(
            expectedNode,
            itemRefs.get(itemRefs.size() - 1).get(0).next,
            context,
            result -> String.format("The call of the method remove(%s) possibly modified the head.next "
                + "(head=sentinel) to %s, but expected %s.", key, result.object(), expectedNode)
        );
    }

}
