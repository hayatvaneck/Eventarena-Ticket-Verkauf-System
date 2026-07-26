package domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Receipt {

    private final String receiptId;
    private final String userEmail;
    private final String customerName;
    private final LocalDateTime createdAt;
    private final double totalAmount;
    private final List<String> ticketIds;

    public Receipt(String receiptId, String userEmail, String customerName, LocalDateTime createdAt, double totalAmount, List<String> ticketIds) {
        this.receiptId = receiptId;
        this.userEmail = userEmail;
        this.customerName = customerName;
        this.createdAt = createdAt;
        this.totalAmount = totalAmount;
        this.ticketIds = new ArrayList<>(ticketIds);
    }

    public String getReceiptId() {
        return receiptId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getCustomerName() {
        return customerName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public List<String> getTicketIds() {
        return new ArrayList<>(ticketIds);
    }
}
