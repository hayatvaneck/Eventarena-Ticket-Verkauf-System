package domain;

public class Ticket {
    private final String ticketId;
    private final Event event;
    private final Section section;
    private final Customer customer;
    private final double finalPrice;
    private final String seatInfo;
    private String userEmail;
    private CustomerType customerType;
    private double price;

    public Ticket(String ticketId, Event event, Section section, Customer customer, double finalPrice, String seatInfo, String userEmail, CustomerType customerType, double price) {
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

    public Ticket(String ticketId, Event event, Section section, Customer customer, double finalPrice, String seatInfo, String userEmail) {
        this(
            ticketId, 
            event, 
            section, 
            customer, 
            finalPrice,
            seatInfo, 
            userEmail,
            (customer != null && customer.getCustomerType() != null
            ? customer.getCustomerType()
            : CustomerType.STANDARD),               
            (event != null && section != null ? event.getBasePrice() * section.getPriceFactor() : finalPrice)
        );
    }

    /* 
    private static double calculateDiscountPrice(double basePrice, String customerType) {
        if (customerType == null) {
            return basePrice;
        }
        switch (customerType) {
            case "Student":
                return basePrice * 0.80;
                case "Rentner":
                    return basePrice * 0.7;
                    case "Kind":
                        return basePrice * 0.5;
                        default:
                            return basePrice;
                        }
                    }
                    */

    // Anzeige des Tickets in der Konsole
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
    public String getSeatInfo() {
        return seatInfo;
    }
    public String getUserEmail() {
        return userEmail;
    }
    public CustomerType getCustomerType() {
        return customerType;
    }
    public double getPrice() {
        return price;
    }
    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }
    public void setPrice(double price) {
        this.price = price;
    }


}
