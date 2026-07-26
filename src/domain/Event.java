package domain;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
/**
 * Die Klasse Event repräsentiert eine Veranstaltung mit Basispreis, Termin und Bereichen.
 */
public class Event {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dateTime;
    private double basePrice;
    private List<Section> sections;
    private MapType mapType;

    public Event(Long id, String title, String description, LocalDateTime dateTime, double basePrice, MapType mapType) {
        this.id = id;
        this.title = title;
        this.description = description != null ? description : "";
        this.dateTime = dateTime;
        this.basePrice = basePrice;
        this.sections = new ArrayList<>();
        this.mapType = mapType;
    }

    public Event(Long id, String title, LocalDateTime dateTime, double basePrice, MapType mapType) {
        this(id, title, "", dateTime, basePrice, mapType);
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public Section findSectionByName(String name) {
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
            // Jede section muss eine Methode getAvailableSeats() haben, 
            // die die Anzahl der verfÃ¼gbaren PlÃ¤tze in dieser Section zurÃ¼ckgibt.
        }
        return totalSeats;
    }

    public enum MapType {
        STAGE_SEATED, ARENA, STAGE_STANDING
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
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    public double getBasePrice() {
        return basePrice;
    }
    public List<Section> getSections() {
        return sections;
    }
    public MapType getMapType() {
        return mapType;
    }
}



