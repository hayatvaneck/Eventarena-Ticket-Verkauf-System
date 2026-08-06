package repository;

import domain.Receipt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Die Klasse ReceiptRepository verwaltet das Speichern und Laden von Quittungen.

 */

public class ReceiptRepository {

    /** Einzige Repository-Instanz innerhalb der Anwendung. */
    private static ReceiptRepository instance;
    /** Im Arbeitsspeicher verfügbare Quittungen. */
    private final List<Receipt> receipts;
    /** Laufender Zähler für neue Quittungsnummern. */
    private long receiptCounter;

    /** Relativer Pfad zur Quittungsdatei. */
    private static final String CSV_FILE_PATH = "data/receipts.csv";
    /** Spaltentrennzeichen des Quittungsformats. */
    private static final String CSV_SEPARATOR = ";";

    /** Lädt vorhandene Quittungen und initialisiert den Nummernzähler. */
    private ReceiptRepository() {
        this.receipts = new ArrayList<>();
        loadReceiptsFromCSV();
        this.receiptCounter = 1000L + receipts.size();
    }

    /** @return zentrale Singleton-Instanz des Quittungs-Repositories */
    public static synchronized ReceiptRepository getInstance() {
        if (instance == null) {
            instance = new ReceiptRepository();
        }
        return instance;
    }

    /**
     * Reserviert eine neue fortlaufende Quittungsnummer.
     *
     * @return neue ID im Format {@code R-<Nummer>}
     */
    public synchronized String nextReceiptId() {
        receiptCounter++;
        return "R-" + receiptCounter;
    }

    /**
     * Fügt eine Quittung dem Speicher hinzu und hängt sie an die CSV-Datei an.
     *
     * @param receipt zu speichernde Quittung
     */
    public synchronized void save(Receipt receipt) {
        receipts.add(receipt);
        appendReceiptToCSV(receipt);
    }

    /**
     * Sucht alle Quittungen eines Benutzerkontos.
     *
     * @param userEmail E-Mail des Benutzerkontos
     * @return neu erzeugte Liste passender Quittungen
     */
    public synchronized List<Receipt> findByUserEmail(String userEmail) {
        List<Receipt> result = new ArrayList<>();
        if (userEmail == null) {
            return result;
        }
        for (Receipt receipt : receipts) {
            if (userEmail.equalsIgnoreCase(receipt.getUserEmail())) {
                result.add(receipt);
            }
        }
        return result;
    }

    /**
     * Serialisiert eine einzelne Quittung am Ende der CSV-Datei.
     *
     * @param receipt zu serialisierende Quittung
     */
    private void appendReceiptToCSV(Receipt receipt) {
        File file = new File(CSV_FILE_PATH);
        boolean isNewFile = !file.exists() || file.length() == 0;

        try (FileOutputStream fos = new FileOutputStream(file, true);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(osw)) {

            if (isNewFile) {
                writer.write("receiptId;userEmail;customerName;createdAt;totalAmount;ticketIds");
                writer.newLine();
            }

            String ticketIdsJoined = String.join("|", receipt.getTicketIds());
            String line = String.join(CSV_SEPARATOR,
                receipt.getReceiptId(),
                receipt.getUserEmail(),
                receipt.getCustomerName(),
                receipt.getCreatedAt().toString(),
                String.valueOf(receipt.getTotalAmount()),
                ticketIdsJoined
            );

            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Fehler beim Schreiben von receipts.csv: " + e.getMessage());
        }
    }

    /** Liest beim Start alle gültigen Quittungen aus der CSV-Datei ein. */
    private void loadReceiptsFromCSV() {
        File file = new File(CSV_FILE_PATH);
        if (!file.exists()) {
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {

            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(CSV_SEPARATOR, -1);
                if (data.length < 6) {
                    continue;
                }

                String receiptId = data[0];
                String userEmail = data[1];
                String customerName = data[2];
                LocalDateTime createdAt = LocalDateTime.parse(data[3]);
                double totalAmount = Double.parseDouble(data[4]);
                List<String> ticketIds = data[5].isEmpty()
                    ? new ArrayList<>()
                    : new ArrayList<>(Arrays.asList(data[5].split("\\|")));

                receipts.add(new Receipt(receiptId, userEmail, customerName, createdAt, totalAmount, ticketIds));
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Laden von receipts.csv: " + e.getMessage());
        }
    }
}



