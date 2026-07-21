package service;

import domain.*;
import exceptions.*;
import repository.*;
import java.util.ArrayList;
import java.util.List;

public class BookingService {
    
    private final EventRepository eventRepo;
    private final TicketRepository ticketRepo;
    private final List<Ticket> activeTickets;
    private long ticketIdCounter;

    public BookingService() {
        this.eventRepo = EventRepository.getInstance();
        this.ticketRepo = TicketRepository.getInstance();
        this.activeTickets = new ArrayList<>();
        this.ticketIdCounter = 1000L + ticketRepo.findAll().size(); // Ticketnummern starten bei 1000.

        restoreBookedSeatsFromRepository();
    }

    public Ticket bookTicket (Long eventId, String sectionName, Customer customer, String userEmail) throws SeatAlreadyBookedException{
        // 1. Event suchen
        Event event = eventRepo.findById(eventId);
        if(event == null) {
            throw new IllegalArgumentException("Event mit ID " + eventId + " wurde nicht gefunden.");
        }

        // 2. Sections im Event suchen
        Section section = event.findSectionByName(sectionName);
        if(section == null) {
            throw new IllegalArgumentException("Der Block '" + sectionName + "' existiert für dieses Event nicht.");
        }

        // 3. Platz reservieren
        boolean reservationSuccessful = section.bookNextAvailableTicket();

        if(!reservationSuccessful) {
            throw new SeatAlreadyBookedException("Der Bereich '" + sectionName + "' ist ausgebucht oder gesperrt.");
        }

        // 4. Ticket-ID generieren und Preis berechnen
        ticketIdCounter++;
        String generatedTicektId = "T-" + ticketIdCounter;
        double basePrice = event.getBasePrice() * section.getPriceFactor();
        String customerType = (customer != null) ? customer.getCustomerType() : "Standard";
        double discountFactor = calculateDiscountFactor(customerType);
        double finalPrice = basePrice * discountFactor;
        String seatInfo = "Freie Platzwahl (Stehplatz)";

        // 5. Ticket Objekt erstellen und im Service speichern
        Ticket newTicket = new Ticket(
            generatedTicektId, 
            event, 
            section, 
            customer, 
            finalPrice, 
            seatInfo, 
            userEmail,
            customerType,
            basePrice
        );
        activeTickets.add(newTicket);
        ticketRepo.save(newTicket);

        return newTicket;
    }

    public Ticket bookSpecificTicket(Long eventId, String sectionName, int row, int seatNumber, Customer customer, String userEmail) throws SeatAlreadyBookedException {
        // 1. Event und Section suchen
        Event event = eventRepo.findById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event mit ID " + eventId + " wurde nicht gefunden.");
        }

        Section section = event.findSectionByName(sectionName);
        if (section == null) {
            throw new IllegalArgumentException("Der Block '" + sectionName + "' existiert nicht.");
        }

        // 2. Check ob es ein Sitzplatz-Block ist
        if(!(section instanceof SeatedSection)) {
            throw new IllegalArgumentException("Der Bereich '" + sectionName + "' erlaubt keine gezielte Platzwahl.");
        }

        SeatedSection seatedSection = (SeatedSection) section;

        // 3. Konkreten Sitzplatz holen
        Seat chosenSeat = seatedSection.getSeat(row, seatNumber);
        if (chosenSeat == null) {
            throw new IllegalArgumentException("Der Platz (Reihe " + row + ", Platz " + seatNumber + ") existiert in diesem Block nicht.");
        }

        // 4. Platz buchen
        chosenSeat.book();

        // 5. Ticket generieren
        ticketIdCounter++;
        String generatedTicketId = "T-" + ticketIdCounter;
        double basePrice = event.getBasePrice() * section.getPriceFactor();
        String customerType = (customer != null) ? customer.getCustomerType() : "Standard";
        double discountFactor = calculateDiscountFactor(customerType);
        double finalPrice = basePrice * discountFactor;
        String seatInfo = "Reihe " + row + ", Platz " + seatNumber;

        Ticket newTicket = new Ticket(
            generatedTicketId, 
            event, 
            section, 
            customer, 
            finalPrice, 
            seatInfo, 
            userEmail,
            customerType,
            basePrice
        );
        activeTickets.add(newTicket);
        ticketRepo.save(newTicket);

        return newTicket;
    }

    private double calculateDiscountFactor(String customerType) {
        if (customerType == null) {
            return 1.0;
        }
        switch (customerType) {
            case "Student":
                return 0.80;
            case "Rentner":
                return 0.7;
            case "Kind":
                return 0.5;
            default:
                return 1.0;
        }
    }

    // Liste aller aktiven Tickets im System
    public List<Ticket> getActiveTickets() {
        return new ArrayList<>(this.activeTickets);
    }

    public boolean cancelTicket(Ticket ticket, User user) {
        if (ticket == null || user == null) {
            return false;
        }

        Section section = ticket.getSection();
        if (section == null) {
            return false;
        }

        boolean seatReleased = false;

        if (section instanceof SeatedSection) {
            SeatedSection seatedSection = (SeatedSection) section;

            int[] rowAndSeat = parseRowAndSeat(ticket.getSeatInfo());

            if (rowAndSeat[0] > 0 && rowAndSeat[1] > 0) {
                seatedSection.releaseSeat(rowAndSeat[0], rowAndSeat[1]);
                seatReleased = true;
            }

        } else if (section instanceof StandingSection) {
            StandingSection standingSection = (StandingSection) section;
            seatReleased = standingSection.releaseStandingTicket();
        }

        if (seatReleased) {
            user.removeTicket(ticket);
            TicketRepository.getInstance().deleteTickets(ticket.getTicketId());
            return true;
        }

        return false;
    }

    private int[] parseRowAndSeat (String seatInfo) {
        if (seatInfo == null) {
            return new int[]{0, 0};
        }
        try {
            String[] numbers = seatInfo.replaceAll("[^0-9]+", " ").trim().split("\\s+");
            if (numbers.length >= 2) {
                int row = Integer.parseInt(numbers[0]);
                int seat = Integer.parseInt(numbers[1]);
                return new int[]{row, seat};
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Parsen der Sitzplatz-Info: " + seatInfo);
        }
        return new int[]{0, 0};
    }

    public void restoreBookedSeatsFromRepository() {
        List<Ticket> savedTickets = ticketRepo.findAll();

        for (Ticket ticket : savedTickets) {
            if (!activeTickets.contains(ticket)) {
                activeTickets.add(ticket);
            }

            Section section = ticket.getSection();
            if (section == null) {
                continue;
            }

            try {
                if (section instanceof SeatedSection) {
                    SeatedSection seatedSection = (SeatedSection) section;
                    int[] rowAndSeat = parseRowAndSeat(ticket.getSeatInfo());

                    if (rowAndSeat[0] > 0 && rowAndSeat[1] > 0) {
                        Seat seat = seatedSection.getSeat(rowAndSeat[0], rowAndSeat[1]);
                        if (seat != null) {
                            seat.book();
                        }
                    }
                } else if (section instanceof StandingSection) {
                    section.bookNextAvailableTicket();
                }
            } catch (SeatAlreadyBookedException e) {
                System.err.println("Warnung beim Wiederherstellen der Plätze: " + e.getMessage());
            }
        }
    }
}
