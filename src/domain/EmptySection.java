package domain;
import exceptions.SeatAlreadyBookedException;

public class EmptySection extends Section {
    public EmptySection(String name) {
        super(name, 0.0);
    }

    @Override
    public boolean bookNextAvailableTicket() throws SeatAlreadyBookedException {
        System.out.println("[INFO] Dieser Bereich ist für das Event " + getName() + " nicht verfügbar.");
        return false; // Keine Buchungen möglich
    }

    @Override
    public int getAvailableSeats() {
        return 0; // Keine verfügbaren Plätze
    }

    @Override
    public void printLayout() {
        System.out.println("\nBereich: " + getName());
        System.out.println("Typ: GESPERRT | SPIELFLÄCHE | BÜHNE");
        System.out.println("Status: Für Zuschauer nicht verfügbar.\n");
    }
}
