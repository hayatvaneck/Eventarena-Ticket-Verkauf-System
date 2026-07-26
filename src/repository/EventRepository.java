package repository;

import domain.*;
import domain.Event.EventType;
import domain.layout.HallLayoutFactory;
import domain.layout.HallLayoutFactory.InteriorMode;

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
 * Die Klasse EventRepository laedt, speichert und verwaltet alle verfuegbaren Events inklusive Innenraum-Modus.

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

        if (loadFromCsv()) {
            return;
        }

        seedMockData();
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
                EventType eventType;
                InteriorMode interiorMode;

                if (columns.length >= 7) {
                    description = columns[2].trim();
                    dateTime = LocalDateTime.parse(columns[3].trim());
                    basePrice = Double.parseDouble(columns[4].trim());
                    eventType = EventType.valueOf(columns[5].trim());
                    interiorMode = parseInteriorMode(columns[6]);
                } else {
                    description = "";
                    dateTime = LocalDateTime.parse(columns[2].trim());
                    basePrice = Double.parseDouble(columns[3].trim());
                    eventType = EventType.valueOf(columns[4].trim());
                    interiorMode = columns.length >= 6
                        ? parseInteriorMode(columns[5])
                        : inferInteriorModeFromEventType(eventType);
                }

                Event event = new Event(eventId, title, description, dateTime, basePrice, eventType);
                HallLayoutFactory.applyStandardLayout(event, interiorMode);
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
        eventLines.add("event_id,title,description,date_time,base_price,event_type,interior_mode");

        List<Event> sortedEvents = new ArrayList<>(this.events);
        sortedEvents.sort(Comparator.comparing(Event::getId));

        for (Event event : sortedEvents) {
            InteriorMode interiorMode = HallLayoutFactory.inferInteriorMode(event);
            eventLines.add(
                event.getId() + "," +
                escapeCsv(event.getTitle()) + "," +
                escapeCsv(event.getDescription()) + "," +
                event.getDateTime() + "," +
                event.getBasePrice() + "," +
                event.getEventType().name() + "," +
                interiorMode.name()
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

    private InteriorMode parseInteriorMode(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return InteriorMode.STANDING;
            }
            return InteriorMode.valueOf(value.trim().toUpperCase());
        } catch (Exception ex) {
            return InteriorMode.STANDING;
        }
    }

    private InteriorMode inferInteriorModeFromEventType(EventType eventType) {
        if (eventType == EventType.BASKETBALL) {
            return InteriorMode.EMPTY;
        }
        if (eventType == EventType.GALA) {
            return InteriorMode.SEATED;
        }
        return InteriorMode.STANDING;
    }

    // Erstellen der Events und die zugehÃ¶rigen Sections
    private void seedMockData() {
        Event concert = new Event(
            1L,
            "Don Toliver Octane Tour Leg 2",
            "Hip-Hop-Liveshow mit Stehplatz-Innenraum und energiegeladener Konzertatmosphaere.",
            LocalDateTime.of(2026, 11, 2, 19, 0),
            100.0,
            EventType.CONCERT
        );
        HallLayoutFactory.applyStandardLayout(concert, InteriorMode.STANDING);
        events.add(concert);
    
        Event gala = new Event(
            2L,
            "Klassik Gala",
            "Festlicher Konzertabend mit klassischem Programm im bestuhlten Innenraum.",
            LocalDateTime.of(2026, 12, 15, 20, 0),
            150.0,
            EventType.GALA
        );
        HallLayoutFactory.applyStandardLayout(gala, InteriorMode.SEATED);
        events.add(gala);

        Event sport = new Event(
            3L,
            "Alba Berlin vs. FC Bayern MÃ¼nchen",
            "Topspiel der Basketball-Bundesliga mit freier Sicht auf das Spielfeld.",
            LocalDateTime.of(2026, 8, 11, 18, 0),
            80.0,
            EventType.BASKETBALL
        );
        HallLayoutFactory.applyStandardLayout(sport, InteriorMode.EMPTY);
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

    // Event wÃ¤hrend der Laufzeit hinzufÃ¼gen
    public void save(Event event) {
        if(event != null) {
            InteriorMode interiorMode = event.getSections() == null || event.getSections().isEmpty()
                ? inferInteriorModeFromEventType(event.getEventType())
                : HallLayoutFactory.inferInteriorMode(event);

            Event normalizedEvent = new Event(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDateTime(),
                event.getBasePrice(),
                event.getEventType()
            );
            HallLayoutFactory.applyStandardLayout(normalizedEvent, interiorMode);

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
    
}



