package domain;
import exceptions.SeatAlreadyBookedException;

/**
 * Die Klasse StandingSection verwaltet einen Stehplatzbereich mit Kapazitaet und Verkaufsstand.

 */

public class StandingSection extends Section {
    private final int capacity;
    private int soldTickets;
    
    public StandingSection(String name, double priceFactor, int capacity) {
        super(name, priceFactor);
        this.capacity = capacity;
        this.soldTickets = 0;
    }

    @Override
    public boolean bookNextAvailableTicket() throws SeatAlreadyBookedException {
        if (this.soldTickets < capacity) {
            this.soldTickets++;
            return true;
        }
        return false; // Keine verfÃ¼gbaren PlÃ¤tze mehr
    }

    @Override
    public int getAvailableSeats() {
        return this.capacity - this.soldTickets;
    }

    @Override
    public void printLayout() {
        System.out.println("\nStatus fÃ¼r Bereich: " + getName());
        System.out.printf("Typ: STEHPLÃ„TZE | Verkauft: %d/%d | VerfÃ¼gbar: %d%n",
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

    public boolean releaseStandingTicket() {
        if (this.soldTickets > 0) {
            this.soldTickets--;
            return true;
        }
        return false;
    }

    public void incrementSoldTickets() {
        if (this.soldTickets < this.capacity) {
            this.soldTickets++;
        }
    }

    public int getCapacity() {
        return capacity;
    }
}



