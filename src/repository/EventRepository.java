package repository;
import java.util.ArrayList;
import java.util.List;
import domain.Event;

public class EventRepository {
    private static EventRepository instance;

    private final List<Event> events;

    private EventRepository() {
        this.events = DemoData.createDemoEvents();
    }

    public static synchronized EventRepository getInstance() {
        if(instance == null) {
            instance = new EventRepository();
        }
        return instance;
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

    // Event während der Laufzeit hinzufügen  //Gedanke für spätere Implementierung: Wenn wir Eventeingabe erstellen: Wie wird Dopplung der ID vermieden? 
    public void save(Event event) {
        if(event != null) {
            this.events.add(event);
        } 
    }
    
}
