package repository;
import java.util.ArrayList;
import java.util.List;
import domain.Event;

//Dient der Verwaltung der Events. Implementiert das Singleton-Muster, um sicherzustellen es zu keiner Dopplung kommt. Enthält Methoden zum Abrufen und Speichern von Events. 

public class EventRepository {
    private static EventRepository instance;

    private final List<Event> events;

    /* Konstruktor - Initialisiert die Liste der Events mit Demo-Daten. Wird privat gehalten, um Singleton-Muster zu implementieren. 
    Wird durch getInstance() im BookingService und App aufgerufen.
    */
    private EventRepository() {
        this.events = DemoData.createDemoEvents();
    }

    // Singleton-Instanz abrufen - Stellt sicher, dass nur eine Instanz von EventRepository existiert. Thread-sicher durch synchronized.
    public static synchronized EventRepository getInstance() {
        if(instance == null) {
            instance = new EventRepository();
        }
        return instance;
    }

    // Methode, um Liste aller geladenen Events abzurufen. Gibt eine Kopie der Liste zurück, um die Kapselung zu wahren.
    public List<Event> getAllEvents() {
        return new ArrayList<>(this.events);
    }
    
    // Methode, um ein Event anhand seiner ID im Repository zu finden. Gibt null zurück, wenn kein Event mit der angegebenen ID existiert. 
    public Event findById(Long id) {
        for(Event event : events) {
            if(event.getId().equals(id)) {
                return event;
            }
        }
        return null;
    }

    // Methode, um ein Event während der Laufzeit hinzuzufügen  
    // 
    //!!Gedanke für spätere Implementierung: Wenn wir Eventeingabe erstellen: Wie wird Dopplung der ID vermieden?
    //
    public void save(Event event) {
        if(event != null) {
            this.events.add(event);
        } 
    }
    
}
