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

import static h10.PublicTutorUtils.PROBABILITY_ALWAYS_ADD;
import static h10.PublicTutorUtils.contextH2;
import static h10.PublicTutorUtils.copy;
import static h10.PublicTutorUtils.listItemAsList;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertEquals;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertNotNull;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertNull;

/**
 * Defines the public JUnit test cases related to the task H2.
 *
 * @author Nhan Huynh
 * @see SkipList#add(Object)
 */
@DisplayName("H2")
@TestForSubmission
@SuppressWarnings({"unchecked", "DuplicatedCode"})
public final class H2_PublicTests {

    /**
     * Tests if the {@link SkipList#add(Object)} method sets the size of the list correctly.
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
     *         "sizes": array of integers
     *     }
     * }</pre>
     *
     * @param object the list to test
     * @param keys   the elements to add
     * @param sizes  the expected sizes of the list after insertion
     */
    @DisplayName("06 | Methode setzt die Größe korrekt.")
    @ParameterizedTest(name = "Test {index}: Größe der Liste nach dem Einfügen von {1} ist {2}.")
    @JsonClasspathSource("h2/size.json")
    public void testAddSize(
        @Property("list") @ConvertWith(SkipListConverter.class) Object object,
        @Property("keys") @ConvertWith(ArrayConverter.Auto.class) Integer[] keys,
        @Property("sizes") @ConvertWith(ArrayConverter.Auto.class) Integer[] sizes) {
        SkipList<Integer> list = (SkipList<Integer>) object;
        for (int i = 0; i < keys.length; i++) {
            Integer key = keys[i];
            Integer size = sizes[i];

            Context context = contextH2(list, key);

            assertEquals(
                size,
                list.size(),
                context,
                result -> String.format("The call of the method add(%s) possibly modified the size to %s, but "
                    + "expected %s.", key, result.object(), size)
            );
        }
    }

    /**
     * Tests if the {@link SkipList#add(Object)} method sets the height of the list correctly.
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
    @DisplayName("07 | Methode setzt die aktuelle Maximum Level korrekt.")
    @ParameterizedTest(name = "Test {index}: Aktuelle Höchstebene der Liste nach dem Einfügen von {1} ist {2}.")
    @JsonClasspathSource("h2/current_max_level.json")
    public void testAddCurrentMaxLevel(
        @Property("list") @ConvertWith(SkipListConverter.class) Object object,
        @Property("key") Integer key) {
        SkipList<Integer> list = (SkipList<Integer>) object;
        list.setProbability(PROBABILITY_ALWAYS_ADD);

        Context context = contextH2(list, key);

        assertEquals(
            list.maxHeight,
            list.getHeight(),
            context,
            result -> String.format("The call of the method add(%s) possibly modified the height to %s, but "
                + "expected %s.", key, result.object(), list.maxHeight)
        );
    }

    /**
     * Tests if the {@link SkipList#add(Object)} method adds the element correctly to an empty list.
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
     * @param key    the elements to add
     */
    @DisplayName("08 | Methode fügt in einer leeren Liste korrekt ein.")
    @ParameterizedTest(name = "Test {index}: Einfügen von {1} in einer Liste.")
    @JsonClasspathSource("h2/empty_list.json")
    public void testAddEmptyList(
        @Property("list") @ConvertWith(SkipListConverter.class) Object object,
        @Property("key") Integer key
    ) {
        SkipList<Integer> list = (SkipList<Integer>) object;

        Context context = contextH2(list, key);

        assertNotNull(
            list.head,
            context,
            result -> String.format("The call of the method add(%s) should add the element %s as new head, but given "
                + "%s.", key, key, result.object())
        );
        assert list.head != null;
        assertNull(
            list.head.key.value,
            context,
            result -> String.format("The call of the method add(%s) should add a new sentinel node but given %s.",
                key, result.object())
        );


        assert list.head.next != null;
        assertEquals(
            key,
            list.head.next.key.value,
            context,
            result -> String.format("The call of the method add(%s) should add the element %s, but given %s.", key,
                key, result.object())
        );
    }

    /**
     * Tests if the {@link SkipList#add(Object)}  method adds the element correctly to a list to the lowest level only.
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
     *         "refs": array of integers
     *     }
     * }</pre>
     *
     * @param object the list to test
     * @param keys   the elements to add
     * @param refs   the indices of the previous node of the added elements
     */
    @DisplayName("09 | Methode fügt die Elemente in der untersten Ebene korrekt ein.")
    @ParameterizedTest(name = "Test {index}: Einfügen von {1} in der untersten Ebene.")
    @JsonClasspathSource("h2/lowest_level_only.json")
    public void testAddLowestLevelOnly(
        @Property("list") @ConvertWith(SkipListConverter.class) Object object,
        @Property("keys") @ConvertWith(ArrayConverter.Auto.class) Integer[] keys,
        @Property("refs") @ConvertWith(ArrayConverter.Auto.class) Integer[] refs
    ) {
        SkipList<Integer> source = (SkipList<Integer>) object;
        for (int i = 0; i < keys.length; i++) {
            SkipList<Integer> list = copy(source);
            List<List<ListItem<ExpressNode<Integer>>>> itemRefs = listItemAsList(list.head);
            Integer key = keys[i];
            Integer ref = refs[i];

            Context context = contextH2(list, key);

            // Offset is 1 because of the sentinel node
            ListItem<ExpressNode<Integer>> node = itemRefs.get(itemRefs.size() - 1).get(ref + 1);

            assertNotNull(
                node.next,
                context,
                result -> String.format("The call of the method add(%s) should add the element %s as the successor of "
                    + "%s, but no successor exists.", key, key, node)
            );
            assert node.next != null;
            assertEquals(
                key,
                node.next.key.value,
                context,
                result -> String.format("The call of the method add(%s) should add the element %s "
                    + "as the successor of %s, but given %s.", key, key, node, result.object())
            );
        }
    }

    /**
     * Tests if the {@link SkipList#add(Object)} method creates a new level correctly.
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
     *         "numberOfElementsLevel": array of integers,
     *         "refs": array of integers
     *     }
     * }</pre>
     *
     * @param object                the list to test
     * @param key                   the element to add
     * @param numberOfElementsLevel the number of elements on each level
     * @param refs                  the indices of the added elements
     */
    @DisplayName("10 | Methode erstellt neue Ebene korrekt.")
    @ParameterizedTest(name = "Test {index}: Erstellen einer neuen Ebene beim Einfügen von {1}.")
    @JsonClasspathSource("h2/create_new_level.json")
    public void testAddCreateLevel(
        @Property("list") @ConvertWith(SkipListConverter.class) Object object,
        @Property("key") Integer key,
        @Property("numberOfElementsLevel") @ConvertWith(ArrayConverter.Auto.class) Integer[] numberOfElementsLevel,
        @Property("refs") @ConvertWith(ArrayConverter.Auto.class) Integer[] refs
    ) {
        Probability probability = new Probability() {
            private boolean first = true;

            @Override
            public boolean nextBoolean() {
                if (first) {
                    first = false;
                    return true;
                }
                return false;
            }
        };
        SkipList<Integer> list = (SkipList<Integer>) object;
        list.setProbability(probability);

        Context context = contextH2(list, key);

        List<List<ListItem<ExpressNode<Integer>>>> itemRefs = listItemAsList(list.head);
        for (int i = 0; i < numberOfElementsLevel.length; i++) {
            int level = i;
            List<ListItem<ExpressNode<Integer>>> node = itemRefs.get(i);
            // Offset is 1 because of the sentinel node
            int expectedSize = numberOfElementsLevel[i] + 1;
            assertEquals(
                expectedSize,
                node.size(),
                context,
                result -> String.format("The call of the method add(%s) should add the element %s on  the level %s "
                    + "and modify the size to %s, but given size %s.", key, key, level, expectedSize, result.object())
            );
            assertEquals(
                key,
                node.get(refs[i] + 1).key.value,
                context,
                result -> String.format("The call of the method add(%s) should add the element %s on  the level %s, "
                    + "but given %s.", key, key, level, result.object())
            );
        }
    }

    /**
     * Tests if the {@link SkipList#add(Object)}  method sets the reference to the previous node correctly.
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
    @DisplayName("11 | Methode setzt den Vorgänger-Verweis korrekt.")
    @ParameterizedTest(name = "Test {index}: Setzen des Vorgänger-Verweises beim Einfügen von {1}.")
    @JsonClasspathSource("h2/references_prev.json")
    public void testAddReferencesPrev(
        @Property("list") @ConvertWith(SkipListConverter.class) Object object,
        @Property("key") Integer key,
        @Property("refs") @ConvertWith(ArrayConverter.Auto.class) Integer[] refs) {
        SkipList<Integer> list = (SkipList<Integer>) object;
        list.setProbability(PROBABILITY_ALWAYS_ADD);

        Context context = contextH2(list, key);

        List<List<ListItem<ExpressNode<Integer>>>> itemRefs = listItemAsList(list.head);
        for (int i = 0; i < refs.length; i++) {
            int level = i;
            ListItem<ExpressNode<Integer>> node = itemRefs.get(i).get(refs[i] + 1);

            assert node != null;
            assert node.key.prev != null;
            assertEquals(
                key,
                node.key.prev.key.value,
                context,
                result -> String.format("The call of the method add(%s) should add the element %s on the level %s, "
                        + "and the successor node should reference to it, but given previous node %s.", key, key,
                    level, result.object())
            );
        }
    }

}
