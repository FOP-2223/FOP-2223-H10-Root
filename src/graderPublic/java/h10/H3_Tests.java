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

import static h10.TutorUtils.contextBuilderList;
import static h10.TutorUtils.listItemAsList;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertEquals;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertNotNull;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertNull;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertSame;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.contextBuilder;

/**
 * Defines the public JUnit test cases related to the task H3.
 *
 * @author Nhan Huynh
 * @see SkipList#remove(Object)
 */
@TestForSubmission
@DisplayName("H3")
@SuppressWarnings("unchecked")
public class H3_Tests {

    /**
     * Creates a pre context for the given list operation {@link SkipList#remove(Object)}.
     *
     * @param list the list to execute the operation on
     * @param key  the element to search for
     *
     * @return the pre context for the given list operation
     */
    private static Context contextPre(SkipList<Integer> list, Integer key) {
        return contextBuilderList(list, "add(Object)")
            .add("Method", "add(Object)")
            .add("Element to remove", key)
            .build();
    }

    /**
     * Creates a post context for the given list operation {@link SkipList#add(Object)}. This context extend a
     * context by adding the modification results to the context.
     *
     * @param list the list to execute the operation on
     *
     * @return the post context for the given list operation
     */
    private static Context contextPost(Context preContext, SkipList<Integer> list) {
        return contextBuilder().add(preContext)
            .add("Elements after removal", list)
            .add("Size after removal", list.size)
            .add("Current Max Level after removal", list.getCurrentMaxLevel())
            .build();
    }

    /**
     * Tests if the {@link SkipList#remove(Object)} method sets the size of the list correctly.
     *
     * <p>The parameters are read from the json file with the following structure:
     *
     * <pre>{@code
     *     {
     *         "list": {
     *             "levels": 2D array of integers,
     *             ("maxLevel": integer)
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

            Context context = contextPre(list, key);
            list.remove(key);
            context = contextPost(context, list);

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
     * Tests if the {@link SkipList#remove(Object)} method sets the current max level of the list correctly.
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
     *         "currentMaxLevel": integer
     *     }
     * }</pre>
     *
     * @param object          the list to test
     * @param key             the element to add
     * @param currentMaxLevel the expected current max level of the list
     */
    @DisplayName("18 | Methode setzt die aktuelle Maximum Level korrekt.")
    @ParameterizedTest(name = "Test {index}: Aktuelle Höchstebene der Liste nach dem Entfernen von {1} ist {2}.")
    @JsonClasspathSource("h3/current_max_level.json")
    public void testRemoveCurrentMaxLevel(
        @Property("list") @ConvertWith(SkipListConverter.class) Object object,
        @Property("key") Integer key,
        @Property("currentMaxLevel") int currentMaxLevel) {
        SkipList<Integer> list = (SkipList<Integer>) object;

        Context context = contextPre(list, key);
        list.remove(key);
        context = contextPost(context, list);


        assertEquals(
            currentMaxLevel,
            list.getCurrentMaxLevel(),
            context,
            result -> String.format("The call of the method remove(%s) possibly modified the current max level to %s, "
                + "but expected %s.", key, result.object(), currentMaxLevel)
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
     *             ("maxLevel": integer)
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

        Context context = contextPre(list, key);
        list.remove(key);
        context = contextPost(context, list);

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
     *             ("maxLevel": integer)
     *         }
     *         "key": integer
     *     }
     * }</pre>
     *
     * @param object          the list to test
     * @param key             the element to add
     * @param currentMaxLevel the expected current max level of the list after removals
     */
    @DisplayName("20 | Methode entfernt Ebenen mit einem Element korrekt.")
    @ParameterizedTest(name = "Test {index}: Entfernung von Listen mit einem Element {1}.")
    @JsonClasspathSource("h3/single_element_lists.json")
    public void testRemoveSingleElementLists(
        @Property("list") @ConvertWith(SkipListConverter.class) Object object,
        @Property("key") Integer key,
        @Property("currentMaxLevel") int currentMaxLevel) {
        SkipList<Integer> list = (SkipList<Integer>) object;
        List<List<ListItem<ExpressNode<Integer>>>> itemRefs = listItemAsList(list.head);
        int currentMaxLevelBefore = list.getCurrentMaxLevel();

        Context context = contextPre(list, key);
        list.remove(key);
        context = contextPost(context, list);

        // Starting level to test
        int start = currentMaxLevelBefore - currentMaxLevel;
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
     *             ("maxLevel": integer)
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

        Context context = contextPre(list, key);
        list.remove(key);
        context = contextPost(context, list);

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
