package h10;

import org.sourcegrade.jagr.api.rubric.Criterion;
import org.sourcegrade.jagr.api.rubric.Rubric;
import org.sourcegrade.jagr.api.rubric.RubricProvider;

import static h10.PublicTutorUtils.criterion;

/**
 * Defines the private rubric for the assignment H10.
 *
 * @author Nhan Huynh
 */
public class H10_RubricProvider implements RubricProvider {

    /**
     * Defines the private rubric for the task H1.
     */
    private static final Criterion H1 = criterion("H1 | SkipList#contains(Object)", H1_PrivateTests.class);

    /**
     * Defines the private rubric for the task H2.
     */
    private static final Criterion H2 = criterion("H2 | SkipList#add(Object)", H2_PrivateTests.class);

    /**
     * Defines the private rubric for the task H3.
     */
    private static final Criterion H3 = criterion("H3 | SkipList#remove(Object)", H3_PrivateTests.class);

    @Override
    public Rubric getRubric() {
        return Rubric.builder()
            .title("H10 | Verzeigerte Strukturen - Private Tests")
            .addChildCriteria(H1, H2, H3)
            .build();
    }

}
