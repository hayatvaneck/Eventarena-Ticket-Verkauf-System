package domain.layout;

import domain.EmptySection;
import domain.Event;
import domain.Event.MapType;
import domain.SeatedSection;
import domain.StandingSection;

/**
 * Die Klasse HallLayoutFactory erstellt das feste Hallenlayout und wählt die Innenraum-Nutzung je Event.
 */

public final class HallLayoutFactory {

    /** Einheitliche Reihenanzahl der äußeren Sitzblöcke. */
    private static final int OUTER_BLOCK_ROWS = 10;
    /** Einheitliche Sitzanzahl pro Reihe der äußeren Blöcke. */
    private static final int OUTER_BLOCK_SEATS_PER_ROW = 20;
    /** Reihenanzahl eines bestuhlten Innenraums. */
    private static final int INTERIOR_ROWS = (int) (OUTER_BLOCK_ROWS * 1.5);

    /** Beschreibt die konkrete Nutzung des zentralen Innenraums. */
    public enum InteriorMode {
        /** Innenraum wird als Stehplatzbereich verwendet. */
        STANDING,
        /** Innenraum wird mit nummerierten Sitzplätzen bestuhlt. */
        SEATED,
        /** Innenraum ist Bühne, Spielfeld oder anderweitig nicht buchbar. */
        EMPTY
    }

    /** Verhindert die Instanziierung der rein statischen Factory-Klasse. */
    private HallLayoutFactory() {
    }

    /**
     * Wendet das durch das Event vorgegebene Kartenlayout an.
     *
     * @param event Event, dessen Bereichsliste aufgebaut werden soll
     */
    public static void applyLayoutForMapType(Event event) {
        if (event == null) {
            return;
        }

        applyLayoutForMapType(event, event.getMapType());
    }

    /**
     * Wendet einen ausdrücklich übergebenen Saalplantyp auf das Event an.
     *
     * @param event zu befüllendes Event
     * @param mapType zu verwendender Saalplantyp
     */
    public static void applyLayoutForMapType(Event event, MapType mapType) {
        if (event == null) {
            return;
        }

        InteriorMode interiorMode = inferInteriorModeFromMapType(mapType);
        applyStandardLayout(event, interiorMode);
    }

    /**
     * Erstellt Innenraum, Außenblöcke und VIP-Bereich des Standardlayouts.
     *
     * @param event zu befüllendes Event
     * @param interiorMode Nutzung des zentralen Innenraums
     */
    public static void applyStandardLayout(Event event, InteriorMode interiorMode) {
        if (event == null) {
            return;
        }

        if (interiorMode == null) {
            interiorMode = InteriorMode.STANDING;
        }

        switch (interiorMode) {
            case STANDING:
                event.addSection(new StandingSection("Innenraum", 1.0, 2000));
                break;
            case SEATED:
                event.addSection(new SeatedSection("Innenraum", 1.0, INTERIOR_ROWS, OUTER_BLOCK_SEATS_PER_ROW));
                break;
            case EMPTY:
                event.addSection(new EmptySection("Innenraum"));
                break;
            default:
                event.addSection(new StandingSection("Innenraum", 1.0, 2000));
                break;
        }

        // Feste Hallenstruktur: diese Blöcke sind für alle Events gleich.
        event.addSection(new SeatedSection("Block 1", 1.2, OUTER_BLOCK_ROWS, OUTER_BLOCK_SEATS_PER_ROW));
        event.addSection(new SeatedSection("Block 2", 1.2, OUTER_BLOCK_ROWS, OUTER_BLOCK_SEATS_PER_ROW));
        event.addSection(new SeatedSection("Block 3", 1.2, OUTER_BLOCK_ROWS, OUTER_BLOCK_SEATS_PER_ROW));
        event.addSection(new SeatedSection("Block 4", 1.2, OUTER_BLOCK_ROWS, OUTER_BLOCK_SEATS_PER_ROW));
        event.addSection(new SeatedSection("Block 5", 1.0, OUTER_BLOCK_ROWS, OUTER_BLOCK_SEATS_PER_ROW));
        event.addSection(new SeatedSection("Block 6", 0.8, OUTER_BLOCK_ROWS, OUTER_BLOCK_SEATS_PER_ROW));
        event.addSection(new SeatedSection("VIP", 2.5, 3, 15));
    }

    /**
     * Übersetzt den öffentlichen Kartentyp in den internen Innenraummodus.
     *
     * @param mapType Kartentyp des Events
     * @return passender Innenraummodus
     */
    private static InteriorMode inferInteriorModeFromMapType(MapType mapType) {
        if (mapType == null) {
            return InteriorMode.STANDING;
        }

        switch (mapType) {
            case ARENA:
                return InteriorMode.EMPTY;
            case STAGE_SEATED:
                return InteriorMode.SEATED;
            case STAGE_STANDING:
            default:
                return InteriorMode.STANDING;
        }
    }

    /**
     * Erkennt den Innenraummodus anhand der bereits angelegten Bereichsobjekte.
     *
     * @param event zu untersuchendes Event
     * @return erkannter Modus, standardmäßig {@link InteriorMode#STANDING}
     */
    public static InteriorMode inferInteriorMode(Event event) {
        if (event == null || event.getSections() == null) {
            return InteriorMode.STANDING;
        }

        for (domain.Section section : event.getSections()) {
            if (section == null) {
                continue;
            }

            String sectionName = section.getName() != null ? section.getName().toLowerCase() : "";
            if (!sectionName.contains("innenraum")) {
                continue;
            }

            if (section instanceof StandingSection) {
                return InteriorMode.STANDING;
            }
            if (section instanceof SeatedSection) {
                return InteriorMode.SEATED;
            }
            if (section instanceof EmptySection) {
                return InteriorMode.EMPTY;
            }
        }

        return InteriorMode.STANDING;
    }
}



