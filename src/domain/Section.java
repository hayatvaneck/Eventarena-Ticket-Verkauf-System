package domain;

import exceptions.SeatAlreadyBookedException;

/**
 * Die abstrakte Klasse Section definiert die gemeinsame Basis für alle buchbaren und nicht buchbaren Bereiche.

 */

public abstract class Section {
    /** Eindeutiger Anzeigename des Bereichs innerhalb eines Events. */
    private final String name;
    /** Multiplikator für den Basispreis des Events. */
    private final double priceFactor;

    /**
     * Initialisiert die gemeinsamen Daten aller Bereichsarten.
     *
     * @param name nicht leerer Bereichsname
     * @param priceFactor nicht negativer Faktor für den Ticketpreis
     * @throws IllegalArgumentException wenn Name oder Faktor ungültig sind
     */
    public Section(String name, double priceFactor) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name darf nicht leer sein.");
        }
        if (priceFactor < 0) {
            throw new IllegalArgumentException("Preisfaktor darf nicht negativ sein.");
        }

        this.name = name;
        this.priceFactor = priceFactor;
    }

    /**
     * Reserviert die nächste verfügbare Kapazität des Bereichs.
     *
     * @return {@code true}, wenn eine Reservierung durchgeführt wurde
     * @throws SeatAlreadyBookedException wenn eine konkrete Platzbuchung kollidiert
     */
    public abstract boolean bookNextAvailableTicket() throws SeatAlreadyBookedException;
    /** @return Anzahl der noch verfügbaren Tickets in diesem Bereich */
    public abstract int getAvailableSeats();
    /** Gibt eine einfache textuelle Darstellung des Bereichs auf der Konsole aus. */
    public abstract void printLayout();

    /** @return Name des Bereichs */
    public String getName() {
        return name;
    }

    /** @return Preisfaktor relativ zum Event-Basispreis */
    public double getPriceFactor() {
        return priceFactor;
    }
}


