package domain;
import exceptions.SeatAlreadyBookedException;
import repository.EventRepository;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== STARTING ARENA TICKETING SYSTEM ===");

        EventRepository repo = EventRepository.getInstance();

        /*
        // Neues Event erstellen
        Event concert = new Event(1L, "Don Toliver", LocalDateTime.of(2026, 11, 02, 19, 0), 50.0);

        // Sections hinzufügen
        Section standing = new StandingSection("Stehplatz Innenraum", 1.0, 10);
        Section seatedASection = new SeatedSection("Sitzplatz Block A", 1.5, 10, 20);
        Section seatedBSection = new SeatedSection("Sitzplatz Block B", 1.2, 8, 15);
        Section stageSection = new EmptySection("Bühne");

        concert.addSection(standing);
        concert.addSection(seatedASection);
        concert.addSection(seatedBSection);
        concert.addSection(stageSection);
        */

        System.out.println("\n=== EVENT DETAILS ===");
        System.out.println("Event: " + concert.getTitle());
        System.out.println("Datum & Uhrzeit: " + concert.getDateTime());
        System.out.println("Verfügbare Tickets insgesamt: " + concert.getTotalAvailableSeats());

        // Layouts der Sections anzeigen
        System.out.println("\n=== SECTION LAYOUTS ===");
        standing.printLayout();
        seatedASection.printLayout();
        seatedBSection.printLayout();

        // Buchungen simulieren
        System.out.println("\n=== BUCHUNG STARTEN ===");
        try {
            // Sitzplatz buchen
            System.out.println("\nBuche einen Sitzplatz in Block A:");
            boolean successSeatA = seatedASection.bookNextAvailableTicket();
            System.out.println("Buchung erfolgreich: " + successSeatA);

            seatedASection.printLayout();

            // Stehplätze buchen
            System.out.println("\nBuche eine Stehplatz im Innenraum:");
            for(int i = 1; i <= 5; i++) {
                boolean successStanding = standing.bookNextAvailableTicket();
                System.out.println("Buchung " + i + " erfolgreich: " + successStanding);
                }
            standing.printLayout();

            // Buchungsversuch in EmptySection
            System.out.println("\nVersuche Platz auf der Bühne zu buchen...");
            boolean successStage = stageSection.bookNextAvailableTicket();
            System.out.println("Buchung geklappt? " + successStage);
        } catch (SeatAlreadyBookedException e) {
            System.out.println("[FEHLER] Da lief etwas mit den Sitzen schief: " + e.getMessage());
        }

        // Finaler Status des Events
        System.out.println("\n=== Finaler Zustand des Events===");
        System.out.println("Verfügbare Tickets übrig: " + concert.getTotalAvailableSeats());
        System.out.println("Komplett ausgebucht? " + concert.isSoldOut());
    }
}
