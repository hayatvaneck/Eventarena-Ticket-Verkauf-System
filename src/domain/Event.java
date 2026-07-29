package domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse Event repräsentiert eine Veranstaltung mit Basispreis, Termin und Bereichen.
 */
public class Event {

    private Long id;
    private String title;
    private String description;
    private EventType eventType;
    private LocalDateTime dateTime;
    private double basePrice;
    private List<Section> sections;
    private MapType mapType;

    public Event(Long id, String title, String description, EventType eventType,
                 LocalDateTime dateTime, double basePrice, MapType mapType) {
        this.id = id;
        this.title = title;
        this.description = description != null ? description : "";
        this.eventType = eventType;
        this.dateTime = dateTime;
        this.basePrice = basePrice;
        this.sections = new ArrayList<>();
        this.mapType = mapType;
    }

    public Event(Long id, String title, EventType eventType,
                 LocalDateTime dateTime, double basePrice, MapType mapType) {
        this(id, title, "", eventType, dateTime, basePrice, mapType);
    }

    public void addSection(Section section) {
        if (section == null) {
            throw new IllegalArgumentException("Section darf nicht null sein.");
        }
        this.sections.add(section);
    }

    public Section findSectionByName(String name) {
        if (name == null) {
            return null;
        }

        for (Section section : sections) {
            if (section.getName().equalsIgnoreCase(name)) {
                return section;
            }
        }
        return null;
    }

    public int getTotalAvailableSeats() {
        int totalSeats = 0;
        for (Section section : sections) {
            totalSeats += section.getAvailableSeats();
        }
        return totalSeats;
    }

    public boolean isSoldOut() {
        return getTotalAvailableSeats() == 0;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public EventType getEventType() {
        return eventType;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public List<Section> getSections() {
        return new ArrayList<>(sections);
    }

    public MapType getMapType() {
        return mapType;
    }

    public enum MapType {
        STAGE_SEATED,
        ARENA,
        STAGE_STANDING
    }

    public enum EventType {
        KONZERT,
        THEATER,
        SPORTS,
        COMEDY,
        GALA,
        TANZ,
        FIRMENEVENT,
        MUSICAL,
        OTHER
    }
}