package ui.dialogs;

import domain.Ticket;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

/**
 * Die Klasse TicketDialog zeigt die Details eines einzelnen Tickets in einem separaten Fenster.

 */

public final class TicketDialog {

    private TicketDialog() {
    }

    public static void show(Stage owner, Ticket ticket) {
        if (ticket == null) {
            return;
        }

        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.setTitle("Event / Ticket");

        VBox root = new VBox(8);
        root.setPadding(new Insets(15));

        Label title = new Label("Ticket " + ticket.getTicketId());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        String eventName = ticket.getEvent() != null ? ticket.getEvent().getTitle() : "-";
        String eventDate = ticket.getEvent() != null ? ticket.getEvent().getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : "-";
        String eventDescription = (ticket.getEvent() != null && ticket.getEvent().getDescription() != null)
            ? ticket.getEvent().getDescription().trim()
            : "";
        if (eventDescription.isEmpty()) {
            eventDescription = "Keine Eventbeschreibung vorhanden.";
        }
        String sectionName = ticket.getSection() != null ? ticket.getSection().getName() : "-";
        String customerType = ticket.getCustomerType() != null ? ticket.getCustomerType() : "Standard";

        root.getChildren().addAll(
            title,
            new Label("Event: " + eventName),
            new Label("Datum: " + eventDate),
            new Label("Beschreibung: " + eventDescription),
            new Label("Bereich: " + sectionName),
            new Label("Platz: " + ticket.getSeatInfo()),
            new Label("Typ: " + customerType),
            new Label(String.format("Preis: %.2f EUR", ticket.getFinalPrice()))
        );

        stage.setScene(new Scene(root, 420, 280));
        stage.show();
    }
}



