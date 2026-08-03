package ui.dialogs;

import domain.Receipt;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

/**
 * Die Klasse ReceiptDialog zeigt die Details einer einzelnen Quittung in einem
 * separaten Fenster.
 */
public final class ReceiptDialog {

    private ReceiptDialog() {
    }

    public static void show(Stage owner, Receipt receipt) {
        if (receipt == null) {
            return;
        }

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) {
            stage.initOwner(owner);
        }
        stage.setTitle("Quittung " + receipt.getReceiptId());

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.setStyle(
                "-fx-background-color: #ecf0f1;" +
                        "-fx-font-family: 'Segoe UI', sans-serif;");

        Label title = new Label("Quittung " + receipt.getReceiptId());
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        VBox contentBox = new VBox(6);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        contentBox.getChildren().addAll(
                createContentLabel("Kunde: " + receipt.getCustomerName()),
                createContentLabel("E-Mail: " + receipt.getUserEmail()),
                createContentLabel("Zeitpunkt: " + receipt.getCreatedAt().format(dtf)));

        // --- KAUFMÄNNISCHE BERECHNUNG (19% MwSt) ---
        double brutto = receipt.getTotalAmount();
        double netto = brutto / 1.19;
        double steuer = brutto - netto;

        // Visueller Trennstrich
        Label separator = new Label("--------------------------------------------------");
        separator.setStyle("-fx-text-fill: #bdc3c7;");

        Label lblNetto = createContentLabel(String.format("Netto (exkl. MwSt.): %.2f EUR", netto));
        Label lblTax = createContentLabel(String.format("zzgl. 19%% MwSt.: %.2f EUR", steuer));

        Label lblBrutto = createContentLabel(String.format("Gesamtbetrag (Brutto): %.2f EUR", brutto));
        lblBrutto.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px; -fx-font-weight: bold;");

        contentBox.getChildren().addAll(separator, lblNetto, lblTax, lblBrutto);

        contentBox.getChildren().add(createContentLabel("Tickets:"));
        for (String ticketId : receipt.getTicketIds()) {
            contentBox.getChildren().add(createContentLabel(" - " + ticketId));
        }

        Button btnClose = new Button("Schließen");
        btnClose.setStyle(
                "-fx-background-color: #7f8c8d;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: Hand;");
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
                        "-fx-line-spacing: 4px;");
        return label;
    }
}