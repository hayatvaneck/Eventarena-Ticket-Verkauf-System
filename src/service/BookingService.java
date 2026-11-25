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

    /** Repository für Veranstaltung und Saalplan. */
    private final EventRepository eventRepo;
    /** Repository für das dauerhafte Speichern und Löschen von Tickets. */
    private final TicketRepository ticketRepo;
    /** Während der Laufzeit bekannte, nicht stornierte Tickets. */
    private final List<Ticket> activeTickets;
    /** Laufender Zähler zur Erzeugung lesbarer Ticketnummern. */
    private long ticketIdCounter;

    /**
     * Verbindet den Service mit den zentralen Repositories und rekonstruiert die
     * Belegungszustände aus bereits gespeicherten Tickets.
     */
    public BookingService() {
        this.eventRepo = EventRepository.getInstance();
        this.ticketRepo = TicketRepository.getInstance();
        this.activeTickets = new ArrayList<>();
        this.ticketIdCounter = 1000L + ticketRepo.findAll().size();

        restoreBookedSeatsFromRepository();
    }

    /**
     * Bucht die nächste freie Kapazität eines Bereichs, insbesondere einen Stehplatz.
     *
     * @param eventId ID der gewünschten Veranstaltung
     * @param sectionName Name des gewünschten Bereichs
     * @param customer Ticketinhaber und Kundengruppe
     * @param userEmail E-Mail des kaufenden Benutzerkontos
     * @return erzeugtes und gespeichertes Ticket
     * @throws SeatAlreadyBookedException wenn der Bereich voll oder gesperrt ist
     * @throws IllegalArgumentException wenn Event oder Bereich nicht existieren
     */
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

    /**
     * Bucht einen konkreten Sitzplatz und erzeugt das zugehörige Ticket.
     *
     * @param eventId ID der Veranstaltung
     * @param sectionName Name des Sitzbereichs
     * @param row einsbasierte Reihennummer
     * @param seatNumber einsbasierte Platznummer
     * @param customer Ticketinhaber und Kundengruppe
     * @param userEmail E-Mail des kaufenden Benutzerkontos
     * @return erzeugtes und gespeichertes Ticket
     * @throws SeatAlreadyBookedException wenn der Sitz bereits belegt ist
     * @throws IllegalArgumentException wenn Event, Bereich oder Sitz ungültig sind
     */
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

    /**
     * Liefert den Multiplikator für den kundengruppenabhängigen Endpreis.
     *
     * @param customerType Kundengruppe
     * @return Rabattfaktor zwischen {@code 0.5} und {@code 1.0}
     */
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

    /** @return defensive Kopie aller aktiven Tickets */
    public List<Ticket> getActiveTickets() {
        return new ArrayList<>(this.activeTickets);
    }

    /**
     * Storniert ein Ticket, gibt seine Kapazität frei und entfernt die Persistenz.
     *
     * @param ticket zu stornierendes Ticket
     * @param user Benutzerkonto, aus dem das Ticket entfernt wird
     * @return {@code true}, wenn Platzfreigabe und Löschung erfolgreich waren
     */
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

    /**
     * Extrahiert Reihe und Sitznummer aus der gespeicherten Platzbeschreibung.
     *
     * @param seatInfo Text wie {@code Reihe 2, Platz 7}
     * @return Array mit Reihe und Platz oder zwei Nullen bei ungültigem Text
     */
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

    /**
     * Überträgt persistierte Tickets in die aktive Liste und markiert die
     * zugehörigen Sitz- beziehungsweise Stehplatzkapazitäten als belegt.
     */
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
