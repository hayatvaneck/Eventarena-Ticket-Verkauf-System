package ui.dialogs;

import domain.Receipt;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

/**
 * Die Klasse ReceiptHistoryDialog zeigt die gespeicherten Quittungen eines Benutzers und öffnet ausgewählte Einträge.

 */

public final class ReceiptHistoryDialog {

    /** Verhindert die Instanziierung der ausschließlich statisch genutzten Klasse. */
    private ReceiptHistoryDialog() {
    }

    /**
     * Zeigt alle übergebenen Quittungen chronologisch auswählbar in einem
     * separaten Fenster an.
     *
     * @param owner übergeordnetes Hauptfenster
     * @param receipts anzuzeigende Quittungen
     * @param onOpenReceipt Aktion zum Öffnen der ausgewählten Quittung
     */
    public static void show(Stage owner, List<Receipt> receipts, Consumer<Receipt> onOpenReceipt) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.setTitle("Gespeicherte Quittungen");

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label title = new Label("Quittungen");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ListView<String> receiptList = new ListView<>();
        receiptList.setPrefHeight(250);

        if (receipts == null || receipts.isEmpty()) {
            receiptList.getItems().add("Keine Quittungen gefunden.");
            receiptList.setDisable(true);
        } else {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            for (Receipt receipt : receipts) {
                receiptList.getItems().add(
                    receipt.getReceiptId() + " | " +
                    receipt.getCreatedAt().format(dtf) + " | " +
                    String.format("%.2f EUR", receipt.getTotalAmount())
                );
            }
        }

        Button openButton = new Button("Quittung öffnen");
        openButton.setDisable(receipts == null || receipts.isEmpty());
        openButton.setOnAction(e -> {
            int selectedIndex = receiptList.getSelectionModel().getSelectedIndex();
            if (receipts != null && selectedIndex >= 0 && selectedIndex < receipts.size()) {
                onOpenReceipt.accept(receipts.get(selectedIndex));
            }
        });

        root.getChildren().addAll(title, receiptList, openButton);
        stage.setScene(new Scene(root, 500, 350));
        stage.show();
    }
}



