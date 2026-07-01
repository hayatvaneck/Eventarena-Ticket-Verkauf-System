package ui;

import domain.*;
import exceptions.SeatAlreadyBookedException;
import repository.*;
import service.BookingService;
import controller.SeatSelectionController;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import javafx.scene.paint.Color;

public class App extends Application {
    
    private Stage primaryStage;
    private final EventRepository eventRepo = EventRepository.getInstance();
    private final BookingService bookingService = new BookingService();

    // Globale Zustände für den Buchungsprozess
    private Event currentSelectedEvent = null;
    private SeatedSection currentSelectedSection = null;
    private Label selectionStatusLabel = new Label("Kein Platz ausgewählt");
    private long customerIdCounter = 1L;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Arena Ticketsystem OOP");

        showMainMenu();
    }

    // --- SCREEN 1: HAUPTMENÜ ---
    private void showMainMenu() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f6fa;");

        Label title = new Label("ARENA TICEKETSYSTEM");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        ListView<String> eventListView = new ListView<>();
        List<Event> events = eventRepo.getAllEvents();
        for (Event event : events) {
            eventListView.getItems().add("ID: " + event.getId() + " | " + event.getTitle() + " (" + event.getBasePrice() + " EUR)");
        }

        Button nextButton = new Button("Blöcke anzeigen");
        nextButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px;");
        nextButton.setPrefWidth(200);

        nextButton.setOnAction(e -> {
            int selectedIndex = eventListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                currentSelectedEvent = events.get(selectedIndex);
                showGraphicSectionSelection();
                //showSectionSelection();
            } else {
                showAlert(Alert.AlertType.WARNING, "Auswahl fehlt","Bitte wählen Sie zuerst ein Event aus!");
            }
        });

        root.getChildren().addAll(title, new Label("Verfügbare Events:"), eventListView, nextButton);
        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- SCREEN 1b: BLOCKAUSWAHL ---
    private void showSectionSelection() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f6fa;");

        Label title = new Label("Blockauswahl");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label eventInfo = new Label("Event: " + currentSelectedEvent.getTitle());
        eventInfo.setStyle("-fx-font-style: italic;");

        ListView<String> sectionListView = new ListView<>();
        // Filtern der Blöcke mit Sitzplätzen
        List<SeatedSection> seatedSections = new ArrayList<>();
        for (Section section : currentSelectedEvent.getSections()) {
            if (section instanceof SeatedSection) {
                seatedSections.add((SeatedSection) section);
                sectionListView.getItems().add(section.getName() + " (Faktor: x" + section.getPriceFactor() + ")");
            }
        }

        Button nextButton = new Button("Sitzplätze anzeigen");
        nextButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px;");
        nextButton.setPrefWidth(200);

        nextButton.setOnAction(e -> {
            int selectedIndex = sectionListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                currentSelectedSection = seatedSections.get(selectedIndex);
                showSeatSelection();
            } else {
                showAlert(Alert.AlertType.WARNING, "Auswahl fehlt", "Bitte wählen Sie einen Sitzplatz-Block aus!");
            }
        });

        Button backButton = new Button("Zurück zu den Events");
        backButton.setOnAction(e -> showMainMenu());

        // Falls das Event keine Sitzplätze hat
        if (seatedSections.isEmpty()) {
            sectionListView.setPlaceholder(new Label("Keine Sitzplatz-Blöcke für dieses Event verfügbar."));
            nextButton.setDisable(true);
        }

        root.getChildren().addAll(title, eventInfo, new Label("Verfügbare Blöcke:"), sectionListView, nextButton, backButton);
        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
    }

    // --- SCREEN 2: SITZAUSWAHL ---
    public void showSeatSelection() {
        VBox root = new VBox(15);
        root.setPadding (new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label header = new Label("Saalplan für: " + currentSelectedSection.getName());
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label stageLabel = new Label("--- BÜHNE / SPIELFELD ---");
        stageLabel.setStyle("-fx-background-color: #bdc3c7; -fx-padding: 5 50 5 50;");

        GridPane seatGrid = new GridPane();
        seatGrid.setHgap(6);
        seatGrid.setVgap(6);
        seatGrid.setAlignment(Pos.CENTER);

        SeatSelectionController controller = new SeatSelectionController(seatGrid, this);
        controller.populateSeatPlan(currentSelectedSection);

        selectionStatusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2980b9;");
        selectionStatusLabel.setText("Kein Platz ausgewählt");

        Button confirmButton = new Button("Sitzplatz bestätigen");
        confirmButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");

        confirmButton.setOnAction(e -> {
            if (controller.getSelectedSeat() != null) {
                showBookingForm(controller.getSelectedSeat());
            } else {
                showAlert(Alert.AlertType.WARNING, "Kein Sitzplatz", "Bitte wählen Sie einen freien Sitzplatz aus!");
            }
        });

        Button backButton = new Button("Zurück zum Hauptmenü");
        backButton.setOnAction(e -> showMainMenu());

        root.getChildren().addAll(header, stageLabel, seatGrid, confirmButton, backButton, selectionStatusLabel);
        Scene scene = new Scene(root, 800, 650);
        primaryStage.setScene(scene);
    }

    // --- SCREEN 3: BUCHUNGSFORMULAR ---
    private void showBookingForm(Seat chosenSeat) {
        VBox root = new VBox(12);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Personalisierung & Zahlung");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label infoLabel = new Label(String.format("Gewähltes Event %s\nPlatz: Reihe %d, Sitz %d\nBlock: %s",
                currentSelectedEvent.getTitle(), chosenSeat.getRowNumber(), chosenSeat.getSeatNumber(), currentSelectedSection.getName()));
        infoLabel.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 10;");

        TextField txtFirstName = new TextField();
        txtFirstName.setPromptText("Vorname");
        TextField txtLastName = new TextField();
        txtLastName.setPromptText("Nachname");

        ComboBox<String> cbCustomerType = new ComboBox<>();
        cbCustomerType.getItems().addAll("REGULAR", "STUDENT", "RENTNER", "VIP");
        cbCustomerType.setPromptText("Kundentyp auswählen");
        cbCustomerType.getSelectionModel().selectFirst();

        Button btnFinalBook = new Button("Kostenpflichtig buchen");
        btnFinalBook.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnFinalBook.setPrefWidth(200);

        btnFinalBook.setOnAction(e -> {
            if (txtFirstName.getText().isBlank() || txtLastName.getText().isBlank()) {
                showAlert(Alert.AlertType.ERROR, "Fehler", "Bitte füllen Sie alle Namensfelder aus!");
                return;
            }

            Customer customer = new Customer(customerIdCounter++, txtFirstName.getText(), txtLastName.getText(), cbCustomerType.getValue());

            try {
                // Ausführung der Buchung im Service
                Ticket ticket = bookingService.bookSpecificTicket(
                    currentSelectedEvent.getId(),
                    currentSelectedSection.getName(),
                    chosenSeat.getRowNumber(),
                    chosenSeat.getSeatNumber(),
                    customer
                );

                // Erfolgsmeldung
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Buchung erfolgreich!");
                successAlert.setHeaderText("Ticket erfolgreiche gebucht: " + ticket.getTicketId());
                successAlert.setContentText(String.format("Event: %s\nKunde: %s\nEndpreis: %.2f EUR",
                        ticket.getEvent().getTitle(), ticket.getCustomer().getFullName(), ticket.getFinalPrice()));
                successAlert.showAndWait();

                // Zurück zum Hauptmenü
                showMainMenu();
            } catch (SeatAlreadyBookedException ex) {
                showAlert(Alert.AlertType.ERROR, "Buchung fehlgeschlagen", ex.getMessage());
            }
        });

        Button btnCancel = new Button("Abbrechen");
        btnCancel.setOnAction(e -> showSeatSelection());

        root.getChildren().addAll(title, infoLabel, new Label("Vorname:"), txtFirstName, new Label("Nachname:"), txtLastName,
                new Label("Kundentyp:"), cbCustomerType, btnFinalBook, btnCancel);
        Scene scene = new Scene(root, 450, 550);
        primaryStage.setScene(scene);
    }

    public void updateSelectionLabel(String text) {
        selectionStatusLabel.setText(text);
    }

    public void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void showGraphicSectionSelection() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #ebe4e4;");

        Label title = new Label("Wählen Sie Ihren Wunschblock aus");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-font-color: #333333;");

        // 1. Hintergrundbild laden (Bilddatei muss im "resources" oder Hauptordner liegen)
        Image arenaMapImage = new Image(getClass().getResourceAsStream("/saalplan_stehplätze_innenraum.png"));
        ImageView imageView = new ImageView(arenaMapImage);
        imageView.setFitWidth(600);
        imageView.setPreserveRatio(true);

        
        // 2. Container für das Bild und die klickbaren Bereiche
        StackPane mapContainer = new StackPane();
        Pane clickLayer = new Pane();

        // 3. Klickbare Bereiche für die Blöcke erstellen
        // Block 1
        Polygon block1 = new Polygon(new double[]{
            372.0, 159.0, // unten links
            372.0, 66.4, // oben links
            583.0, 66.4, // oben rechts
            583.0, 159.0 // unten rechts
        });

        block1.setFill(Color.rgb(52, 152, 219, 0.2)); // leicht transparentes Blau
        block1.setStroke(Color.DARKBLUE); // Rahmenlinie
        block1.setStrokeWidth(1);

        // Hover-Effekt:
        block1.setOnMouseEntered(e -> block1.setFill(Color.rgb(52, 152, 219, 0.6)));
        block1.setOnMouseExited(e -> block1.setFill(Color.rgb(52, 152, 219, 0.2)));

        // Klick-Event:
        block1.setOnMouseClicked(e -> {
            currentSelectedSection = findSectionByName("Block 1");
            if (currentSelectedSection != null) {
                showSeatSelection();
            }
        });

        // Block 2
        Polygon block2 = new Polygon(new double[]{
            368.8, 159.0,
            156.8, 159.0,
            156.8, 66.4,
            368.8, 65.0
        });

        block2.setFill(Color.rgb(46, 204, 113, 0.2)); // leicht transparentes Grün
        block2.setStroke(Color.DARKGREEN);
        block2.setStrokeWidth(1);
        block2.setOnMouseEntered(e -> block2.setFill(Color.rgb(46, 204, 113, 0.6)));
        block2.setOnMouseExited(e -> block2.setFill(Color.rgb(46, 204, 113, 0.2)));
        block2.setOnMouseClicked(e -> {
            currentSelectedSection = findSectionByName("Block 2");
            if (currentSelectedSection != null) {
                showSeatSelection();
            }
        });

        // Block 3
        Polygon block3 = new Polygon(new double[]{
            155.2, 354.4,
            368.8, 354.4,
            369.6, 447.2,
            156.0, 447.2
        });
        block3.setFill(Color.rgb(231, 76, 60, 0.2));
        block3.setStroke(Color.DARKRED);
        block3.setStrokeWidth(1);
        block3.setOnMouseEntered(e -> block3.setFill(Color.rgb(231, 76, 60, 0.6)));
        block3.setOnMouseExited(e -> block3.setFill(Color.rgb(231, 76, 60, 0.2)));
        block3.setOnMouseClicked(e -> {
            currentSelectedSection = findSectionByName("Block 3");
            if (currentSelectedSection != null) {
                showSeatSelection();
            }
        });

        // Block 4
        Polygon block4 = new Polygon(new double[]{
            372.8, 354.4,
            583.2, 354.4,
            583.2, 447.2,
            372.8, 447.2
        });
        block4.setFill(Color.rgb(155, 89, 182, 0.2));
        block4.setStroke(Color.DARKVIOLET);
        block4.setStrokeWidth(1);
        block4.setOnMouseEntered(e -> block4.setFill(Color.rgb(155, 89, 182, 0.6)));
        block4.setOnMouseExited(e -> block4.setFill(Color.rgb(155, 89, 182, 0.2)));
        block4.setOnMouseClicked(e -> {
            currentSelectedSection = findSectionByName("Block 4");
            if (currentSelectedSection != null) {
                showSeatSelection();
            }
        });

        // Block 6
        Polygon block6 = new Polygon(new double[]{
            97.6, 176.0,
            166.4, 176.8,
            165.6, 336.0,
            97.6, 336.0
        });
        block6.setFill(Color.rgb(52, 152, 219, 0.2));
        block6.setStroke(Color.DARKBLUE);
        block6.setStrokeWidth(1);
        block6.setOnMouseEntered(e -> block6.setFill(Color.rgb(52, 152, 219, 0.6)));
        block6.setOnMouseExited(e -> block6.setFill(Color.rgb(52, 152, 219, 0.2)));
        block6.setOnMouseClicked(e -> {
            currentSelectedSection = findSectionByName("Block 6");
            if (currentSelectedSection != null) {
                showSeatSelection();
            }
        });

        // Stehplätze
        Polygon standingArea = new Polygon(new double[]{
            185.6, 176.8,
            548.8, 176.8,
            548.8, 336.0,
            185.6, 336.0
        });
        standingArea.setFill(Color.rgb(241, 196, 15, 0.2));
        standingArea.setStroke(Color.GOLDENROD);
        standingArea.setStrokeWidth(1);
        standingArea.setOnMouseEntered(e -> standingArea.setFill(Color.rgb(241, 196, 15, 0.6)));
        standingArea.setOnMouseExited(e -> standingArea.setFill(Color.rgb(241, 196, 15, 0.2)));
        standingArea.setOnMouseClicked(e -> {
            currentSelectedSection = findSectionByName("Stehplätze");
            if (currentSelectedSection != null) {
                showSeatSelection();
            }
        });

        clickLayer.getChildren().addAll(block1, block2, block3, block4, block6, standingArea);

        mapContainer.getChildren().addAll(imageView, clickLayer);

        Button backButton = new Button("Zurück zu den Events");
        backButton.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white;");
        backButton.setOnAction(e -> showMainMenu());

        root.getChildren().addAll(title, mapContainer, backButton);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public SeatedSection findSectionByName(String name) {
        for (Section section : currentSelectedEvent.getSections()) {
            if (section instanceof SeatedSection && section.getName().equalsIgnoreCase(name)) {
                return (SeatedSection) section;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
