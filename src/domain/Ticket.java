package domain;

public class Ticket {
    private final String ticketId;
    private final Event event;
    private final Section section;
    private final Customer customer;
    private final double finalPrice;

    public Ticket(String ticketId, Event event, Section section, Customer customer, double finalPrice) {
        this.ticketId = ticketId;
        this.event = event;
        this.section = section;
        this.customer = customer;
        this.finalPrice = finalPrice;
    }

    // Anzeige des Tickets in der Konsole
    public void printTicketDetails() {
        System.out.println("\n=======================================================");
        System.out.println("                    TICKET BESTÄTIGUNG                   ");
        System.out.println("=======================================================");
        System.out.printf(" Ticket-ID:     %s%n", ticketId);
        System.out.printf(" Event:         %s%n", event.getTitle());
        System.out.printf(" Datum/Zeit:    %s%n", event.getDateTime());
        System.out.printf(" Bereich:       %s%n", section.getName());
        System.out.printf(" Kunde:         %s (%s)%n", customer.getFullName(), customer.getCustomerType());
        System.out.println("-------------------------------------------------------");
        System.out.printf(" Endpreis:      %.2f EUR%n", finalPrice);
        System.out.println("=======================================================\n");
    }

    // Getter
    public String getTicketId() {
        return ticketId;
    }
    public Event getEvent() {
        return event;
    }
    public Section getSection() {
        return section;
    }
    public Customer getCustomer() {
        return customer;
    }
    public double getFinalPrice() {
        return finalPrice;
    }
}
