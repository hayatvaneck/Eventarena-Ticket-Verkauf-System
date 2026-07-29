package repository;

import domain.*;
import domain.Event.MapType;
import domain.Event.EventType;
import domain.layout.HallLayoutFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Die Klasse EventRepository lädt, speichert und verwaltet alle verfügbaren Events inklusive Layout-Definition.

 */

public class EventRepository {
    private static EventRepository instance;
    private final List<Event> events;
    private static final Path EVENTS_CSV = Paths.get("data", "events.csv");

    private EventRepository() {
        this.events = new ArrayList<>();
        loadEventsFromCsvOrSeed();
    }

    public static synchronized EventRepository getInstance() {
        if(instance == null) {
            instance = new EventRepository();
        }
        return instance;
    }

    private void loadEventsFromCsvOrSeed() {
        this.events.clear();
        // Demo-Daten sind die einzige Quelle beim Start: CSV wird damit synchronisiert.
        EventDemoData.seedInto(this.events);
        saveAllToCsv();
    }

    private boolean loadFromCsv() {
        if (!Files.exists(EVENTS_CSV)) {
            return false;
        }

        try {
            List<String> eventLines = Files.readAllLines(EVENTS_CSV, StandardCharsets.UTF_8);

            if (eventLines.size() <= 1) {
                return false;
            }

            for (int i = 1; i < eventLines.size(); i++) {
                String line = eventLines.get(i).trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] columns = parseCsvLine(line);
                if (columns.length < 5) {
                    continue;
                }

                Long eventId = Long.parseLong(columns[0].trim());
                String title = columns[1].trim();
                String description;
                LocalDateTime dateTime;
                double basePrice;
                MapType mapType;
                if (columns.length >= 6) {
                    description = columns[2].trim();
                    dateTime = LocalDateTime.parse(columns[3].trim());
                    basePrice = Double.parseDouble(columns[4].trim());
                    mapType = MapType.valueOf(columns[5].trim());
                } else {
                    description = "";
                    dateTime = LocalDateTime.parse(columns[2].trim());
                    basePrice = Double.parseDouble(columns[3].trim());
                    mapType = MapType.valueOf(columns[4].trim());
                }

                EventType eventType;
                if (columns.length >= 7) {
                    eventType = EventType.valueOf(columns[6].trim());
                } else {
                    eventType = EventType.OTHER;
                }
                Event event = new Event(eventId, title, description, eventType, dateTime, basePrice, mapType);
                HallLayoutFactory.applyLayoutForMapType(event);
                this.events.add(event);
            }

            this.events.sort(Comparator.comparing(Event::getId));
            return !this.events.isEmpty();
        } catch (Exception ignored) {
            this.events.clear();
            return false;
        }
    }

    private void saveAllToCsv() {
        List<String> eventLines = new ArrayList<>();
        eventLines.add("event_id,title,description,event_type,date_time,base_price,map_type");

        List<Event> sortedEvents = new ArrayList<>(this.events);
        sortedEvents.sort(Comparator.comparing(Event::getId));

        for (Event event : sortedEvents) {
            eventLines.add(
                event.getId() + "," +
                escapeCsv(event.getTitle()) + "," +
                escapeCsv(event.getDescription()) + "," +
                event.getEventType().name() + "," +
                event.getDateTime() + "," +
                event.getBasePrice() + "," +
                event.getMapType().name()
            );
        }

        try {
            Files.write(EVENTS_CSV, eventLines, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new RuntimeException("Events konnten nicht in CSV gespeichert werden.", ex);
        }
    }

    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        result.add(current.toString());
        return result.toArray(new String[0]);
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    // Liste aller geladenen Events
    public List<Event> getAllEvents() {
        return new ArrayList<>(this.events);
    }

    // Event nach ID suchen
    public Event findById(Long id) {
        for (Event event : events) {
            if (event.getId().equals(id)) {
                return event;
            }
        }
        return null;
    }

    // Event waehrend der Laufzeit hinzufuegen
    public void save(Event event) {
        if (event != null) {
            Event normalizedEvent = new Event(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getEventType(),
                event.getDateTime(),
                event.getBasePrice(),
                event.getMapType()
            );
            HallLayoutFactory.applyLayoutForMapType(normalizedEvent);

            this.events.add(normalizedEvent);
            saveAllToCsv();
        }
    }

    public Long nextEventId() {
        long maxId = 0L;
        for (Event event : this.events) {
            if (event.getId() != null && event.getId() > maxId) {
                maxId = event.getId();
            }
        }
        return maxId + 1;
    }
   
    public synchronized boolean deleteEvent(Long eventId) {
        if (eventId == null) return false;
        
        // Entfernt das Event aus der Liste, wenn die ID übereinstimmt
        boolean removed = this.events.removeIf(e -> e.getId().equals(eventId));
        
        // Wenn es erfolgreich entfernt wurde, überschreiben wir die CSV
        if (removed) {
            saveAllToCsv();
        }
        return removed;
    }
}






