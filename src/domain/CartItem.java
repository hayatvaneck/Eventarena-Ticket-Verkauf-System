package domain;

/**
 * Bündelt eine noch nicht gebuchte Auswahl aus Veranstaltung, Bereich und
 * Platz. Die UI verwendet das Objekt als unveränderlichen Warenkorbeintrag.
 */
public class CartItem {
    /** Veranstaltung, für die das Ticket ausgewählt wurde. */
    private final Event event;
    /** Gewählter Sitz- oder Stehplatzbereich der Veranstaltung. */
    private final Section section;
    /** Gewählter Sitz beziehungsweise technischer Platzhalter bei Stehplätzen. */
    private final Seat seat;

    /**
     * Erstellt einen Warenkorbeintrag aus den drei Bestandteilen der Auswahl.
     *
     * @param event Veranstaltung der Auswahl
     * @param section gewählter Veranstaltungsbereich
     * @param seat gewählter oder für Stehplätze erzeugter Platz
     */
    public CartItem(Event event, Section section, Seat seat) {
        this.event = event;
        this.section = section;
        this.seat = seat;
    }

    /** @return Veranstaltung dieses Warenkorbeintrags */
    public Event getEvent() {
        return event;
    }

    /** @return gewählter Veranstaltungsbereich */
    public Section getSection() {
        return section;
    }

    /** @return gewählter Platz */
    public Seat getSeat() {
        return seat;
    }
}
