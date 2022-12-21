package h10;

import org.sourcegrade.jagr.api.rubric.Criterion;

import java.util.regex.Pattern;

public abstract class CriterionBuilder {

    protected final String shortDescription;

    protected CriterionBuilder(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    protected Criterion.Builder builderWithShortDescription() {
        return Criterion.builder().shortDescription(shortDescription);
    }

    protected static String codeTagify(String s) {
        return Pattern.compile("\\[\\[\\[(.+?)]]]")
            .matcher(s)
            .replaceAll(matchResult -> "\\\\<code\\\\>%s\\\\</code\\\\>".formatted(matchResult.group(1)));
    }

    public abstract Criterion build();

}
