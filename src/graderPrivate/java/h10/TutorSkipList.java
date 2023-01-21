package h10;

import java.util.Comparator;

/**
 * A custom skip list implementation that allows to track the number of comparisons on each operation.
 *
 * @param <T> the type of the elements in this list
 *
 * @author Nhan Huynh
 */
public class TutorSkipList<T> extends SkipList<T> {

    /**
     * The comparator used to track the number of comparisons.
     */
    private final CounterComparator<? super T> counter;

    /**
     * Constructs and initializes an empty skip list.
     *
     * @param cmp         the comparator used to maintain order in this list
     * @param maxHeight   the maximum level of the skip list
     * @param probability the probability function used to determine if a node should be added on another level
     */
    @SuppressWarnings("unchecked")
    public TutorSkipList(Comparator<? super T> cmp, int maxHeight, Probability probability) {
        super(new CounterComparator<>(cmp), maxHeight, probability);
        this.counter = (CounterComparator<? super T>) this.cmp;
    }

    @Override
    public boolean contains(T key) {
        counter.resetCounter();
        return super.contains(key);
    }

    @Override
    public void add(T key) {
        counter.resetCounter();
        super.add(key);
    }

    @Override
    public void remove(T key) {
        counter.resetCounter();
        super.remove(key);
    }

    /**
     * Returns the number of comparisons made on the last operation.
     *
     * @return the number of comparisons made on the last operation
     */
    public int getComparisonCount() {
        return counter.getComparisons();
    }

    /**
     * A comparator that counts the number of comparisons made.
     *
     * @param <T> the type of the elements in this list
     */
    private static class CounterComparator<T> implements Comparator<T> {

        /**
         * The counter used to track the number of comparisons.
         */
        private int comparisons = 0;

        /**
         * The comparator used to compare elements.
         */
        private final Comparator<? super T> real;

        /**
         * Constructs a new counter comparator.
         *
         * @param real the comparator used to compare elements
         */
        public CounterComparator(Comparator<? super T> real) {
            this.real = real;
        }

        @Override
        public int compare(T o1, T o2) {
            comparisons++;
            return real.compare(o1, o2);
        }

        /**
         * Returns the number of comparisons made.
         *
         * @return the number of comparisons made
         */
        public int getComparisons() {
            return comparisons;
        }

        /**
         * Resets the counter to 0.
         */
        public void resetCounter() {
            comparisons = 0;
        }

        @Override
        public String toString() {
            return "CounterComparator{"
                + "comparisons=" + comparisons
                + ", real=" + real
                + '}';
        }

    }

}
