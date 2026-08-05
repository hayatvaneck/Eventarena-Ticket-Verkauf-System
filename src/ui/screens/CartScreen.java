package ui.screens;

import domain.Event;
import domain.Seat;
import domain.Section;
import domain.StandingSection;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
        // --- 1. NEUES LAYOUT: BORDERPANE ---
        javafx.scene.layout.BorderPane root = new javafx.scene.layout.BorderPane();
        root.setStyle("-fx-background-color: #f5f5f7;");

        // --- 2. SCHICKES KÄSTCHEN FÜR DEN TITEL ---
        Label title = createTitle("WARENKORB");
        Label subtitle = createSubtitle("Ihre ausgewählten Tickets im Überblick:");

        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setMaxWidth(400);
        headerBox.setStyle(
                "-fx-background-color: white; " +
                        "-fx-padding: 15 30 15 30; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 5, 0, 0, 2);");
        headerBox.getChildren().addAll(title, subtitle);

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
                                "-fx-background-radius: 4px;");

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
                cbType.getItems().addAll("Standard", "Student (-20%)", "Rentner (-30%)", "Kind (-50%)");
                cbType.setValue("Standard");
                cbType.setPrefWidth(140);

                VBox discountBox = new VBox(3);
                discountBox.setAlignment(Pos.CENTER_LEFT);

                Label lblDiscount = new Label("Rabatt:");
                lblDiscount.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");
                discountBox.getChildren().addAll(lblDiscount, cbType);

                cbType.setOnAction(e -> {
                    String selectedType = cbType.getValue();
                    // 1. Text in den echten CustomerType (Enum) umwandeln
                    domain.CustomerType cType = app.mapCustomerType(selectedType);
                    // 2. Den echten Rabatt vom BookingService berechnen lassen
                    double discount = app.getBookingService().calculateDiscountFactor(cType);

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
                                "-fx-cursor: hand");
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

        // --- BUTTONS NEBENEINANDER & RICHTIGES DESIGN ---
        Button btnAddMore = createBackButton("Weitere Tickets hinzufügen");
        btnAddMore.setPrefWidth(300);
        btnAddMore.setMinHeight(45);
        btnAddMore.setMaxHeight(45);
        btnAddMore.setOnAction(e -> {
            // --- NEU: LIMIT-PRÜFUNG WIE VOM KOLLEGEN VORGESCHLAGEN ---
            if (app.getCartItems().size() >= 10) {
                app.showAlert(Alert.AlertType.WARNING, "Limit erreicht",
                        "Sie haben bereits die maximale Anzahl von 10 Tickets im Warenkorb.");
                return; // Bricht ab, der Nutzer bleibt direkt im Warenkorb!
            }

            // Wenn noch gar kein Event ausgewählt wurde (z.B. direkt nach Registrierung),
            // schicken wir den Nutzer zurück ins Hauptmenü zur Event-Auswahl.
            if (app.getCurrentSelectedEvent() == null) {
                app.navigateTo(ScreenManager.Screen.MAIN_MENU);
            } else {
                app.navigateTo(ScreenManager.Screen.GRAPHIC_SECTION_SELECTION);
            }
        });

        Button btnFinalBook = createSelectingButton("Jetzt kostenpflichtig buchen");
        btnFinalBook.setPrefWidth(300);
        btnFinalBook.setMinHeight(45);
        btnFinalBook.setMaxHeight(45);

        if (cartItems == null || cartItems.isEmpty()) {
            btnFinalBook.setDisable(true);
        }

       btnFinalBook.setOnAction(e -> {
            // --- NEU: ZUERST DEN LOGIN-STATUS PRÜFEN ---
            if (app.getLoggedInUser() == null) {
                // Wenn nicht eingeloggt: Login aufrufen und danach zurück in den Warenkorb leiten!
                app.ensureLoggedIn(() -> app.navigateTo(ScreenManager.Screen.CART));
                return; // Kauf-Aktion hier zwingend abbrechen, damit nicht im Hintergrund gebucht wird
            }

            // --- WENN EINGELOGGT: GANZ NORMAL BUCHEN ---
            List<String> chosenTypes = new ArrayList<>();
            for (ComboBox<String> cb : typeComboBoxes) {
                chosenTypes.add(cb.getValue());
            }
            app.bookCurrentCart(chosenTypes);
        });

        // Die Buttons in eine HBox packen, damit sie nebeneinander stehen!
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(btnAddMore, btnFinalBook);

        // --- PERFEKTER ZUSAMMENBAU FÜR BORDERPANE ---

        VBox topBox = createRoot(20, new Insets(30, 30, 20, 30), Pos.TOP_CENTER);
        topBox.getChildren().addAll(headerBox, scrollPane);

        HBox dummyFooter = createInvisibleStandardFooter();
        VBox bottomBox = createRoot(10, new Insets(0, 30, 30, 30), Pos.BOTTOM_CENTER);
        bottomBox.getChildren().addAll(buttonBox, dummyFooter);

        root.setCenter(topBox);
        root.setBottom(bottomBox);

        return createDefaultScene(root);
    }

}