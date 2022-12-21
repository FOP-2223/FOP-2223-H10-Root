package h10;

import org.sourcegrade.jagr.api.rubric.Criterion;
import org.sourcegrade.jagr.api.rubric.Rubric;
import org.sourcegrade.jagr.api.rubric.RubricProvider;

import static h10.PublicTutorUtils.criterion;

/**
 * Defines the public rubric for the assignment H10.
 *
 * @author Nhan Huynh
 */
public final class H10_RubricProvider implements RubricProvider {

    /**
     * Defines the public rubric for the task H1.
     */
    private static final Criterion H1 = criterion("H1 | SkipList#contains(Object)", H1_PublicTests.class,
        "03 | Methode findet die Elemente mit einer minimalen Anzahl an Vergleichen in der obersten Ebene.",
        "04 | Methode findet die Elemente mit einer minimalen Anzahl an Vergleichen in den Zwischenebenen.",
        "05 | Methode findet die Elemente mit einer minimalen Anzahl an Vergleichen in der untersten Ebene.");

    /**
     * Defines the public rubric for the task H2.
     */
    private static final Criterion H2 = criterion("H2 | SkipList#add(Object)", H2_PublicTests.class,
        "12 | Methode erstellt neue Ebenen korrekt.",
        "13 | Methode fügt Element in vorhandenen Ebenen korrekt.",
        "14 | Methode fügt Element in allen Ebenen korrekt.",
        "15 | Methode fügt Element vorne/hinten korrekt.",
        "16 | Methode setzt die Referenzen nach oben/unten korrekt."
    );

    /**
     * Defines the public rubric for the task H3.
     */
    private static final Criterion H3 = criterion("H3 | SkipList#remove(Object)", H3_PublicTests.class,
        "22 | Methode entfernt das letzte Element korrekt.",
        "23 | Methode entfernt die Elemente korrekt.",
        "24 | Methode entfernt alle Elemente korrekt.",
        "25 | Methode setzt die Referenzen korrekt."
    );

    @Override
    public Rubric getRubric() {
        return Rubric.builder()
            .title("H10 | Verzeigerte Strukturen - Public Tests")
            .addChildCriteria(H1, H2, H3)
            .build();
    }

}
