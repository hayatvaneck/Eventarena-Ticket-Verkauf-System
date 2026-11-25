package domain;
import exceptions.SeatAlreadyBookedException;

/**
 * Die Klasse EmptySection repräsentiert einen nicht buchbaren Innenraum wie Bühne oder Spielfläche.

 */

public class EmptySection extends Section {
    /**
     * Erstellt einen benannten, grundsätzlich nicht buchbaren Bereich.
     *
     * @param name Bezeichnung wie Bühne oder Innenraum
     */
    public EmptySection(String name) {
        super(name, 0.0);
    }

    /**
     * Lehnt jede Buchung ab.
     *
     * @return immer {@code false}
     */
    @Override
    public boolean bookNextAvailableTicket() throws SeatAlreadyBookedException {
        System.out.println("[INFO] Dieser Bereich ist für das Event " + getName() + " nicht verfügbar.");
        return false; // Keine Buchungen möglich
    }

    /** @return immer {@code 0}, da der Bereich nicht buchbar ist */
    @Override
    public int getAvailableSeats() {
        return 0; // Keine verfügbaren Plätze
    }

    /** Kennzeichnet den Bereich in der Konsolenausgabe als gesperrt. */
    @Override
    public void printLayout() {
        System.out.println("\nBereich: " + getName());
        System.out.println("Typ: GESPERRT | SPIELFLÄCHE | BÜHNE");
        System.out.println("Status: Für Zuschauer nicht verfügbar.\n");
    }
}



