package ui.dialogs;

import domain.Receipt;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

/**
 * Die Klasse ReceiptDialog zeigt die Details einer einzelnen Quittung in einem separaten Fenster.

 */

public final class ReceiptDialog {

    private ReceiptDialog() {
    }

    public static void show(Stage owner, Receipt receipt) {
        if (receipt == null) {
            return;
        }

        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.setTitle("Quittung " + receipt.getReceiptId());

        VBox root = new VBox(8);
        root.setPadding(new Insets(15));

        Label title = new Label("Quittung " + receipt.getReceiptId());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        root.getChildren().addAll(
            title,
            new Label("Kunde: " + receipt.getCustomerName()),
            new Label("E-Mail: " + receipt.getUserEmail()),
            new Label("Zeitpunkt: " + receipt.getCreatedAt().format(dtf)),
            new Label(String.format("Gesamtbetrag: %.2f EUR", receipt.getTotalAmount())),
            new Label("Tickets:")
        );

        for (String ticketId : receipt.getTicketIds()) {
            root.getChildren().add(new Label("- " + ticketId));
        }

        stage.setScene(new Scene(root, 460, 340));
        stage.show();
    }
}



