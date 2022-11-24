package h10;

import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Objects;

/**
 * Represents a skip list.
 *
 * @param <T> the type of the elements in this list
 *
 * @author Nhan Huynh
 * @see <a href="https://en.wikipedia.org/wiki/Skip_list">Skip list</a>
 */
public class SkipList<T> {

    /**
     * The comparator used to maintain order in this list.
     */
    protected final Comparator<? super T> cmp;

    /**
     * The maximum height of the skip list.
     */
    final int maxHeight;

    /**
     * The probability function used to determine if a node should be added on another level.
     */
    private Probability probability;

    /**
     * The head of the skip list.
     */
    @Nullable ListItem<ExpressNode<T>> head;

    /**
     * The current height of the skip list.
     */
    int height = 0;

    /**
     * The number of items in the skip list.
     */
    int size = 0;

    /**
     * Constructs and initializes an empty skip list without the probability to add elements on higher levels.
     *
     * @param cmp      the comparator used to maintain order in this list
     * @param maxHeight the maximum height of the skip list
     */
    public SkipList(Comparator<? super T> cmp, int maxHeight) {
        this(cmp, maxHeight, () -> false);
    }

    /**
     * Constructs and initializes an empty skip list.
     *
     * @param cmp         the comparator used to maintain order in this list
     * @param maxHeight    the maximum height of the skip list
     * @param probability the probability function used to determine if a node should be added on another level
     */
    public SkipList(Comparator<? super T> cmp, int maxHeight, Probability probability) {
        this.cmp = cmp;
        this.maxHeight = maxHeight;
        this.probability = probability;
    }

    /**
     * Returns the current height of this skip list.
     *
     * @return the current height of this skip list
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the probability function used to determine if a node should be added on another level.
     *
     * @return the probability function used to determine if a node should be added on another leve
     */
    public Probability getProbability() {
        return probability;
    }

    /**
     * Sets the probability function used to determine if a node should be added on another level.
     *
     * @param probability the probability function
     */
    public void setProbability(Probability probability) {
        this.probability = probability;
    }

    /**
     * Returns the number of items in this skip list.
     *
     * @return the number of items in this skip list
     */
    public int size() {
        return size;
    }

    /**
     * Returns the first occurrence of the specified element in this list, or {@code null} if this list does not
     * contain the element. The first element is on the lowest level.
     *
     * @param key the element to search for
     *
     * @return the first occurrence of the specified element in this list, or {@code null} if this list does not
     *         contain the element
     */
    private ListItem<ExpressNode<T>> get(T key) {
        if (isEmpty()) {
            return null;
        }
        ListItem<ExpressNode<T>> previous = head;
        // Remember sentinel value for going down if the first element is greater than the searched one
        while (previous != null) {
            if (previous.next == null) {
                // Edge case if we are moving down on many levels at once, since the last element is <
                previous = previous.key.down;
                continue;
            }
            // Skip the sentinel node
            ListItem<ExpressNode<T>> node = previous.next;
            int value = cmp.compare(node.key.value, key);
            if (value == 0) {
                // Key found
                return node;
            } else if (node.next != null && value < 0) {
                // Key can be on the same level
                previous = previous.next;
            } else if (node.next == null && value < 0) {
                // Go down on the last node
                previous = node;
                previous = previous.key.down;
            } else {
                // Key can be on the lower level
                previous = previous.key.down;
            }
        }
        return null;
    }

    /**
     * Returns {@code true} if this list contains the specified element.
     *
     * @param key the element whose presence in this list is to be tested
     *
     * @return {@code true} if this list contains the specified element
     */
    public boolean contains(T key) {
        return get(key) != null;
    }

    /**
     * Adds the specified element to this list. The element will be added on the highest floor of the skip list and on
     * the next levels if the probability function returns {@code true}.
     *
     * @param key the element to be added
     */
    public void add(T key) {
        ListItem<ExpressNode<T>> previous = head;
        // Store the potential previous nodes for each level where an insertion
        ListItem<ListItem<ExpressNode<T>>> positions = null;
        // Find insertion position on all levels
        while (previous != null) {
            if (previous.next == null) {
                // Edge case if we are moving down on many levels at once, since the last element is <
                ListItem<ListItem<ExpressNode<T>>> item = new ListItem<>();
                item.key = previous;
                // The lowest level will be the first element in the list
                // Insertion at the beginning of the list
                item.next = positions;
                positions = item;
                previous = previous.key.down;
                continue;
            }
            // Skip the sentinel node
            ListItem<ExpressNode<T>> node = previous.next;
            int value = cmp.compare(node.key.value, key);
            if (node.next != null && value <= 0) {
                // Key can be on the same level
                previous = previous.next;
            } else {
                if (node.next == null && value <= 0) {
                    // Go down on the last node
                    previous = node;
                }
                // Go down, remember potential insertion position
                ListItem<ListItem<ExpressNode<T>>> item = new ListItem<>();
                item.key = previous;
                // The lowest level will be the first element in the list
                // Insertion at the beginning of the list
                item.next = positions;
                positions = item;
                previous = previous.key.down;
            }
        }

        // Potential insertions on each level
        int currentHeight = 1;
        ListItem<ExpressNode<T>> lowerLevelNode = null;
        do {
            if (head == null) {
                // Empty list, create the first level and sentinel node
                head = new ListItem<>();
                head.key = new ExpressNode<>();

                // Insertion position on the first level
                ListItem<ListItem<ExpressNode<T>>> node = new ListItem<>();
                node.key = head;
                positions = node;
                height++;
            } else if (currentHeight > height) {
                // Create new level if it does not exist (new upper level)
                ListItem<ExpressNode<T>> newHead = new ListItem<>();
                newHead.key = new ExpressNode<>();
                newHead.key.down = head;
                head.key.up = newHead;
                head = newHead;

                ListItem<ListItem<ExpressNode<T>>> node = new ListItem<>();
                node.key = head;
                positions = node;
                height++;
            }
            ListItem<ExpressNode<T>> node = new ListItem<>();
            node.key = new ExpressNode<>();
            node.key.value = key;

            // Connect lower and upper levels
            if (lowerLevelNode != null) {
                node.key.down = lowerLevelNode;
                lowerLevelNode.key.up = node;
            }

            ListItem<ExpressNode<T>> current = positions.key;
            if (current.next != null) {
                // Last node does not have a next node, so we do not need to adjust the references
                current.next.key.prev = node;
                node.next = current.next;
            }

            node.key.prev = current;
            current.next = node;

            positions = positions.next;
            currentHeight++;
            lowerLevelNode = node;
        } while ((positions != null || currentHeight <= maxHeight) && probability.nextBoolean());
        size++;
    }

    /**
     * Removes the first occurrence of the specified element from this list, if it is present. The element will be
     * removed from all levels.
     *
     * @param key the element to be removed from this list, if present
     */
    public void remove(T key) {
        // Get the first occurrence of the element
        ListItem<ExpressNode<T>> walker = get(key);

        // If the element is not null, that means it is present in the list
        if (walker != null) {
            size--;
        }

        // Removal of element on all levels
        while (walker != null) {
            ListItem<ExpressNode<T>> lowerLevel = walker.key.down;
            // Cannot be null since get returns non-null values if the element is found
            // We checked that walker != null
            assert walker.key.prev != null;
            if (walker.key.prev.key.value == null && walker.next == null) {
                // Single element list
                if (walker.key.up == null) {
                    // Head should be deleted
                    // Since walker is non-null, the list is not empty
                    assert head != null;
                    head = head.key.down;
                    if (lowerLevel != null) {
                        lowerLevel.key.up = null;
                    }
                } else {
                    // Adjust reference from up and down levels
                    walker.key.up.key.down = walker.key.down;
                    // Since walker is non-null, the list is not empty
                    assert walker.key.down != null;
                    walker.key.down.key.up = walker.key.up;
                }
                height--;
            } else {
                // Adjust reference from prev and next nodes
                walker.key.prev.next = walker.next;
                if (walker.next != null) {
                    walker.next.key.prev = walker.key.prev;
                }
            }
            walker = lowerLevel;
        }
    }

    /**
     * Returns {@code true} if this list contains no elements.
     *
     * @return {@code true} if this list contains no elements
     */
    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SkipList<?> skipList = (SkipList<?>) o;
        return height == skipList.height
            && size == skipList.size
            && Objects.equals(head, skipList.head);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxHeight, head, height, size);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (ListItem<ExpressNode<T>> walker = head; walker != null; walker = walker.key.down) {
            sb.append("[");
            for (ListItem<ExpressNode<T>> walker2 = walker.next; walker2 != null; walker2 = walker2.next) {
                sb.append(walker2.key.value);
                if (walker2.next != null) {
                    sb.append(", ");
                }
            }
            sb.append("]");
            if (walker.key.down != null) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

}
