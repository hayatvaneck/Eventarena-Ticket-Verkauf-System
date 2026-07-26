package repository;

import domain.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse TicketRepository verwaltet die Persistenz von Tickets in der CSV-Datei.

 */

public class TicketRepository {

    private static TicketRepository instance;
    private final List<Ticket> tickets;
    private static final String CSV_FILE_PATH = "data/tickets.csv";
    private static final String CSV_SEPARATOR = ";";

    private TicketRepository() {
        this.tickets = new ArrayList<>();
        loadTicketsFromCSV();
    }

    public static synchronized TicketRepository getInstance() {
        if (instance == null) {
            instance = new TicketRepository();
        }
        return instance;
    }

    public synchronized void save(Ticket ticket) {
        this.tickets.add(ticket);
        appendTicketToCSV(ticket);
    }

    public List<Ticket> findAll() {
        return new ArrayList<>(this.tickets);
    }

    private void appendTicketToCSV(Ticket ticket) {
        File file = new File(CSV_FILE_PATH);
        boolean isNewFile = !file.exists() || file.length() == 0;

        try (FileOutputStream fos = new FileOutputStream(file, true);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            BufferedWriter writer = new BufferedWriter(osw)) {

            if (isNewFile) {
                writer.write("ticketId;eventId;sectionName;customerId;customerFirstName;customerLastName;customerType;finalPrice;seatInfo;userEmail");
                writer.newLine();
            }

            Customer customer = ticket.getCustomer();
            String csvLine = String.join(CSV_SEPARATOR, 
                    ticket.getTicketId(),
                    String.valueOf(ticket.getEvent().getId()),
                    ticket.getSection().getName(),
                    String.valueOf(ticket.getCustomer().getId()),
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getCustomerType(),
                    String.valueOf(ticket.getFinalPrice()),
                    ticket.getSeatInfo(),
                    ticket.getUserEmail()
            );

            writer.write(csvLine);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Fehler beim Schreiben in die CSV-Datei: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadTicketsFromCSV() {
        File file = new File(CSV_FILE_PATH);
        if(!file.exists()) {
            return;
        }

        EventRepository eventRepo = EventRepository.getInstance();

        try (FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(isr)) {

            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if(isHeader) {
                    isHeader = false;
                    continue;
                }

                if(line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(CSV_SEPARATOR);
                if (data.length < 10) {
                    continue;
                }

                String ticketId = data[0];
                Long eventId = Long.parseLong(data[1]);
                String sectionName = data[2];
                Long customerId = Long.parseLong(data[3]);
                String customerFirstName = data[4];
                String customerLastName = data[5];
                String customerType = data[6];
                double finalPrice = Double.parseDouble(data[7]);
                String seatInfo = data[8];
                String userEmail = data[9];

                Event event = eventRepo.findById(eventId);
                if (event == null) {
                    System.err.println("Event mit ID " + eventId + " fÃ¼r Ticket " + ticketId + "existiert nicht mehr. Ticket Ã¼bersprungen.");
                    continue;
                }

                Section section = event.findSectionByName(sectionName);
                if (section instanceof StandingSection) {
                    ((StandingSection) section).incrementSoldTickets();
                }
                
                if (section == null) {
                    System.err.println("Bereich " + sectionName + " fÃ¼r Ticket " + ticketId + " existiert nicht mehr. Ticket Ã¼bersprungen.");
                    continue;
                }

                Customer customer = new Customer(customerId, customerFirstName, customerLastName, customerType);

                Ticket ticket = new Ticket(ticketId, event, section, customer, finalPrice, seatInfo, userEmail);
                this.tickets.add(ticket);
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Fehler beim Laden der CSV-Datei: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public synchronized boolean deleteTickets(String ticketId) {
        if (ticketId == null) {
            return false;
        }

        boolean removed = this.tickets.removeIf(t -> t.getTicketId().equals(ticketId));

        if (removed) {
            rewriteTicketsCSV();
        }
        return removed;
    }

    private void rewriteTicketsCSV() {
        File file = new File(CSV_FILE_PATH);
        try (FileOutputStream fos = new FileOutputStream(file, false);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
             BufferedWriter writer = new BufferedWriter(osw)) {
            
            writer.write("ticketId;eventId;sectionName;customerId;customerFirstName;customerLastName;customerType;finalPrice,seatInfo;userEmail");
            writer.newLine();

            for (Ticket ticket : this.tickets) {
                Customer customer = ticket.getCustomer();
                String csvLine = String.join(CSV_SEPARATOR,
                        ticket.getTicketId(),
                        String.valueOf(ticket.getEvent().getId()),
                        ticket.getSection().getName(),
                        String.valueOf(ticket.getCustomer().getId()),
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getCustomerType(),
                        String.valueOf(ticket.getFinalPrice()),
                        ticket.getSeatInfo(),
                        ticket.getUserEmail()
                );
                writer.write(csvLine);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Aktualisieren der ticets.csv: " + e.getMessage());
        }
    }
}



