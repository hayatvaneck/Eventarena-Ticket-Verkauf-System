package domain;
import exceptions.SeatAlreadyBookedException;

/**
 * Die Klasse StandingSection verwaltet einen Stehplatzbereich mit Kapazität und Verkaufsstand.

 */

public class StandingSection extends Section {
    /** Maximale Anzahl gleichzeitig verkaufbarer Stehplatztickets. */
    private final int capacity;
    /** Aktuell verkaufte Anzahl von Stehplatztickets. */
    private int soldTickets;

    /**
     * Erstellt einen Stehplatzbereich mit leerem Verkaufsstand.
     *
     * @param name Name des Bereichs
     * @param priceFactor Preisfaktor relativ zum Event
     * @param capacity maximale Ticketanzahl
     */
    public StandingSection(String name, double priceFactor, int capacity) {
        super(name, priceFactor);
        this.capacity = capacity;
        this.soldTickets = 0;
    }

    /**
     * Reserviert eine Einheit der Stehplatzkapazität.
     *
     * @return {@code true}, wenn noch Kapazität vorhanden war
     */
    @Override
    public boolean bookNextAvailableTicket() throws SeatAlreadyBookedException {
        if (this.soldTickets < capacity) {
            this.soldTickets++;
            return true;
        }
        return false; // Keine verfügbaren Plätze mehr
    }

    /** @return noch nicht verkaufte Stehplatztickets */
    @Override
    public int getAvailableSeats() {
        return this.capacity - this.soldTickets;
    }

    /** Gibt Kapazität und Auslastung als Konsolenübersicht aus. */
    @Override
    public void printLayout() {
        System.out.println("\nStatus für Bereich: " + getName());
        System.out.printf("Typ: STEHPLÄTZE | Verkauft: %d/%d | Verfügbar: %d%n",
                this.soldTickets, this.capacity, this.getAvailableSeats());

        // Visueller Balken fÃ¼r die Konsole
        int barLength = 10;
        int percentageFilled = (int) (((double) this.soldTickets / this.capacity) * barLength);

        System.out.print("Auslastung: [");
        for (int i = 0; i < barLength; i++) {
            if (i < percentageFilled) {
                System.out.print("#");
            } else {
                System.out.print("-");
            }
        }
        System.out.println("]\n");
    }

    /**
     * Gibt bei einer Stornierung eine verkaufte Kapazitätseinheit zurück.
     *
     * @return {@code true}, wenn ein Ticket freigegeben werden konnte
     */
    public boolean releaseStandingTicket() {
        if (this.soldTickets > 0) {
            this.soldTickets--;
            return true;
        }
        return false;
    }

    /**
     * Erhöht den Verkaufsstand, ohne die Kapazitätsgrenze zu überschreiten.
     * Wird insbesondere beim Wiederherstellen gespeicherter Buchungen verwendet.
     */
    public void incrementSoldTickets() {
        if (this.soldTickets < this.capacity) {
            this.soldTickets++;
        }
    }

    /** @return maximale Kapazität des Bereichs */
    public int getCapacity() {
        return capacity;
    }
}



