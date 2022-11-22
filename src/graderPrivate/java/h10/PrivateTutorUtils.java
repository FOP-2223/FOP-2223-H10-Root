package h10;

import static h10.PublicTutorUtils.copy;

/**
 * Defines the private utility methods for the testing purposes for the tasks of the assignment H10.
 *
 * @author Nhan Huynh
 */
public class PrivateTutorUtils {

    /**
     * Don't let anyone instantiate this class.
     */
    private PrivateTutorUtils() {
    }

    /**
     * Creates a copy of a tutor skip list.
     *
     * @param list the skip list to copy
     * @param <T>  the type of the elements in the skip list
     *
     * @return a copy of the skip list
     */
    public static <T> TutorSkipList<T> copyTutor(TutorSkipList<T> list) {
        SkipList<T> source = copy(list);
        TutorSkipList<T> copy = new TutorSkipList<>(source.cmp, source.getMaxLevel(), source.getProbability());
        copy.head = source.head;
        copy.currentMaxLevel = source.currentMaxLevel;
        copy.size = source.size;
        return copy;
    }

}
