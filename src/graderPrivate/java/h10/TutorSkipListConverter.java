package h10;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

/**
 * Converter for converting a JSON object to a {@link TutorSkipList}.
 *
 * @author Nhan Huynh
 */
public class TutorSkipListConverter implements ArgumentConverter {

    /**
     * Converts the given source to an object of the type of the parameter.
     */
    private final SkipListConverter converter = new SkipListConverter();

    @Override
    @SuppressWarnings("unchecked")
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
        SkipList<Integer> list = (SkipList<Integer>) converter.convert(source, context);
        TutorSkipList<Integer> tutorList = new TutorSkipList<>(list.cmp, list.maxHeight, list.getProbability());
        tutorList.head = list.head;
        tutorList.height = list.height;
        return tutorList;
    }

}
