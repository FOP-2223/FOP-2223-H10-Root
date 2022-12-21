package h10;

import org.sourcegrade.jagr.api.rubric.Criterion;
import org.sourcegrade.jagr.api.rubric.GradeResult;

import java.util.List;

public class UngradedCriterionBuilder extends CriterionBuilder {

    private final int minPoints;
    private final int maxPoints;

    public UngradedCriterionBuilder(String shortDescription) {
        this(shortDescription, 0, 1);
    }

    public UngradedCriterionBuilder(String shortDescription, int minPoints, int maxPoints) {
        super(codeTagify(shortDescription));
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
    }

    @Override
    public Criterion build() {
        return Criterion.builder()
            .shortDescription(shortDescription)
            .grader((testCycle, criterion) -> new GradeResult() {
                @Override
                public int getMinPoints() {
                    return minPoints;
                }

                @Override
                public int getMaxPoints() {
                    return maxPoints;
                }

                @Override
                public List<String> getComments() {
                    return List.of("Not graded by public grader");
                }
            })
            .minPoints(minPoints)
            .maxPoints(maxPoints)
            .build();
    }

}
