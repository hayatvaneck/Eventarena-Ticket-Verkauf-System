package repository;

import domain.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventRepository {
    private static EventRepository instance;

    private final List<Event> events;

    private EventRepository() {
        this.events = new ArrayList<>();
        seedMockData();
    }

    public static synchronized EventRepository getInstance() {
        if(instance == null) {
            instance = new EventRepository();
        }
        return instance;
    }

    // Erstellen der Events und die zugehörigen Sections
    private void seedMockData() {
        // Event 1: Konzert mit Stehplätze im Innenraum
        Event concert = new Event(1L, "Don Toliver Octane Tour Leg 2", LocalDateTime.of(2026, 11, 02, 19, 0), 100.0);
        concert.addSection(new StandingSection("Innenraum (Stehplatz)", 1.0, 2000));
        concert.addSection(new SeatedSection("Block A", 1.2, 20, 10));
        concert.addSection(new SeatedSection("Block B", 1.2, 20, 10));
        concert.addSection(new SeatedSection("VIP", 2.5, 2, 10));
        events.add(concert);
    
        // Event 2: Gala mit Innenraum Bestuhlung
        Event gala = new Event(2L, "Klassik Gala", LocalDateTime.of(2026, 12, 15, 20, 0), 150.0);
        gala.addSection(new SeatedSection("Parkett (bestuhlter Innenraum", 1.0, 15, 12));
        gala.addSection(new SeatedSection("Loge", 2.0, 5, 6));
        gala.addSection(new SeatedSection("Premium", 1.5, 10, 8));
        events.add(gala);

        // Event 3: Sportevent - Innenraum ist Spielfläche
        Event sport = new Event(3L, "Alba Berlin vs. FC Bayern München", LocalDateTime.of(2026, 8, 11, 18, 00), 80.0);
        sport.addSection(new EmptySection("Spielfläche"));
        sport.addSection(new SeatedSection("Fankurve Heim", 0.9, 20, 30));
        sport.addSection(new SeatedSection("Gästeblock", 0.9, 10, 20));
        sport.addSection(new SeatedSection("Haupttribüne", 1.2, 15, 30));
        events.add(sport);
    }

    // Liste aller geladenen Events
    public List<Event> getAllEvents() {
        return new ArrayList<>(this.events);
    }
    
    // Event nach ID suchen
    public Event findById(Long id) {
        for(Event event : events) {
            if(event.getId().equals(id)) {
                return event;
            }
        }
        return null;
    }

    // Event während der Laufzeit hinzufügen
    public void save(Event event) {
        if(event != null) {
            this.events.add(event);
        }
    }
    
}
