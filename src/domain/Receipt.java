package domain;

import java.time.LocalDateTime;
import java.util.List;

public class Receipt {
    private Long receiptID;
    private final Customer customer;
    private List<Ticket> tickets; 
    private double totalPrice; 
    private LocalDateTime receiptTimestamp; 


    // Konstruktor 
    public Receipt (Long receiptID, Customer customer, List<Ticket> tickets, double totalPrice, LocalDateTime receiptTimestamp){
        this.receiptID = receiptID; 
        this.customer = customer;
        this.tickets = tickets;
        this.totalPrice = totalPrice;
        this.receiptTimestamp = receiptTimestamp; 
    }

    //Methode um Receiptinformationen als String ausgeben zu lassen 
    public String getReceiptText() {
        StringBuilder receiptText = new StringBuilder();
        receiptText.append(String.format(
                "Kunde: %s%nGesamtpreis: %.2f EUR%n%nGenerierte Ticket-IDs:%n",
                customer.getFullName(),
                totalPrice));

        for (Ticket ticket : tickets) {
            receiptText.append("- ").append(ticket.getTicketId()).append("\n");
        }
        
        return receiptText.toString();
    }

    
    //  Getter 
    public Long getReceiptID(){
        return receiptID; 
    }

    public Customer getReCustomer(){
        return customer;
    }

    public List<Ticket> getRTickets() {
        return this.tickets; 
    }

    public double getTotalPrice(){
        return this.totalPrice; 
    }

    public LocalDateTime getRTimestamp(){
        return receiptTimestamp;
    }
   
}
