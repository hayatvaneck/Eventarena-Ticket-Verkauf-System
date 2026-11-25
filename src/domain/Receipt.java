package domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse Receipt repräsentiert eine Buchungsquittung mit Betrag, Zeitstempel und Ticketreferenzen.

 */

public class Receipt {

    /** Eindeutige Nummer der Quittung. */
    private final String receiptId;
    /** E-Mail des Benutzerkontos, dem die Buchung zugeordnet ist. */
    private final String userEmail;
    /** Zum Buchungszeitpunkt gespeicherter Name des Käufers. */
    private final String customerName;
    /** Zeitpunkt, zu dem die Buchung abgeschlossen wurde. */
    private final LocalDateTime createdAt;
    /** Gesamtbetrag aller Tickets dieser Buchung. */
    private final double totalAmount;
    /** IDs der Tickets, die gemeinsam abgerechnet wurden. */
    private final List<String> ticketIds;

    /**
     * Erstellt einen unveränderlichen Buchungsbeleg. Die Ticketliste wird
     * defensiv kopiert, damit der Beleg nachträglich nicht verändert wird.
     *
     * @param receiptId eindeutige Quittungsnummer
     * @param userEmail E-Mail des Käufers
     * @param customerName vollständiger Käufername
     * @param createdAt Erstellungszeitpunkt
     * @param totalAmount Gesamtbetrag der Buchung
     * @param ticketIds IDs der enthaltenen Tickets
     */
    public Receipt(String receiptId, String userEmail, String customerName, LocalDateTime createdAt, double totalAmount, List<String> ticketIds) {
        this.receiptId = receiptId;
        this.userEmail = userEmail;
        this.customerName = customerName;
        this.createdAt = createdAt;
        this.totalAmount = totalAmount;
        this.ticketIds = new ArrayList<>(ticketIds);
    }

    /** @return Quittungsnummer */
    public String getReceiptId() {
        return receiptId;
    }

    /** @return E-Mail des zugeordneten Benutzerkontos */
    public String getUserEmail() {
        return userEmail;
    }

    /** @return zum Buchungszeitpunkt gespeicherter Käufername */
    public String getCustomerName() {
        return customerName;
    }

    /** @return Erstellungszeitpunkt der Quittung */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** @return Gesamtbetrag der Buchung */
    public double getTotalAmount() {
        return totalAmount;
    }

    /** @return defensive Kopie der zugehörigen Ticket-IDs */
    public List<String> getTicketIds() {
        return new ArrayList<>(ticketIds);
    }
}



