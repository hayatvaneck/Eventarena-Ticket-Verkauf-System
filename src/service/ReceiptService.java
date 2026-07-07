package service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import domain.Customer;
import domain.Receipt;
import domain.Ticket;


public class ReceiptService {
    private final List<Receipt> receiptsList;  // bisher ungenutzt -- wird für Mitarbeiteraccount relevant 
    private long receiptCounter = 1000;

    //Konstruktor - Erstellt eine Liste für die Receipts und setzt den Counter auf 1000 -- Wir einmalig initialisiert
     public ReceiptService() {
        this.receiptsList = new ArrayList<>();
        this.receiptCounter = 1000L; // Receiptnummern starten bei 1000 (für die Optik) 
    }


    // Methode zur Erstellung der Quittung mit Gesamtpreis, Kundeninformationen, Angaben zu gekaufen Tickets und Zeitstempel 
    public Receipt createReceipt(Customer customer, List<Ticket> tickets){

        double totalPrice = calculateCosts(tickets); 
        long receiptId = receiptCounter++;

        Receipt receipt = new Receipt(receiptId, customer, tickets, totalPrice, LocalDateTime.now());
        receiptsList.add(receipt);

        return receipt;
    }


    /* oben genutzte Methoden */

    // Methode zur Berechnung des Gesamtpreises 
    private double calculateCosts(List<Ticket> tickets) {

        double totalPrice = 0;
        for (Ticket ticket : tickets) {
            totalPrice += ticket.getFinalPrice();
        }
        return totalPrice;

    }


}
