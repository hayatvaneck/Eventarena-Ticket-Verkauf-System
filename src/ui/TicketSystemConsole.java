package ui;

import java.util.List;
import java.util.Scanner;
import repository.EventRepository;
import domain.Event;

public class TicketSystemConsole {
    private final EventRepository eventRepository = EventRepository.getInstance();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        boolean running = true;
        System.out.println("=== ARENA TICKETSYSTEM ===");

        while (running) {
            printMenu();
            System.out.print("Bitte wählen Sie eine Option (1-3): ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    showAllEvents();
                    break;
                case "2":
                    System.out.println("\nHier folgt bald die Buchung via BookingService...");
                    break;
                case "3":
                    running = false;
                    System.out.println("\nProgramm beendet.");
                    break;
                default:
                    System.out.println("\n[Fehler] Ungültige Eingabe!");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n---------------------------------");
        System.out.println("1. Verfügbare Events anzeigen");
        System.out.println("2. Tickets buchen");
        System.out.println("3. Beenden");
        System.out.println("\n---------------------------------");
    }

    private void showAllEvents() {
        System.out.println("\n--- Aktuelle Events in der Arena ---");

        // Lädt die Daten aus dem Repository
        List<Event> events = eventRepository.getAllEvents();

        if (events.isEmpty()) {
            System.out.println("Aktuell keine Events geplant.");
            return;
        }

        // Schleife durch alle Events im Repo
        for (Event e : events) {
            System.out.printf("ID: %d | %-35s | Datum: %s | Preis ab: %.2f EUR | Tickets verfügbar: %d%n",
                e.getId(),
                e.getTitle(),
                e.getDateTime(),
                e.getBasePrice(),
                e.getTotalAvailableSeats());
        }
    }

    public static void main(String[] args) {
        TicketSystemConsole app = new TicketSystemConsole();
        app.start();
    }
}