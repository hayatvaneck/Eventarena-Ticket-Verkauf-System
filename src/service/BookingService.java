package service;

import domain.*;
import exceptions.*;
import repository.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse BookingService kapselt die zentrale Buchungslogik für Tickets,
 * Preise und Stornierungen.
 */
public class BookingService {

    private final EventRepository eventRepo;
    private final TicketRepository ticketRepo;
    private final List<Ticket> activeTickets;
    private long ticketIdCounter;

    public BookingService() {
        this.eventRepo = EventRepository.getInstance();
        this.ticketRepo = TicketRepository.getInstance();
        this.activeTickets = new ArrayList<>();
        this.ticketIdCounter = 1000L + ticketRepo.findAll().size();

        restoreBookedSeatsFromRepository();
    }

    public Ticket bookTicket(Long eventId, String sectionName, Customer customer, String userEmail)
            throws SeatAlreadyBookedException {
        Event event = eventRepo.findById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event mit ID " + eventId + " wurde nicht gefunden.");
        }

        Section section = event.findSectionByName(sectionName);
        if (section == null) {
            throw new IllegalArgumentException("Der Block '" + sectionName + "' existiert für dieses Event nicht.");
        }

        boolean reservationSuccessful = section.bookNextAvailableTicket();

        if (!reservationSuccessful) {
            throw new SeatAlreadyBookedException("Der Bereich '" + sectionName + "' ist ausgebucht oder gesperrt.");
        }

        ticketIdCounter++;
        String generatedTicektId = "T-" + ticketIdCounter;
        double basePrice = event.getBasePrice() * section.getPriceFactor();
        CustomerType customerType = (customer != null && customer.getCustomerType() != null)
                ? customer.getCustomerType()
                : CustomerType.STANDARD;
        double discountFactor = calculateDiscountFactor(customerType);
        double finalPrice = basePrice * discountFactor;
        String seatInfo = "Freie Platzwahl (Stehplatz)";

        Ticket newTicket = new Ticket(
                generatedTicektId,
                event,
                section,
                customer,
                finalPrice,
                seatInfo,
                userEmail,
                customerType.name(),
                basePrice);

        activeTickets.add(newTicket);
        ticketRepo.save(newTicket);

        return newTicket;
    }

    public Ticket bookSpecificTicket(Long eventId, String sectionName, int row, int seatNumber, Customer customer,
            String userEmail) throws SeatAlreadyBookedException {
        Event event = eventRepo.findById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event mit ID " + eventId + " wurde nicht gefunden.");
        }

        Section section = event.findSectionByName(sectionName);
        if (section == null) {
            throw new IllegalArgumentException("Der Block '" + sectionName + "' existiert nicht.");
        }

        if (!(section instanceof SeatedSection)) {
            throw new IllegalArgumentException("Der Bereich '" + sectionName + "' erlaubt keine gezielte Platzwahl.");
        }

        SeatedSection seatedSection = (SeatedSection) section;

        Seat chosenSeat = seatedSection.getSeat(row, seatNumber);
        if (chosenSeat == null) {
            throw new IllegalArgumentException(
                    "Der Platz (Reihe " + row + ", Platz " + seatNumber + ") existiert in diesem Block nicht.");
        }

        chosenSeat.book();

        ticketIdCounter++;
        String generatedTicketId = "T-" + ticketIdCounter;
        double basePrice = event.getBasePrice() * section.getPriceFactor();
        CustomerType customerType = (customer != null && customer.getCustomerType() != null)
                ? customer.getCustomerType()
                : CustomerType.STANDARD;
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
                customerType.name(),
                basePrice);

        activeTickets.add(newTicket);
        ticketRepo.save(newTicket);

        return newTicket;
    }

    public double calculateDiscountFactor(CustomerType customerType) {
        if (customerType == null) {
            return 1.0;
        }
        switch (customerType) {
            case STUDENT:
                return 0.80;
            case SENIOR:
                return 0.7;
            case KIND:
                return 0.5;
            default:
                return 1.0;
        }
    }

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

    private int[] parseRowAndSeat(String seatInfo) {
        if (seatInfo == null) {
            return new int[] { 0, 0 };
        }
        try {
            String[] numbers = seatInfo.replaceAll("[^0-9]+", " ").trim().split("\\s+");
            if (numbers.length >= 2) {
                int row = Integer.parseInt(numbers[0]);
                int seat = Integer.parseInt(numbers[1]);
                return new int[] { row, seat };
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Parsen der Sitzplatz-Info: " + seatInfo);
        }
        return new int[] { 0, 0 };
    }

    public void restoreBookedSeatsFromRepository() {
        List<Ticket> savedTickets = ticketRepo.findAll();

        for (Ticket ticket : savedTickets) {
            if (!activeTickets.contains(ticket)) {
                activeTickets.add(ticket);
            }

            Event event = eventRepo.findById(ticket.getEvent().getId());
            if (event == null) {
                continue;
            }

            String sectionName = ticket.getSection() != null
                    ? ticket.getSection().getName()
                    : null;

            if (sectionName == null) {
                continue;
            }

            Section section = event.findSectionByName(sectionName);
            if (section == null) {
                continue;
            }

            try {
                if (section instanceof SeatedSection) {
                    SeatedSection seatedSection = (SeatedSection) section;
                    int[] rowAndSeat = parseRowAndSeat(ticket.getSeatInfo());

                    int row = rowAndSeat[0];
                    int seatNumber = rowAndSeat[1];

                    if (row > 0 && seatNumber > 0) {
                        Seat seat = seatedSection.getSeat(row, seatNumber);
                        if (seat != null && !seat.isBooked()) {
                            seat.book();
                        }
                    }
                } else if (section instanceof StandingSection) {
                    StandingSection standingSection = (StandingSection) section;
                    standingSection.bookNextAvailableTicket();
                }
            } catch (SeatAlreadyBookedException e) {
                System.err.println("Warnung beim Wiederherstellen der Plätze: " + e.getMessage());
            }
        }
    }
}