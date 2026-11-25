package domain;

/**
 * Die Klasse Ticket repräsentiert ein gebuchtes Ticket mit Event-, Platz- und Preisinformationen.

 */

public class Ticket {
    /** Eindeutige Ticketnummer. */
    private final String ticketId;
    /** Veranstaltung, für die das Ticket gilt. */
    private final Event event;
    /** Gebuchter Veranstaltungsbereich. */
    private final Section section;
    /** Person beziehungsweise Kundengruppe, für die das Ticket ausgestellt ist. */
    private final Customer customer;
    /** Tatsächlich berechneter Preis nach Bereichsfaktor und Rabatt. */
    private final double finalPrice;
    /** Lesbare Platzangabe für Sitz- oder Stehplätze. */
    private final String seatInfo;
    /** E-Mail des kaufenden Benutzerkontos. */
    private String userEmail;
    /** Gespeicherte Bezeichnung der Kundengruppe. */
    private String customerType;
    /** Ausgangspreis des Tickets vor Kundenrabatt. */
    private double price;

    /**
     * Erstellt ein Ticket mit sämtlichen bei der Buchung ermittelten Werten.
     *
     * @param ticketId eindeutige Ticketnummer
     * @param event gebuchte Veranstaltung
     * @param section gebuchter Bereich
     * @param customer Ticketinhaber
     * @param finalPrice Endpreis nach Rabatt
     * @param seatInfo lesbare Platzbeschreibung
     * @param userEmail E-Mail des Käuferkontos
     * @param customerType gespeicherte Kundengruppe
     * @param price Ausgangspreis vor Kundenrabatt
     */
    public Ticket(String ticketId, Event event, Section section, Customer customer, double finalPrice, String seatInfo, String userEmail, String customerType, double price) {
        this.ticketId = ticketId;
        this.event = event;
        this.section = section;
        this.customer = customer;
        this.finalPrice = finalPrice;
        this.seatInfo = seatInfo;
        this.userEmail = userEmail;
        this.customerType = customerType;
        this.price = price;
    }

    /**
     * Komfortkonstruktor für aus der Persistenz rekonstruierte Tickets. Nicht
     * übergebene Preis- und Typinformationen werden aus den Domain-Objekten abgeleitet.
     *
     * @param ticketId eindeutige Ticketnummer
     * @param event gebuchte Veranstaltung
     * @param section gebuchter Bereich
     * @param customer Ticketinhaber
     * @param finalPrice gespeicherter Endpreis
     * @param seatInfo lesbare Platzbeschreibung
     * @param userEmail E-Mail des Käuferkontos
     */
    public Ticket(String ticketId, Event event, Section section, Customer customer, double finalPrice, String seatInfo, String userEmail) {
        this(
            ticketId, 
            event, 
            section, 
            customer, 
            finalPrice,
            seatInfo, 
            userEmail,
            (customer != null && customer.getCustomerType() != null ? customer.getCustomerType().name() : "STANDARD"),
            (event != null && section != null ? event. getBasePrice() * section.getPriceFactor() : finalPrice)
        );
    }

    /** Gibt eine formatierte Zusammenfassung des Tickets auf der Konsole aus. */
    public void printTicketDetails() {
        System.out.println("\n=======================================================");
        System.out.println("                    TICKET BESTÄTIGUNG                   ");
        System.out.println("=======================================================");
        System.out.printf(" Ticket-ID:     %s%n", ticketId);
        System.out.printf(" Event:         %s%n", event.getTitle());
        System.out.printf(" Datum/Zeit:    %s%n", event.getDateTime());
        System.out.printf(" Bereich:       %s%n", section.getName());
        System.out.printf(" Platz:         %s%n", seatInfo);
        System.out.printf(" Kunde:         %s (%s)%n", customer.getFullName(), customer.getCustomerType());
        System.out.println("-------------------------------------------------------");
        System.out.printf(" Endpreis:      %.2f EUR%n", finalPrice);
        System.out.println("=======================================================\n");
    }

    /** @return eindeutige Ticketnummer */
    public String getTicketId() {
        return ticketId;
    }
    /** @return gebuchte Veranstaltung */
    public Event getEvent() {
        return event;
    }
    /** @return gebuchter Veranstaltungsbereich */
    public Section getSection() {
        return section;
    }
    /** @return zugeordneter Ticketinhaber */
    public Customer getCustomer() {
        return customer;
    }
    /** @return berechneter Endpreis */
    public double getFinalPrice() {
        return finalPrice;
    }
    /** @return lesbare Platzbeschreibung */
    public String getSeatInfo() {
        return seatInfo;
    }
    /** @return E-Mail des kaufenden Benutzerkontos */
    public String getUserEmail() {
        return userEmail;
    }
    /** @return gespeicherte Kundengruppe */
    public String getCustomerType() {
        return customerType;
    }
    /** @return Ausgangspreis vor Kundenrabatt */
    public double getPrice() {
        return price;
    }
    /**
     * Aktualisiert die gespeicherte Kundengruppe, etwa beim Rekonstruieren alter Daten.
     *
     * @param customerType neue Kundengruppenbezeichnung
     */
    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }
    /**
     * Aktualisiert den Ausgangspreis des Tickets.
     *
     * @param price neuer Ausgangspreis
     */
    public void setPrice(double price) {
        this.price = price;
    }


}



