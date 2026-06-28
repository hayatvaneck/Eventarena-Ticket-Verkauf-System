package ui;

import java.util.List;
import java.util.Scanner;
import repository.EventRepository;
import domain.*;
import exceptions.*;
import service.BookingService;

public class TicketSystemConsole {
    private final EventRepository eventRepository = EventRepository.getInstance();
    private final Scanner scanner = new Scanner(System.in);
    private final BookingService bookingService = new BookingService();
    private long customerIdCounter = 1L;

    public void start() {
        boolean running = true;
        System.out.println("======================================================");
        System.out.println("================ ARENA TICKETSYSTEM ==================");
        System.out.println("======================================================");

        while (running) {
            printMenu();
            System.out.print("Bitte wählen Sie eine Option (1-3): ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    showAllEvents();
                    break;
                case "2":
                    processBooking();
                    break;
                case "3":
                    running = false;
                    System.out.println("\nVielen Dank für Ihren Besuch. Bis zum nächsten Mal!");
                    break;
                default:
                    System.out.println("\n[Fehler] Ungültige Eingabe! Bitte wählen Sie 1, 2 oder 3.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n---- HAUPTMENÜ ----");
        System.out.println("1. Verfügbare Events und Hallenpläne anzeigen");
        System.out.println("2. Tickets interaktiv buchen");
        System.out.println("3. Anwendung beenden");
        System.out.println("-------------------");
    }

    private void showAllEvents() {
        System.out.println("\n---- AKTUELLE VERANSTALTUNGEN ----");

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

    // Interaktiver Buchungsprozess
    private void processBooking() {
        System.out.println("\n---- TICKETBUCHUNG STARTEN ----");

        // 1. Event-ID abfragen
        System.out.print("Geben Sie die ID des gewünschten Events ein: ");
        String idInput = scanner.nextLine();
        Long eventId;
        try {
            eventId = Long.parseLong(idInput);
        } catch (NumberFormatException e) {
            System.out.println("[FEHLER] Die ID muss eine zahl sein!");
            return;
        }

        Event selectedEvent = eventRepository.findById(eventId);
        if(selectedEvent == null) {
            System.out.println("[FEHLER] Es existiert kein Event mit dieser ID.");
            return;
        }

        System.out.println("\nVerfügbare Bereiche für '" + selectedEvent.getTitle() + "':");
        for (Section section : selectedEvent.getSections()) {
            section.printLayout();
        }

        // 2. Blocknamen abfragen
        System.out.println("Geben Sie den exakten Namen des Bereichs ein: ");
        String sectionName = scanner.nextLine();
        Section selectedSection = selectedEvent.findSectionByName(sectionName);

        if (selectedSection == null) {
            System.out.println("[FEHLER] Dieser Bereich existiert nicht.");
        }

        // 3. Kundendaten abfragen
        System.out.print("Geben Sie Ihren Vornamen ein: ");
        String firstName = scanner.nextLine();
        System.out.print("Geben Sie Ihren Nachnamen ein: ");
        String lastName = scanner.nextLine();
        System.out.print("Kundentyp eingeben (NORMAL, STUDENT, RENTNER, VIP): ");
        String customerType = scanner.nextLine().toUpperCase();

        if (firstName.isBlank() || lastName.isBlank()) {
            System.out.println("[FEHLER] Name darf nicht leer sein!");
            return;
        }

        Customer customer = new Customer(customerIdCounter++, firstName, lastName, customerType);

        // 4. Buchung ausführen und absichern
        try {
            System.out.println("\n[SYSTEM] Verarbeite Buchung...");
            Ticket ticket;

            if (selectedSection instanceof SeatedSection) {
                System.out.print("In welcher Reihe möchten Sie sitzen? ");
                int row = Integer.parseInt(scanner.nextLine());
                System.out.print("Welche Sitznummer möchten Sie haben? ");
                int seatNumber = Integer.parseInt(scanner.nextLine());

                ticket = bookingService.bookSpecificTicket(eventId, sectionName, row, seatNumber, customer);
                System.out.println("\n[ERFOLG] Ihr gewählter Sitzplatz wurde reserviert!");
            } else {
                ticket = bookingService.bookTicket(eventId, sectionName, customer);
            }

            // Erfolg: Ticket ausdrucken/anzeigen
            ticket.printTicketDetails();
        } catch (NumberFormatException e) {
            // Fängt falsche Angaben für Sitz und Reihe ab
            System.out.println("\n[EINGABEFEHLER] Reihe und Sitznummer müssen Zahlen sein!");
        } catch (SeatAlreadyBookedException e) {
            // Fängt ab, wenn der Block voll oder gesperrt ist
            System.out.println("\n[BUCHUNGSFEHLER] " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // Fängt falschen Blocknamen oder Event-ID ab
            System.out.println("\n[EINGABEFEHLER] " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        TicketSystemConsole app = new TicketSystemConsole();
        app.start();
    }
}