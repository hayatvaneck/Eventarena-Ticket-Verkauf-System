package domain.layout;

import domain.EmptySection;
import domain.Event;
import domain.SeatedSection;
import domain.StandingSection;

/**
 * Die Klasse HallLayoutFactory erstellt das feste Hallenlayout und wählt die Innenraum-Nutzung je Event.
 */

public final class HallLayoutFactory {

    public enum InteriorMode {
        STANDING,
        SEATED,
        EMPTY
    }

    private HallLayoutFactory() {
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
                event.addSection(new SeatedSection("Innenraum", 1.0, 15, 12));
                break;
            case EMPTY:
                event.addSection(new EmptySection("Innenraum"));
                break;
            default:
                event.addSection(new StandingSection("Innenraum", 1.0, 2000));
                break;
        }

        // Feste Hallenstruktur: diese Blöcke sind für alle Events gleich.
        event.addSection(new SeatedSection("Block 1", 1.2, 10, 20));
        event.addSection(new SeatedSection("Block 2", 1.2, 10, 20));
        event.addSection(new SeatedSection("Block 3", 1.2, 10, 20));
        event.addSection(new SeatedSection("Block 4", 1.2, 10, 20));
        event.addSection(new SeatedSection("Block 5", 1.0, 10, 20));
        event.addSection(new SeatedSection("Block 6", 0.8, 10, 20));
        event.addSection(new SeatedSection("VIP", 2.5, 3, 15));
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



