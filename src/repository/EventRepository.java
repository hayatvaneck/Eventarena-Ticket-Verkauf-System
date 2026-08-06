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
    /** Einzige Repository-Instanz innerhalb der Anwendung. */
    private static EventRepository instance;
    /** Im Arbeitsspeicher verwaltete Veranstaltungen. */
    private final List<Event> events;
    /** Pfad zur CSV-Datei der Veranstaltungen. */
    private static final Path EVENTS_CSV = Paths.get("data", "events.csv");

    /** Initialisiert den Speicher und lädt den definierten Startbestand. */
    private EventRepository() {
        this.events = new ArrayList<>();
        loadEventsFromCsvOrSeed();
    }

    /**
     * Liefert die zentrale Repository-Instanz.
     *
     * @return Singleton-Instanz des Event-Repositories
     */
    public static synchronized EventRepository getInstance() {
        if(instance == null) {
            instance = new EventRepository();
        }
        return instance;
    }

    /**
     * Setzt den Startbestand auf die Demo-Events und synchronisiert die CSV-Datei.
     */
    private void loadEventsFromCsvOrSeed() {
        this.events.clear();
        // Demo-Daten sind die einzige Quelle beim Start: CSV wird damit synchronisiert.
        EventDemoData.seedInto(this.events);
        saveAllToCsv();
    }


    /**
     * Liest Events aus der vorhandenen CSV-Datei und rekonstruiert ihre Saalpläne.
     *
     * @return {@code true}, wenn mindestens ein Event erfolgreich geladen wurde
     */
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

    /** Schreibt alle Events sortiert und UTF-8-kodiert in die CSV-Datei. */
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

    /**
     * Zerlegt eine CSV-Zeile und berücksichtigt in Anführungszeichen stehende Kommata.
     *
     * @param line einzulesende CSV-Zeile
     * @return extrahierte Spalten
     */
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

    /**
     * Maskiert einen Text für die sichere Ausgabe in einer CSV-Spalte.
     *
     * @param value zu maskierender Wert
     * @return von Anführungszeichen umschlossener CSV-Wert
     */
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    /** @return defensive, nach internem Stand gefüllte Kopie aller Events */
    public List<Event> getAllEvents() {
        return new ArrayList<>(this.events);
    }

    /**
     * Sucht eine Veranstaltung über ihre ID.
     *
     * @param id gesuchte Event-ID
     * @return gefundenes Event oder {@code null}
     */
    public Event findById(Long id) {
        for (Event event : events) {
            if (event.getId().equals(id)) {
                return event;
            }
        }
        return null;
    }

    /**
     * Speichert ein neues Event mit frisch erzeugtem Standardlayout.
     *
     * @param event zu speicherndes Event; {@code null} wird ignoriert
     */
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

    /**
     * Ermittelt die nächste freie positive Event-ID.
     *
     * @return höchste vorhandene ID plus eins
     */
    public Long nextEventId() {
        long maxId = 0L;
        for (Event event : this.events) {
            if (event.getId() != null && event.getId() > maxId) {
                maxId = event.getId();
            }
        }
        return maxId + 1;
    }
   
    /**
     * Entfernt ein Event aus Speicher und CSV-Datei.
     *
     * @param eventId ID des zu löschenden Events
     * @return {@code true}, wenn ein Event entfernt wurde
     */
    public synchronized boolean deleteEvent(Long eventId) {
        if (eventId == null) return false;
        
        // Die ID identifiziert den zu entfernenden Eintrag eindeutig.
        boolean removed = this.events.removeIf(e -> e.getId().equals(eventId));
        
        // Nur eine tatsächliche Änderung der Liste wird persistiert.
        if (removed) {
            saveAllToCsv();
        }
        return removed;
    }
    /**
     * Ersetzt ein vorhandenes Event und baut dessen Layout vollständig neu auf.
     *
     * @param updatedEvent Event mit bestehender ID und aktualisierten Daten
     * @return {@code true}, wenn das Event gefunden und ersetzt wurde
     */
    public synchronized boolean updateEvent(Event updatedEvent) {
        if (updatedEvent == null || updatedEvent.getId() == null) {
            return false;
        }

        // Das bestehende Event wird anhand seiner ID gesucht.
        for (int i = 0; i < this.events.size(); i++) {
            if (this.events.get(i).getId().equals(updatedEvent.getId())) {
                
                // Ein Neuaufbau stellt ein zum geänderten Kartentyp passendes Hallenlayout sicher.
                Event normalizedEvent = new Event(
                        updatedEvent.getId(),
                        updatedEvent.getTitle(),
                        updatedEvent.getDescription(),
                        updatedEvent.getEventType(),
                        updatedEvent.getDateTime(),
                        updatedEvent.getBasePrice(),
                        updatedEvent.getMapType()
                );
                domain.layout.HallLayoutFactory.applyLayoutForMapType(normalizedEvent);
                
                // Der rekonstruierte Datensatz ersetzt das bisherige Event.
                this.events.set(i, normalizedEvent);
                saveAllToCsv();
                return true;
            }
        }
        return false;
    }
}






