package domain;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
public class Event {
    private Long id;
    private String title;
    private LocalDateTime dateTime;
    private double basePrice;
    private List<Section> sections;

    public Event(Long id, String title, LocalDateTime dateTime, double basePrice) {
        this.id = id;
        this.title = title;
        this.dateTime = dateTime;
        this.basePrice = basePrice;
        this.sections = new ArrayList<>();
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
            // die die Anzahl der verfügbaren Plätze in dieser Section zurückgibt.
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
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    public double getBasePrice() {
        return basePrice;
    }
    public List<Section> getSections() {
        return sections;
    }
}
