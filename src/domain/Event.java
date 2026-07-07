package domain;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
public class Event {
    private Long id;
    private String title;
    private LocalDateTime dateTime;
    private double basePrice;
    private String description;
    private List<Section> sections;

    public Event(Long id, String title, String description, LocalDateTime dateTime, double basePrice) {
        this.id = id;
        this.title = title;
        this.description = description;
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

   // getter --> für Ticketerstellung
   // & setter --> Für Eventbearbeitung  

   // ID
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Title 
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    // Description 
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Spielzeit
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    
    // Grundpreis   //!!Gedanke: nachträgliche Preisänderungen anhand vom verändertem Faktor statt verändertem Grundpreis
    public double getBasePrice() {
        return basePrice;
    }

    // Blöcke //Gedanke: Bedarf es hier einer nachträglichen Änderung? 
    public List<Section> getSections() {
        return sections;
    }
}
