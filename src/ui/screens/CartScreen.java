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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import ui.App;
import domain.CartItem;
import ui.ScreenManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse CartScreen zeigt den Warenkorb, die Tickettypen
 * und startet den kostenpflichtigen Buchungsvorgang.
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
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        VBox formContainer = new VBox(10);
        formContainer.setAlignment(Pos.CENTER);

        List<ComboBox<String>> typeComboBoxes = new ArrayList<>();
        List<CartItem> cartItems = app.getCartItems();

        if (cartItems == null || cartItems.isEmpty()) {
            Label emptyLabel = createMutedInfoLabel("Ihr Warenkorb ist derzeit leer.");
            formContainer.getChildren().add(emptyLabel);
        } else {
            for (int i = 0; i < cartItems.size(); i++) {
                final int index = i;
                CartItem item = cartItems.get(i);

                Event event = item.getEvent();
                Section seatSection = item.getSection();
                Seat seat = item.getSeat();

                String eventName = event != null ? event.getTitle() : "Unbekanntes Event";
                double basePrice = event != null ? event.getBasePrice() : 0.0;
                double sectionFactor = seatSection != null ? seatSection.getPriceFactor() : 1.0;
                double singleTicketPrice = basePrice * sectionFactor;

                HBox row = new HBox(20);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(10, 14, 10, 14));
                row.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: #dcdde1;" +
                    "-fx-border-radius: 4px;" +
                    "-fx-background-radius: 4px;"
                );

                String seatLabelText;
                if (seatSection instanceof StandingSection) {
                    seatLabelText = "Stehplatz: " + seatSection.getName();
                } else if (seat != null && seatSection != null) {
                    seatLabelText = "Sitzplatz: " + seatSection.getName()
                            + ", Reihe " + seat.getRowNumber()
                            + ", Platz " + seat.getSeatNumber();
                } else {
                    seatLabelText = "Ticket " + (i + 1);
                }

                VBox ticketInfoBox = createVBox(4, Pos.CENTER_LEFT);

                Label lblEvent = new Label("Event: " + eventName);
                lblEvent.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

                Label lblSeat = new Label(seatLabelText);
                lblSeat.setStyle("-fx-text-fill: #34495e;");

                ticketInfoBox.getChildren().addAll(lblEvent, lblSeat);

                Label lblPrice = new Label(String.format("%.2f €", singleTicketPrice));
                lblPrice.setStyle("-fx-pref-width: 90px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

                ComboBox<String> cbType = new ComboBox<>();
                cbType.getItems().addAll("Standard", "Student", "Rentner", "Kind");
                cbType.setValue("Standard");
                cbType.setPrefWidth(110);

                VBox discountBox = new VBox(3);
                discountBox.setAlignment(Pos.CENTER_LEFT);

                Label lblDiscount = new Label("Rabatt:");
                lblDiscount.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");
                discountBox.getChildren().addAll(lblDiscount, cbType);

                cbType.setOnAction(e -> {
                    String selectedType = cbType.getValue();
                    double discount = getDiscountFactor(selectedType);
                    double finalPrice = singleTicketPrice * discount;
                    lblPrice.setText(String.format("%.2f €", finalPrice));
                });

                Region spacer = createHorizontalSpacer();
                HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

                javafx.scene.control.Button btnDelete = new javafx.scene.control.Button("X");
                btnDelete.setStyle(
                    "-fx-background-color: #e74c3c;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 6px 10px;" +
                    "-fx-background-radius: 4px;" +
                    "-fx-cursor: hand"
                );
                btnDelete.setAlignment(Pos.CENTER_RIGHT);
                VBox.setMargin(btnDelete, new Insets(0, 0, 0, 0));

                btnDelete.setOnAction(e -> {
                    app.getCartItems().remove(index);
                    app.navigateTo(ScreenManager.Screen.CART);
                });

                row.getChildren().addAll(ticketInfoBox, lblPrice, discountBox, spacer, btnDelete);
                formContainer.getChildren().add(row);
                typeComboBoxes.add(cbType);
            }
        }

        ScrollPane scrollPane = createTransparentScrollPane(formContainer);
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

        if (cartItems == null || cartItems.isEmpty()) {
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