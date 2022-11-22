package h10;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.junitpioneer.jupiter.json.Property;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.conversion.ArrayConverter;

import java.util.Arrays;
import java.util.List;

import static h10.H3_PublicTests.contextPost;
import static h10.H3_PublicTests.contextPre;
import static h10.PrivateTutorUtils.copyTutor;
import static h10.PublicTutorUtils.contextBuilderList;
import static h10.PublicTutorUtils.listItemAsList;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertEquals;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertNotNull;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertNull;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.assertSame;

/**
 * Defines the private JUnit test cases related to the task H3.
 *
 * @author Nhan Huynh
 * @see SkipList#remove(Object)
 */
@DisplayName("H3")
@TestForSubmission
@SuppressWarnings({"unchecked", "DuplicatedCode"})
public class H3_PrivateTests {

    /**
     * Tests if the {@link SkipList#remove(Object)} method removes the last element correctly.
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
     * @param object     the list to test
     * @param comparison the expected number of comparisons to find the last element
     */
    @DisplayName("22 | Methode entfernt das letzte Element korrekt.")
    @ParameterizedTest(name = "Test {index}: Entfernung des letzten Elements.")
    @JsonClasspathSource("h3/last_element.json")
    public void testRemoveLastElement(
        @Property("list") @ConvertWith(TutorSkipListConverter.class) Object object,
        @Property("comparison") int comparison) {
        TutorSkipList<Integer> list = (TutorSkipList<Integer>) object;
        List<List<ListItem<ExpressNode<Integer>>>> itemRefs = listItemAsList(list.head);
        List<ListItem<ExpressNode<Integer>>> last = itemRefs.get(itemRefs.size() - 1);
        Integer key = last.get(last.size() - 1).key.value;

        Context context = contextPre(list, key);
        list.remove(key);
        context = contextPost(context, list);

        assertEquals(
            comparison,
            list.getComparisonCount(),
            context,
            result -> String.format("The call to remove(%d) should have made %d comparisons, but made %d instead.",
                key, comparison, result.object())
        );

        assertNotNull(
            list.head,
            context,
            result -> String.format("The call to remove(%d) should not have removed the entire list.", key)
        );

        assert list.head != null;
        ListItem<ExpressNode<Integer>> actual = last.get(last.size() - 2).next;
        assertNull(
            actual,
            context,
            result -> String.format("The call to remove(%d) should have removed the last element, but given %s.", key,
                result.object())
        );
    }

    /**
     * Tests if the {@link SkipList#remove(Object)} method removes the elements correctly.
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
     *         "comparisons": array of integers,
     *         "refs: array of 2D arrays of integers
     *     }
     * }</pre>
     *
     * @param object      the list to test
     * @param keys        the elements to remove
     * @param comparisons the number of comparisons to find the correct insertion point
     * @param refs        the indices of the removed elements on each level
     */
    @DisplayName("23 | Methode entfernt die Elemente korrekt.")
    @ParameterizedTest(name = "Test {index}: Entfernung der Elemente {1} mit minimalen Vergleichen {2}.")
    @JsonClasspathSource("h3/element.json")
    public void testRemoveElement(
        @Property("list") @ConvertWith(TutorSkipListConverter.class) Object object,
        @Property("keys") @ConvertWith(ArrayConverter.Auto.class) Integer[] keys,
        @Property("comparisons") @ConvertWith(ArrayConverter.Auto.class) Integer[] comparisons,
        @Property("refs") @ConvertWith(ArrayConverter.Auto.class) Integer[][] refs
    ) {
        TutorSkipList<Integer> source = (TutorSkipList<Integer>) object;
        for (int i = 0; i < keys.length; i++) {
            TutorSkipList<Integer> list = copyTutor(source);
            List<List<ListItem<ExpressNode<Integer>>>> itemRefs = listItemAsList(list.head);
            int index = i;
            Integer key = keys[i];

            Context context = contextPre(list, key);
            list.remove(key);
            context = contextPost(context, list);

            assertEquals(
                comparisons[i],
                list.getComparisonCount(),
                context,
                result -> String.format("The call to remove(%d) should have made %d comparisons, but made %d instead.",
                    key, comparisons[index], result.object())
            );
            for (int level = 0; level < refs[i].length; level++) {
                int currentLevel = level;
                if (refs[i][level] == -2) {
                    continue;
                }
                ListItem<ExpressNode<Integer>> previous = itemRefs.get(level).get(refs[i][level] + 1);
                ListItem<ExpressNode<Integer>> successor = itemRefs.get(level).get(refs[i][level] + 3);
                assertSame(
                    previous.next,
                    successor,
                    context,
                    result -> String.format("The call to remove(%d) should have removed the element at level %d and "
                        + "the successor should be %s, but given %s.", key, currentLevel, successor, result.object())
                );
            }
        }
    }

    /**
     * Tests if the {@link SkipList#remove(Object)} method removes all element correctly.
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
     *         "comparisons": array of integers
     *     }
     * }</pre>
     *
     * @param object      the list to test
     * @param keys        the elements to remove
     * @param comparisons the number of comparisons to find the correct insertion point
     */
    @DisplayName("24 | Methode entfernt alle Elemente korrekt.")
    @ParameterizedTest(name = "Test {index}: Entfernung der Elemente {1} mit minimalen Vergleichen {2}.")
    @JsonClasspathSource("h3/all_elements.json")
    public void testRemoveAllElements(
        @Property("list") @ConvertWith(TutorSkipListConverter.class) Object object,
        @Property("keys") @ConvertWith(ArrayConverter.Auto.class) Integer[] keys,
        @Property("comparisons") @ConvertWith(ArrayConverter.Auto.class) Integer[] comparisons) {
        TutorSkipList<Integer> list = (TutorSkipList<Integer>) object;
        for (int i = 0; i < keys.length; i++) {
            int index = i;
            Integer key = keys[i];

            Context context = contextPre(list, key);
            list.remove(key);
            context = contextPost(context, list);

            assertEquals(
                comparisons[i],
                list.getComparisonCount(),
                context,
                result -> String.format("The call to remove(%d) should have made %d comparisons, but made %d instead.",
                    key, comparisons[index], result.object())
            );
        }

        Context context = contextBuilderList(list, "SkipList#remove(Object)")
            .add("Method", "remove(Object)")
            .add("Elements to remove", Arrays.toString(keys))
            .build();

        assertNull(
            list.head,
            context,
            result -> String.format("The call to remove(%s) should have removed the entire list, but given %s.",
                Arrays.toString(keys), result.object())
        );
    }

    /**
     * Tests if the {@link SkipList#remove(Object)} method sets the references correctly.
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
     *         "comparisons": array of integers,
     *         "refs: array of 2D arrays of integers
     *     }
     * }</pre>
     *
     * @param object the list to test
     * @param key    the element to remove
     * @param refs   the indices of the removed elements on each level
     */
    @DisplayName("25 | Methode setzt die Referenzen korrekt.")
    @ParameterizedTest(name = "Test {index}: Setzen des Vorgänger-Verweises beim Entfernen von {2}.")
    @JsonClasspathSource("h3/references.json")
    public void testRemoveReferences(
        @Property("list") @ConvertWith(SkipListConverter.class) Object object,
        @Property("key") Integer key,
        @Property("refs") @ConvertWith(ArrayConverter.Auto.class) Integer[] refs) {
        SkipList<Integer> list = (SkipList<Integer>) object;

        Context context = contextPre(list, key);
        list.remove(key);
        context = contextPost(context, list);

        List<List<ListItem<ExpressNode<Integer>>>> itemRefs = listItemAsList(list.head);
        for (int i = 0; i < refs.length; i++) {
            int level = i;
            ListItem<ExpressNode<Integer>> successor = itemRefs.get(i).get(refs[i] + 1);
            ListItem<ExpressNode<Integer>> previous = itemRefs.get(i).get(refs[i]);

            assertNotNull(
                successor,
                context,
                result -> String.format("The call of the method remove(%s) should remove the element %s on the level "
                        + "%s, and the successor node should reference to it, but no successor node given.", key, key,
                    level)
            );
            assert successor != null;
            assertSame(
                previous,
                successor.key.prev,
                context,
                result -> String.format("The call of the method remove(%s) should remove the element %s on the level "
                        + "%s, and the successor node should reference to it, but given previous node %s.", key, key,
                    level, result.object())
            );
        }
    }

}
