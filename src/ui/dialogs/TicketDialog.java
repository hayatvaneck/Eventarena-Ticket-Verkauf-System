package ui.dialogs;

import domain.Ticket;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

/**
 * Die Klasse TicketDialog zeigt die Details eines einzelnen Tickets in einem separaten Fenster.

 */

public final class TicketDialog {

    private static final int MAX_PREVIEW_LENGTH = 80; // Ab dieser Länge wird die Beschreibung gekürzt

    private TicketDialog() {
    }

    public static void show(Stage owner, Ticket ticket) {
        if (ticket == null) {
            return;
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) {
            stage.initOwner(owner);
        }
        stage.setTitle("Event / Ticket");

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.setStyle(
            "-fx-background-color: #ecf0f1;" +
            "-fx-font-family: 'Segoe UI', sans-serif;"
        );

        Label title = new Label("Ticket " + ticket.getTicketId());
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        String eventName = ticket.getEvent() != null ? ticket.getEvent().getTitle() : "-";
        String eventDate = ticket.getEvent() != null ? ticket.getEvent().getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : "-";

        final String fullDescription = (ticket.getEvent() != null && ticket.getEvent().getDescription() != null)
            ? ticket.getEvent().getDescription().trim()
            : "Keine Eventbeschreibung vorhanden.";

        String sectionName = ticket.getSection() != null ? ticket.getSection().getName() : "-";
        String customerType = ticket.getCustomerType() != null ? ticket.getCustomerType() : "Standard";

        VBox contentBox = new VBox(6);

        Label lblEvent = createContentLabel("Event: " + eventName);
        Label lblDate = createContentLabel("Datum: " + eventDate);

        // Beschreibung mit Ein-/Ausklapp Funktion
        VBox descBox = new VBox(2);
        Label lblDesc = createContentLabel("");
        lblDesc.setWrapText(true);
        lblDesc.setMaxWidth(380);

        if (fullDescription.length() > MAX_PREVIEW_LENGTH) {
            String shortDescription = fullDescription.substring(0, MAX_PREVIEW_LENGTH) + ". . .";
            lblDesc.setText("Beschreibung: " + shortDescription);

            Hyperlink toggleLink = new Hyperlink("Mehr anzeigen");
            toggleLink.setStyle(
                "-fx-text-fill: #2c3e50; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 12px; " +
                "-fx-focus-color: transparent; " +
                "-fx-faint-focus-color: transparent; " +
                "-fx-underline: false;"
            );

            toggleLink.setOnAction(e -> {
                if (toggleLink.getText().equals("Mehr anzeigen")) {
                    lblDesc.setText("Beschreibung: " + fullDescription);
                    toggleLink.setText("Weniger anzeigen");
                } else {
                    lblDesc.setText("Beschreibung: " + shortDescription);
                    toggleLink.setText("Mehr anzeigen");
                }
                stage.sizeToScene();
            });

            descBox.getChildren().addAll(lblDesc, toggleLink);
        } else {
            lblDesc.setText("Beschreibung: " + fullDescription);
            descBox.getChildren().add(lblDesc);
        }

        Label lblSection = createContentLabel("Bereich: " + sectionName);
        Label lblSeat = createContentLabel("Platz: " + ticket.getSeatInfo());
        Label lblType = createContentLabel("Typ: " + customerType);
        Label lblPrice = createContentLabel(String.format("Preis: %.2f EUR", ticket.getFinalPrice()));
        lblPrice.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 13px; -fx-font-weight: bold;");

        contentBox.getChildren().addAll(
            lblEvent,
            lblDate, 
            descBox,
            lblSection,
            lblSeat,
            lblType,
            lblPrice   
        );

        Button btnClose = new Button("Schließen");
        btnClose.setStyle(
            "-fx-background-color: #7f8c8d;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: Hand;"
        );
        btnClose.setOnAction(e -> stage.close());

        root.getChildren().addAll(title, contentBox, btnClose);

        Scene scene = new Scene(root, 420, -1);
        stage.setScene(scene);
        stage.show();
    }

    private static Label createContentLabel(String text) {
        Label label = new Label(text);
        label.setStyle(
            "-fx-text-fill: #2c3e50;" +
            "-fx-font-size: 13px;" +
            "-fx-line-spacing: 4px;"
        );
        return label;
    }
}



