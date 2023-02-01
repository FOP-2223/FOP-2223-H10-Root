package h10;

import org.apache.maven.api.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junitpioneer.jupiter.json.JsonClasspathSource;
import org.junitpioneer.jupiter.json.Property;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.conversion.ArrayConverter;

import java.util.List;

import static h10.PrivateTutorUtils.assertComparisons;
import static h10.PrivateTutorUtils.convert;
import static h10.PublicTutorUtils.contextH3;
import static h10.PublicTutorUtils.listItemAsList;
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
     *             ("maxHeight": integer)
     *         },
     *         "comparisons": 2D array of integers
     *     }
     * }</pre>
     *
     * @param object      the list to test
     * @param comparisons the expected comparisons path
     */
    @DisplayName("22 | Methode entfernt das letzte Element korrekt.")
    @ParameterizedTest(name = "Test {index}: Entfernung des letzten Elements.")
    @JsonClasspathSource("h3/last_element.json")
    public void testRemoveLastElement(
        @Property("list") @ConvertWith(VisitorSkipListConverter.class) Object object,
        @Property("comparisons") @ConvertWith(ArrayConverter.Auto.class) Integer[][] comparisons) {
        VisitorSkipList<Integer> list = convert(object);
        List<List<ListItem<ExpressNode<VisitorNode<Integer>>>>> nodes = listItemAsList(list.head);
        List<ListItem<ExpressNode<VisitorNode<Integer>>>> last = nodes.get(nodes.size() - 1);
        VisitorNode<Integer> node = last.get(last.size() - 1).key.value;

        // In order to not track the actual node, we have to create a new one
        Context context = contextH3(list, new VisitorNode<>(node.getValue()));

        assertComparisons(nodes, comparisons, context);

        assert list.head != null;
        @Nullable ListItem<ExpressNode<VisitorNode<Integer>>> actual = last.get(last.size() - 2).next;
        assertNull(
            actual,
            context,
            result -> String.format("The call to remove(%s) should have removed the last element, but given %s.", node,
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
     *             ("maxHeight": integer)
     *         }
     *         "keys": array of integers,
     *         "comparisons": 2D array of integers
     *         "refs: array of 2D arrays of integers
     *     }
     * }</pre>
     *
     * @param object      the list to test
     * @param key         the element to remove
     * @param comparisons the expected comparisons path
     * @param refs        the indices of the removed elements on each level
     */
    @DisplayName("23 | Methode entfernt die Elemente korrekt.")
    @ParameterizedTest(name = "Test {index}: Entfernung der Elemente {1} mit minimalen Vergleichen {2}.")
    @JsonClasspathSource({
        "h3/element/33.json", "h3/element/34.json", "h3/element/37.json",
        "h3/element/51.json", "h3/element/94.json", "h3/element/148.json"
    })
    public void testRemoveElement(
        @Property("list") @ConvertWith(VisitorSkipListConverter.class) Object object,
        @Property("key") Integer key,
        @Property("comparisons") @ConvertWith(ArrayConverter.Auto.class) Integer[][] comparisons,
        @Property("refs") @ConvertWith(ArrayConverter.Auto.class) Integer[] refs
    ) {
        @SuppressWarnings("DuplicatedCode")
        VisitorSkipList<Integer> list = convert(object);
        List<List<ListItem<ExpressNode<VisitorNode<Integer>>>>> nodes = listItemAsList(list.head);
        VisitorNode<Integer> node = new VisitorNode<>(key);
        Context context = contextH3(list, node);

        assertComparisons(nodes, comparisons, context);

        for (int level = 0; level < refs.length; level++) {
            int currentLevel = level;
            if (refs[level] == -2) {
                continue;
            }
            ListItem<ExpressNode<VisitorNode<Integer>>> previous = nodes.get(level).get(refs[level] + 1);
            ListItem<ExpressNode<VisitorNode<Integer>>> successor = nodes.get(level).get(refs[level] + 3);
            assertSame(
                previous.next,
                successor,
                context,
                result -> String.format("The call to remove(%d) should have removed the element at level %d and "
                    + "the successor should be %s, but given %s.", key, currentLevel, successor, result.object())
            );
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
     *             ("maxHeight": integer)
     *         }
     *         "keys": array of integers,
     *         "comparisons": 2D array of integers,
     *         "empty": boolean
     *     }
     * }</pre>
     *
     * @param object      the list to test
     * @param key         the elements to remove
     * @param comparisons the expected comparisons path
     * @param empty       whether the list should be empty after the removal
     */
    @DisplayName("24 | Methode entfernt alle Elemente korrekt.")
    @ParameterizedTest(name = "Test {index}: Entfernung der Elemente {1} mit minimalen Vergleichen {2}.")
    @JsonClasspathSource({
        "h3/all/98.json", "h3/all/72.json", "h3/all/47.json", "h3/all/17.json", "h3/all/12.json", "h3/all/5.json"
    })
    public void testRemoveAllElements(
        @Property("list") @ConvertWith(VisitorSkipListConverter.class) Object object,
        @Property("key") Integer key,
        @Property("comparisons") @ConvertWith(ArrayConverter.Auto.class) Integer[][] comparisons,
        @Property("empty") boolean empty) {
        @SuppressWarnings("DuplicatedCode")
        VisitorSkipList<Integer> list = convert(object);
        List<List<ListItem<ExpressNode<VisitorNode<Integer>>>>> nodes = listItemAsList(list.head);
        VisitorNode<Integer> node = new VisitorNode<>(key);
        Context context = contextH3(list, node);

        assertComparisons(nodes, comparisons, context);

        if (empty) {
            assertNull(
                list.head,
                context,
                result -> String.format("The call to remove(%s) should have removed the entire list, but given %s.",
                    node, result.object())
            );
        } else {
            assertNotNull(
                list.head,
                context,
                result -> String.format("The call to remove(%s) should not have removed the entire list, but given %s.",
                    node, result.object())
            );
        }
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
     *             ("maxHeight": integer)
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
        SkipList<Integer> list = convert(object);

        Context context = contextH3(list, key);

        List<List<ListItem<ExpressNode<Integer>>>> itemRefs = listItemAsList(list.head);
        for (int i = 0; i < refs.length; i++) {
            int level = i;
            ListItem<ExpressNode<Integer>> successor = itemRefs.get(i).get(refs[i] + 1);
            ListItem<ExpressNode<Integer>> previous = itemRefs.get(i).get(refs[i]);

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
