package h10;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static h10.PublicTutorUtils.linkUpperLevel;

/**
 * Converter for converting a JSON object to a {@link SkipList}.
 *
 * @author Nhan Huynh
 */
public class SkipListConverter implements ArgumentConverter {

    @Override
    @SuppressWarnings("unchecked")
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
        if (!(source instanceof LinkedHashMap)) {
            throw new ArgumentConversionException("Input is not a JSON object");
        } else if (!(context.getParameter().getType() == Object.class)) {
            throw new ArgumentConversionException("Parameter type is not an object type");
        }
        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) source;
        if (!(map.get("levels") instanceof ArrayList)) {
            throw new ArgumentConversionException("Input does not contain a 'levels' property with type 'array'");
        }
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
        return createList(levels, Comparator.naturalOrder(), maxHeight);
    }

    /**
     * Creates a skip list from the given list of levels.
     *
     * @param levels    the list of levels
     * @param cmp       the comparator to use for maintaining the order of the elements
     * @param maxHeight the maximum height of the skip list
     *
     * @return the created skip list
     */
    public SkipList<Integer> createList(ArrayList<ArrayList<Integer>> levels, Comparator<Integer> cmp, int maxHeight) {
        // References to the upper level
        Map<Integer, ListItem<ExpressNode<Integer>>> references = new HashMap<>();
        ListItem<ExpressNode<Integer>> head = null;
        ListItem<ExpressNode<Integer>> tail = null;

        for (ArrayList<Integer> currentLevel : levels) {
            // Create a new level with its nodes, each level starts with a sentinel node
            ListItem<ExpressNode<Integer>> sentinel = new ListItem<>();
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
            ListItem<ExpressNode<Integer>> currentLevelTail = sentinel;
            for (Integer node : currentLevel) {
                ListItem<ExpressNode<Integer>> item = new ListItem<>();
                item.key = new ExpressNode<>();
                item.key.value = node;
                currentLevelTail.next = item;
                item.key.prev = currentLevelTail;
                currentLevelTail = currentLevelTail.next;

                // Link nodes to the upper level
                linkUpperLevel(references, item);
            }
        }

        SkipList<Integer> list = new SkipList<>(cmp, maxHeight);
        list.head = head;
        list.height = levels.size();
        list.size = levels.size() == 0 ? 0 : levels.get(levels.size() - 1).size();
        return list;
    }

}
