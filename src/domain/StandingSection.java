package domain;

public class StandingSection extends Section {
    private final int capacity;
    private int soldTickets;

    public StandingSection(String name, double priceFactor, int capacity) {
        super(name, priceFactor);

        if (capacity <= 0) {
            throw new IllegalArgumentException("Kapazität muss größer als 0 sein.");
        }

        this.capacity = capacity;
        this.soldTickets = 0;
    }

    public boolean bookNextAvailableTicket() {
        if (soldTickets < capacity) {
            soldTickets++;
            return true;
        }
        return false;
    }

    @Override
    public int getAvailableSeats() {
        return capacity - soldTickets;
    }

    @Override
    public void printLayout() {
        System.out.println("\nStatus für Bereich: " + getName());
        System.out.printf("Typ: STEHPLÄTZE | Verkauft: %d/%d | Verfügbar: %d%n",
                soldTickets, capacity, getAvailableSeats());

        int barLength = 10;
        int filledLength = (int) (((double) soldTickets / capacity) * barLength);

        System.out.print("Auslastung: [");
        for (int i = 0; i < barLength; i++) {
            if (i < filledLength) {
                System.out.print("#");
            } else {
                System.out.print("-");
            }
        }
        System.out.println("]\n");
    }

    public int getCapacity() {
        return capacity;
    }

    public int getSoldTickets() {
        return soldTickets;
    }
}