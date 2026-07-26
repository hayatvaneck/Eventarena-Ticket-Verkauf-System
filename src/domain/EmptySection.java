package domain;
import exceptions.SeatAlreadyBookedException;

/**
 * Die Klasse EmptySection repraesentiert einen nicht buchbaren Innenraum wie Buehne oder Spielflaeche.

 */

public class EmptySection extends Section {
    public EmptySection(String name) {
        super(name, 0.0);
    }

    @Override
    public boolean bookNextAvailableTicket() throws SeatAlreadyBookedException {
        System.out.println("[INFO] Dieser Bereich ist fÃ¼r das Event " + getName() + " nicht verfÃ¼gbar.");
        return false; // Keine Buchungen mÃ¶glich
    }

    @Override
    public int getAvailableSeats() {
        return 0; // Keine verfÃ¼gbaren PlÃ¤tze
    }

    @Override
    public void printLayout() {
        System.out.println("\nBereich: " + getName());
        System.out.println("Typ: GESPERRT | SPIELFLÃ„CHE | BÃœHNE");
        System.out.println("Status: FÃ¼r Zuschauer nicht verfÃ¼gbar.\n");
    }
}



