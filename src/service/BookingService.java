package service;

import domain.*;
import exceptions.*;
import repository.*;
import java.util.ArrayList;
import java.util.List;

public class BookingService {
    
    private final EventRepository eventRepo;
    private final List<Ticket> activeTickets;
    private long ticketIdCounter;

    public BookingService() {
        this.eventRepo = EventRepository.getInstance();
        this.activeTickets = new ArrayList<>();
        this.ticketIdCounter = 1000L; // Ticketnummern starten bei 1000.
    }

    public Ticket bookTicket (Long eventId, String sectionName, Customer customer) throws SeatAlreadyBookedException{
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

        double finalPrice = event.getBasePrice() * section.getPriceFactor();

        // 5. Ticket Objekt erstellen und im Service speichern
        Ticket newTicket = new Ticket(generatedTicektId, event, section, customer, finalPrice);
        activeTickets.add(newTicket);

        return newTicket;
    }

    // Liste aller aktiven Tickets im System
    public List<Ticket> getActiveTickets() {
        return new ArrayList<>(this.activeTickets);
    }
}
