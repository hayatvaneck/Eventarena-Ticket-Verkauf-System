package ui.screens;

import domain.Event;
import domain.Seat;
import domain.Section;
import domain.StandingSection;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ui.App;
import ui.ScreenManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse CartScreen zeigt den Warenkorb, die Tickettypen und startet den kostenpflichtigen Buchungsvorgang.

 */

public class CartScreen extends BaseScreen {

    private final App app;

    public CartScreen(App app) {
        this.app = app;
    }

    @Override
    public Scene buildScene() {
        VBox root = createRoot(15, new Insets(30), Pos.CENTER);

        Label title = new Label("WARENKORB");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50");

        VBox formContainer = new VBox(10);
        formContainer.setAlignment(Pos.CENTER);

        List<ComboBox<String>> typeComboBoxes = new ArrayList<>();
        Event currentEvent = app.getCurrentSelectedEvent();
        double basePrice = currentEvent != null ? currentEvent.getBasePrice() : 0.0;

        List<Seat> cartSeats = app.getCartSeats();
        Section selectedSection = app.getCurrentSelectedSection();

        if (cartSeats == null || cartSeats.isEmpty()) {
            Label emptyLabel = new Label("Ihr Warenkorb ist derzeit leer.");
            emptyLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
            formContainer.getChildren().add(emptyLabel);
        } else {
            for (int i = 0; i < cartSeats.size(); i++) {
                final int index = i;
                Seat seat = cartSeats.get(i);

                Section seatSection = (seat != null && seat.getSection() != null) ? seat.getSection() : selectedSection;
                double sectionFactor = (seatSection != null) ? seatSection.getPriceFactor() : 1.0;
                double singleTicketPrice = basePrice * sectionFactor;

                HBox row = new HBox(15);
                row.setAlignment(Pos.CENTER);
                row.setPadding(new Insets(5, 10, 5, 10));
                row.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: #dcdde1;" +
                    "-fx-border-radius: 4px;" +
                    "-fx-background-radius: 4px;"
                );

                String seatLabelText;
                if (seatSection instanceof StandingSection) {
                    seatLabelText = "Innenraum (Stehplatz)";
                } else if (seat != null && seatSection != null) {
                    seatLabelText = seatSection.getName() + ", Reihe " + seat.getRowNumber() + ", Platz " + seat.getSeatNumber();
                } else {
                    seatLabelText = "Ticket " + (i + 1);
                }

                Label lblSeat = new Label(seatLabelText);
                lblSeat.setStyle("-fx-pref-width: 180px; -fx-alignment: center-left;");

                Label lblPrice = new Label(String.format("%.2f €", singleTicketPrice));
                lblPrice.setStyle("-fx-pref-width: 80px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

                ComboBox<String> cbType = new ComboBox<>();
                cbType.getItems().addAll("Standard", "Student", "Rentner", "Kind");
                cbType.setValue("Standard");
                cbType.setPrefWidth(110);

                cbType.setOnAction(e -> {
                    String selectedType = cbType.getValue();
                    double discount = getDiscountFactor(selectedType);
                    double finalPrice = singleTicketPrice * discount;
                    lblPrice.setText(String.format("%.2f €", finalPrice));
                });

                javafx.scene.control.Button btnDelete = new javafx.scene.control.Button("X");
                btnDelete.setStyle(
                    "-fx-background-color: #e74c3c;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 4px;" +
                    "-fx-cursor: hand"
                );

                btnDelete.setOnAction(e -> {
                    app.getCartSeats().remove(index);
                    app.navigateTo(ScreenManager.Screen.CART);
                });

                row.getChildren().addAll(lblSeat, lblPrice, cbType, btnDelete);
                formContainer.getChildren().add(row);
                typeComboBoxes.add(cbType);
            }
        }

        ScrollPane scrollPane = new ScrollPane(formContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #f5f5f7;");
        scrollPane.setPrefHeight(300);

        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        javafx.scene.control.Button btnAddMore = new javafx.scene.control.Button("Weitere Tickets hinzufügen");
        btnAddMore.setStyle(
            "-fx-background-color: #413f3ff7;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-pref-width: 250px;" +
            "-fx-pref-height: 35px;"
        );
        btnAddMore.setOnAction(e -> app.navigateTo(ScreenManager.Screen.GRAPHIC_SECTION_SELECTION));

        javafx.scene.control.Button btnFinalBook = new javafx.scene.control.Button("Jetzt kostenpflichtig buchen");
        btnFinalBook.setStyle(
            "-fx-background-color: #2c3e50;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-pref-width: 250px;" +
            "-fx-pref-height: 35px;"
        );

        if (cartSeats == null || cartSeats.isEmpty()) {
            btnFinalBook.setDisable(true);
        }

        btnFinalBook.setOnAction(e -> {
            List<String> chosenTypes = new ArrayList<>();
            for (ComboBox<String> cb : typeComboBoxes) {
                chosenTypes.add(cb.getValue());
            }
            app.bookCurrentCart(chosenTypes);
        });

        buttonBox.getChildren().addAll(btnFinalBook, btnAddMore);

        root.getChildren().addAll(title, scrollPane, buttonBox);
        return createDefaultScene(root);
    }

    private double getDiscountFactor(String customerType) {
        if (customerType == null) {
            return 1.0;
        }
        switch (customerType) {
            case "Student":
                return 0.8;
            case "Rentner":
                return 0.7;
            case "Kind":
                return 0.5;
            default:
                return 1.0;
        }
    }
}



