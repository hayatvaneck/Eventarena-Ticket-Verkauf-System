package service;

import domain.*;
import exceptions.*;
import repository.*;
import java.util.ArrayList;
import java.util.List;

/*Dient der Verwaltung der Buchungen und der aktiven Tickets. Enthält Methoden zum Buchen von Tickets und zur Verwaltung der Ticket-ID-Generierung.
  Wird einmalig in der App-Klasse instanziert und übergeben, um die Buchungslogik zu kapseln. */
public class BookingService {
    private final EventRepository eventRepo;
    private final List<Ticket> activeTickets;
    private long ticketIdCounter;

    // Konstruktor - 
    public BookingService() {
        this.eventRepo = EventRepository.getInstance();
        this.activeTickets = new ArrayList<>();
        this.ticketIdCounter = 1000L; // Ticketnummern starten bei 1000.
    }

    // Methode um Buchung eines Tickets auszulösen. Wirft eine Exception, wenn der Platz bereits gebucht ist oder der Bereich ausgebucht ist.
    public Ticket bookTicket (Long eventId, String sectionName, Customer customer) throws SeatAlreadyBookedException{
        // 1. Event & Section suchen
        Event event = getEventOrThrow(eventId);
        Section section = getSectionOrThrow(event, sectionName);

        // 3. Platz reservieren
        boolean reservationSuccessful = section.bookNextAvailableTicket();
        if(!reservationSuccessful) {
            throw new SeatAlreadyBookedException("Der Bereich '" + sectionName + "' ist ausgebucht oder gesperrt.");
        }

        // 4. Ticket generieren
        return createTicket(event, section, customer);
    }

    // Methode um Buchung eines spezifischen Sitzplatzes auszulösen. Wirft eine Exception, wenn der Platz bereits gebucht ist oder der Bereich kein Sitzplatz-Block ist.
    public Ticket bookSpecificTicket(Long eventId, String sectionName, int row, int seatNumber, Customer customer) throws SeatAlreadyBookedException {
        // 1. Event und Section suchen
        Event event = getEventOrThrow(eventId);
        Section section = getSectionOrThrow(event, sectionName);

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
        return createTicket(event, section, customer);
    }


    /* Oben verwendete Methoden */

    // Liste aller aktiven Tickets im System
    public List<Ticket> getActiveTickets() {
        return new ArrayList<>(this.activeTickets);
    }

    // Ruft das Event ab bzw. wirft eine Exception, wenn es nicht existiert
    private Event getEventOrThrow(Long eventId) {
        Event event = eventRepo.findById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event mit ID " + eventId + " wurde nicht gefunden.");
        }
        return event;
    }

    // Ruft die Section ab bzw. wirft eine Exception, wenn sie nicht existiert
    private Section getSectionOrThrow(Event event, String sectionName) {
        Section section = event.findSectionByName(sectionName);
        if (section == null) {
            throw new IllegalArgumentException("Der Block '" + sectionName + "' existiert für dieses Event nicht.");
        }
        return section;
    }

    // Ticket-ID generieren und Preis berechnen --> gibt neues Ticket zurück
    private Ticket createTicket(Event event, Section section, Customer customer) {
        ticketIdCounter++;
        String generatedTicektId = "T-" + ticketIdCounter;

        double finalPrice = event.getBasePrice() * section.getPriceFactor();

        Ticket newTicket = new Ticket(generatedTicektId, event, section, customer, finalPrice);
        activeTickets.add(newTicket);
        return newTicket;
    }

}
