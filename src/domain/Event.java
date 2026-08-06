package domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse Event repräsentiert eine Veranstaltung mit Basispreis, Termin und Bereichen.
 */
public class Event {

    /** Eindeutige Kennung der Veranstaltung. */
    private Long id;
    /** Anzeigename der Veranstaltung. */
    private String title;
    /** Ausführliche Beschreibung für die Eventauswahl. */
    private String description;
    /** Fachliche Kategorie der Veranstaltung. */
    private EventType eventType;
    /** Geplanter Beginn der Veranstaltung. */
    private LocalDateTime dateTime;
    /** Ausgangspreis, auf den die Bereichsfaktoren angewendet werden. */
    private double basePrice;
    /** Buchbare und nicht buchbare Bereiche des Saalplans. */
    private List<Section> sections;
    /** Darstellungs- und Nutzungsart des Saalplans. */
    private MapType mapType;

    /**
     * Erstellt eine Veranstaltung mit vollständigen Metadaten und leerem
     * Saalplan. Die Bereiche werden anschließend durch die Layout-Factory ergänzt.
     *
     * @param id eindeutige Event-ID
     * @param title Titel der Veranstaltung
     * @param description Beschreibung, bei {@code null} wird ein leerer Text verwendet
     * @param eventType fachliche Eventkategorie
     * @param dateTime Veranstaltungsbeginn
     * @param basePrice Basispreis vor Bereichsfaktor und Rabatt
     * @param mapType gewünschter Saalplantyp
     */
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

    /**
     * Komfortkonstruktor für Veranstaltungen ohne Beschreibung.
     *
     * @param id eindeutige Event-ID
     * @param title Titel der Veranstaltung
     * @param eventType fachliche Eventkategorie
     * @param dateTime Veranstaltungsbeginn
     * @param basePrice Basispreis vor Bereichsfaktor und Rabatt
     * @param mapType gewünschter Saalplantyp
     */
    public Event(Long id, String title, EventType eventType,
                 LocalDateTime dateTime, double basePrice, MapType mapType) {
        this(id, title, "", eventType, dateTime, basePrice, mapType);
    }

    /**
     * Ergänzt einen Bereich im Saalplan.
     *
     * @param section hinzuzufügender Bereich
     * @throws IllegalArgumentException wenn kein Bereich übergeben wurde
     */
    public void addSection(Section section) {
        if (section == null) {
            throw new IllegalArgumentException("Section darf nicht null sein.");
        }
        this.sections.add(section);
    }

    /**
     * Sucht einen Bereich ohne Beachtung der Groß- und Kleinschreibung.
     *
     * @param name gesuchter Bereichsname
     * @return gefundener Bereich oder {@code null}
     */
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

    /**
     * Addiert die noch verfügbaren Plätze aller Bereiche.
     *
     * @return Gesamtzahl der verfügbaren Tickets
     */
    public int getTotalAvailableSeats() {
        int totalSeats = 0;
        for (Section section : sections) {
            totalSeats += section.getAvailableSeats();
        }
        return totalSeats;
    }

    /** @return {@code true}, wenn kein Bereich mehr Kapazität besitzt */
    public boolean isSoldOut() {
        return getTotalAvailableSeats() == 0;
    }

    /** @return Event-ID */
    public Long getId() {
        return id;
    }

    /** @return Veranstaltungstitel */
    public String getTitle() {
        return title;
    }

    /** @return Eventbeschreibung */
    public String getDescription() {
        return description;
    }

    /** @return fachliche Eventkategorie */
    public EventType getEventType() {
        return eventType;
    }

    /** @return Veranstaltungsbeginn */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /** @return Basispreis vor Bereichsfaktor und Rabatt */
    public double getBasePrice() {
        return basePrice;
    }

    /**
     * Liefert eine Kopie der Bereichsliste, damit die interne Sammlung nicht
     * direkt von außen verändert werden kann.
     *
     * @return Kopie aller Eventbereiche
     */
    public List<Section> getSections() {
        return new ArrayList<>(sections);
    }

    /** @return verwendeter Saalplantyp */
    public MapType getMapType() {
        return mapType;
    }

    /** Legt fest, wie Bühne beziehungsweise Innenraum genutzt werden. */
    public enum MapType {
        /** Bühnenveranstaltung mit bestuhltem Innenraum. */
        STAGE_SEATED,
        /** Arenaveranstaltung mit nicht buchbarer Spielfläche. */
        ARENA,
        /** Bühnenveranstaltung mit Stehplatz-Innenraum. */
        STAGE_STANDING
    }

    /** Fachliche Kategorien zur Einordnung und Anzeige von Veranstaltungen. */
    public enum EventType {
        /** Musikalische Live-Veranstaltung. */
        KONZERT,
        /** Theateraufführung. */
        THEATER,
        /** Sportveranstaltung. */
        SPORTS,
        /** Comedy-Veranstaltung. */
        COMEDY,
        /** Festliche Gala. */
        GALA,
        /** Tanzveranstaltung. */
        TANZ,
        /** Veranstaltung eines Unternehmens. */
        FIRMENEVENT,
        /** Musicalaufführung. */
        MUSICAL,
        /** Auffangwert für nicht gesondert definierte Eventarten. */
        OTHER
    }
}
