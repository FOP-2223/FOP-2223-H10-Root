package h10;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Converter used to parse a JSON object into a {@link VisitorSkipList}.
 *
 * @author Nhan Huynh
 */
public class VisitorSkipListConverter implements ArgumentConverter {

    @Override
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
        if (!(source instanceof LinkedHashMap)) {
            throw new ArgumentConversionException("Input is not a JSON object");
        } else if (!(context.getParameter().getType() == Object.class)) {
            throw new ArgumentConversionException("Parameter type is not an object type");
        }
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) source;
        if (!(map.get("levels") instanceof ArrayList)) {
            throw new ArgumentConversionException("Input does not contain a 'levels' property with type 'array'");
        }
        @SuppressWarnings("unchecked")
        ArrayList<ArrayList<Integer>> levels = (ArrayList<ArrayList<Integer>>) map.get("levels");
        int maxHeight;
        if (map.containsKey("maxHeight")) {
            if (!(map.get("maxHeight") instanceof Integer height)) {
                throw new ArgumentConversionException("Input does not contain a 'maxHeight' property with type 'integer'");
            }
            maxHeight = height;
        } else {
            maxHeight = levels.size();
        }
        Comparator<Integer> cmp = Comparator.naturalOrder();
        return createList(
            levels,
            new Comparator<>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return cmp.compare(o1, o2);
                }

                @Override
                public String toString() {
                    return "Natural Order";
                }
            },
            maxHeight
        );
    }

    /**
     * Links the given node to the upper level.
     *
     * @param references the map of references to the upper level
     * @param item       the node to link
     * @param <T>        the type of the node
     */
    private <T> void linkUpperLevel(Map<T, ListItem<ExpressNode<VisitorNode<T>>>> references,
                                    ListItem<ExpressNode<VisitorNode<T>>> item) {
        T value;
        if (item.key.value == null) {
            value = null;
        } else {
            value = item.key.value.getValue();
        }
        if (references.containsKey(value)) {
            ListItem<ExpressNode<VisitorNode<T>>> upperLevel = references.get(value);
            upperLevel.key.down = item;
            item.key.up = upperLevel;
        }

        references.put(value, item);
    }

    /**
     * Creates a visitor skip list from the given list of levels.
     *
     * @param levels    the list of levels
     * @param cmp       the comparator to use for maintaining the order of the elements
     * @param maxHeight the maximum height of the skip list
     *
     * @return the created visitor skip list
     */
    private VisitorSkipList<Integer> createList(List<? extends List<Integer>> levels, Comparator<Integer> cmp,
                                                int maxHeight) {
        // References to the upper level
        Map<Integer, ListItem<ExpressNode<VisitorNode<Integer>>>> references = new HashMap<>();
        ListItem<ExpressNode<VisitorNode<Integer>>> head = null;
        ListItem<ExpressNode<VisitorNode<Integer>>> tail = null;

        for (List<Integer> currentLevel : levels) {
            // Create a new level with its nodes, each level starts with a sentinel node
            ListItem<ExpressNode<VisitorNode<Integer>>> sentinel = new ListItem<>();
            sentinel.key = new ExpressNode<>();

            // Link nodes to the upper level
            linkUpperLevel(references, sentinel);

            if (head == null) {
                head = sentinel;
            } else {
                tail.key.down = sentinel;
                sentinel.key.up = tail;
            }
            tail = sentinel;

            // Fill nodes to the current level
            ListItem<ExpressNode<VisitorNode<Integer>>> currentLevelTail = sentinel;
            for (Integer node : currentLevel) {
                ListItem<ExpressNode<VisitorNode<Integer>>> item = new ListItem<>();
                item.key = new ExpressNode<>();
                item.key.value = new VisitorNode<>(node);
                currentLevelTail.next = item;
                item.key.prev = currentLevelTail;
                currentLevelTail = currentLevelTail.next;

                // Link nodes to the upper level
                linkUpperLevel(references, item);
            }
        }

        VisitorSkipList<Integer> list = new VisitorSkipList<>(
            new Comparator<>() {
                @Override
                public int compare(h10.VisitorNode<Integer> o1, h10.VisitorNode<Integer> o2) {
                    return cmp.compare(o1.getValue(), o2.getValue());
                }

                @Override
                public String toString() {
                    return cmp.toString();
                }
            },
            maxHeight
        );
        list.head = head;
        list.height = levels.size();
        list.size = levels.size() == 0 ? 0 : levels.get(levels.size() - 1).size();
        return list;
    }

}
