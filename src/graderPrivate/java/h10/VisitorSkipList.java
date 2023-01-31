package h10;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A skip list that keeps track of the visited nodes.
 *
 * @param <T> The type of the values in the skip list.
 *
 * @author Nhan Huynh
 */
public class VisitorSkipList<T> extends SkipList<VisitorNode<T>> {

    /**
     * Constructs and initializes an empty skip list.
     *
     * @param cmp         the comparator used to maintain order in this list
     * @param maxHeight   the maximum height of the skip list
     * @param probability the probability function used to determine if a node should be added on another level
     */
    public VisitorSkipList(Comparator<? super VisitorNode<T>> cmp, int maxHeight, Probability probability) {
        super(new VisitorComparator<T>(cmp), maxHeight, probability);
    }


    /**
     * Constructs and initializes an empty skip list without the probability to add elements on higher levels.
     *
     * @param cmp       the comparator used to maintain order in this list
     * @param maxHeight the maximum height of the skip list
     */
    public VisitorSkipList(Comparator<? super VisitorNode<T>> cmp, int maxHeight) {
        super(new VisitorComparator<T>(cmp), maxHeight);
    }

    /**
     * Resets the visited nodes.
     */
    private void reset() {
        ((VisitorComparator) cmp).reset();
    }

    @Override
    public boolean contains(VisitorNode<T> key) {
        reset();
        return super.contains(key);
    }

    @Override
    public void add(VisitorNode<T> key) {
        reset();
        super.add(key);
        assert head != null;
        if (head.key.down == null) {
            return;
        }
        // Find position of the inserted node
        ListItem<ExpressNode<VisitorNode<T>>> current = head;
        while (current.key.down != null) {
            current = current.key.down;
        }
        current = current.next;
        // If there are duplicates, the last inserted node is the one we are looking for
        ListItem<ExpressNode<VisitorNode<T>>> node = null;
        boolean found = false;
        while (current != null) {
            if (current.key.value == key) {
                found = true;
                node = current;
            } else if (found) {
                break;
            }
            current = current.next;
        }
        // In case the add method does not work correctly, we can stop here
        if (node == null) {
            return;
        }
        // All nodes are unique. We have to replace each node on the higher level with unique nodes
        node = node.key.up;
        while (node != null) {
            node.key.value = new VisitorNode<>(key.getValue());
            node = node.key.up;
        }
    }

    @Override
    public void remove(VisitorNode<T> key) {
        reset();
        super.remove(key);
    }

    /**
     * A comparator that keeps track of the visited nodes.
     *
     * @param <T> The type of the element to compare.
     */
    private static class VisitorComparator<T> implements Comparator<VisitorNode<T>> {

        /**
         * The real comparator.
         */
        private final Comparator<? super VisitorNode<T>> real;

        /**
         * The visited nodes.
         */
        private final List<VisitorNode<T>> visitorNodes = new ArrayList<>();

        /**
         * Constructs a new visitor comparator.
         *
         * @param real the real comparator to use
         */
        public VisitorComparator(Comparator<? super VisitorNode<T>> real) {
            this.real = real;
        }

        @Override
        public int compare(VisitorNode<T> o1, VisitorNode<T> o2) {
            o1.visit();
            o2.visit();
            visitorNodes.add(o1);
            visitorNodes.add(o2);
            return real.compare(o1, o2);
        }

        /**
         * Returns the visited nodes.
         * @return the visited nodes
         */
        public List<VisitorNode<T>> getVisitedNodes() {
            return visitorNodes;
        }

        /**
         * Resets the visited nodes.
         */
        public void reset() {
            visitorNodes.forEach(VisitorNode::unvisit);
            visitorNodes.clear();
        }

        @Override
        public String toString() {
            return "VisitorComparator{" +
                "real=" + real +
                '}';
        }

    }

}
