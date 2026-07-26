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

    private static final int OUTER_BLOCK_ROWS = 10;
    private static final int OUTER_BLOCK_SEATS_PER_ROW = 20;
    private static final int INTERIOR_ROWS = (int) (OUTER_BLOCK_ROWS * 1.5);

    public enum InteriorMode {
        STANDING,
        SEATED,
        EMPTY
    }

    private HallLayoutFactory() {
    }

    public static void applyLayoutForMapType(Event event) {
        if (event == null) {
            return;
        }

        applyLayoutForMapType(event, event.getMapType());
    }

    public static void applyLayoutForMapType(Event event, MapType mapType) {
        if (event == null) {
            return;
        }

        InteriorMode interiorMode = inferInteriorModeFromMapType(mapType);
        applyStandardLayout(event, interiorMode);
    }

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



