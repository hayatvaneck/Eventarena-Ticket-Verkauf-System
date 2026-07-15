package ui;

import domain.*;
import domain.Event.EventType;
import exceptions.SeatAlreadyBookedException;
import repository.*;
import service.BookingService;
import controller.SeatSelectionController;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private Section currentSelectedSection = null;
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
        root.setStyle("-fx-background-color: #f5f5f7;");

        Label title = new Label("ARENA TICEKETSYSTEM");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        ListView<String> eventListView = new ListView<>();
        List<Event> events = eventRepo.getAllEvents();
        for (Event event : events) {
            eventListView.getItems().add("ID: " + event.getId() + " | " + event.getTitle() + " (" + event.getBasePrice() + " EUR)");
        }

        Button nextButton = new Button("Blöcke anzeigen");
        nextButton.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 6px; -fx-padding: 8px 15px");
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
        Scene scene = new Scene(root, 800, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /*
    // --- SCREEN 1b: BLOCKAUSWAHL ---    ERSETZT DURCH GRAFISCHE DARSTELLUNG
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
    */

    // --- SCREEN 2: SITZAUSWAHL ---
    public void showSeatSelection() {
        if (!(currentSelectedSection instanceof SeatedSection)) {
            showAlert(Alert.AlertType.ERROR, "Fehler", "Dieser Block besitzt keine Sitzplätze!");
            showGraphicSectionSelection();
            return;
        }

        SeatedSection seatedSection = (SeatedSection) currentSelectedSection;

        VBox root = new VBox(15);
        root.setPadding (new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label header = new Label("Saalplan für: " + seatedSection.getName());
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label stageLabel = new Label("--- BÜHNE / SPIELFELD ---");
        stageLabel.setStyle("-fx-background-color: #2c3e50; -fx-padding: 5 50 5 50; -fx-text-fill: white;");

        GridPane seatGrid = new GridPane();
        seatGrid.setHgap(6);
        seatGrid.setVgap(6);
        seatGrid.setAlignment(Pos.CENTER);

        seatGrid.setStyle(
            "-fx-border-color: #2c3e50; " +
            "-fx-border-width: 3px; " +
            "-fx-border-radius: 8px; " +
            "-fx-background-color: #f8f9fa; " + 
            "-fx-padding: 25px; " + 
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);"
        );

        SeatSelectionController controller = new SeatSelectionController(seatGrid, this);
        controller.populateSeatPlan(currentSelectedSection);

        selectionStatusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        selectionStatusLabel.setText("Kein Platz ausgewählt");

        Button confirmButton = new Button("Sitzplatz bestätigen");
        confirmButton.setStyle("-fx-background-color: #d4af37; -fx-text-fill: #2c3e50;");

        confirmButton.setOnAction(e -> {
            List<Seat> chosenSeats = controller.getSelectedSeats();
            if (!chosenSeats.isEmpty()) {
                showBookingForm(chosenSeats);
            } else {
                showAlert(Alert.AlertType.WARNING, "Kein Sitzplatz", "Bitte wählen Sie einen freien Sitzplatz aus!");
            }
        });

        Button backButton = new Button("Zurück zum Saalplan");
        backButton.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;");
        backButton.setOnAction(e -> showGraphicSectionSelection());

        root.getChildren().addAll(header, stageLabel, seatGrid, confirmButton, backButton, selectionStatusLabel);
        Scene scene = new Scene(root, 800, 700);
        primaryStage.setScene(scene);
    }

    // --- SCREEN 2b: STEHPLATZ-ANZAHL WÄHLEN ---
    public void showStandingAreaSelection() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f6fa;");

        Label header = new Label("Stehplatz-Auswahl: " + currentSelectedSection.getName());
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label infoLabel = new Label("Bitte wählen Sie die Anzahl der gewünschten Stehplatz-Tickets aus.");
        infoLabel.setStyle("-fx-font-size: 14px;");

        Spinner<Integer> ticketSpinner = new Spinner<>(1, 10, 1);
        ticketSpinner.setStyle("-fx-font-size: 16px;");
        ticketSpinner.setPrefWidth(100);

        Button confirmButton = new Button("Auswahl bestätigen");
        confirmButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        confirmButton.setPrefWidth(200);

        confirmButton.setOnAction(e -> {
            int count = ticketSpinner.getValue();
            List<Seat> virtualSeats = new ArrayList<>();

            for (int i = 1; i <= count; i++) {
                virtualSeats.add(new Seat(0, i));
            }

            showBookingForm(virtualSeats);
        });

        Button backButton = new Button("Zurück zum Saalplan");
        backButton.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white;");
        backButton.setOnAction(e -> showGraphicSectionSelection());

        root.getChildren().addAll(header, infoLabel, ticketSpinner, confirmButton, backButton);
        Scene scene = new Scene(root, 800, 700);
        primaryStage.setScene(scene);
    }

    // --- SCREEN 3: BUCHUNGSFORMULAR ---
    private void showBookingForm(List<Seat> chosenSeats) {
        VBox root = new VBox(12);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Personalisierung & Zahlung");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-filling: #2c3e50");

        StringBuilder seatInfo = new StringBuilder();
        boolean isStandingArea = false;

        for (Seat s : chosenSeats) {
            if (s.getRowNumber() == 0) {
                isStandingArea = true;
                break;
            }
            seatInfo.append(String.format("| Reihe: %d, Platz: %d ", s.getRowNumber(), s.getSeatNumber()));
        }

        String seatDetails = isStandingArea ? "Freie Platzwahl" : seatInfo.toString();

        // Deutsches Datenformat einfügen
        DateTimeFormatter germanDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm 'Uhr'");
        String formatiertesDatum = currentSelectedEvent.getDateTime().format(germanDateTimeFormatter);

        Label infoLabel = new Label(String.format("Event: %s\nDatum: %s\nBlock: %s\nDetails: %s",
                currentSelectedEvent.getTitle(), formatiertesDatum, currentSelectedSection.getName(), seatDetails));
        infoLabel.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 10;");

        TextField txtFirstName = new TextField();
        txtFirstName.setPromptText("Vorname");
        TextField txtLastName = new TextField();
        txtLastName.setPromptText("Nachname");

        txtFirstName.setMaxWidth(250);
        txtLastName.setMaxWidth(250);

        ComboBox<String> cbCustomerType = new ComboBox<>();
        cbCustomerType.getItems().addAll("REGULAR", "STUDENT", "RENTNER");
        cbCustomerType.setPromptText("Kundentyp auswählen");
        cbCustomerType.getSelectionModel().selectFirst();

        Button btnFinalBook = new Button("Kostenpflichtig buchen (" + chosenSeats.size() + " Tickets)");
        btnFinalBook.setStyle("-fx-background-color: #d4af37; -fx-text-fill: white; -fx-font-weight: bold;");
        btnFinalBook.setPrefWidth(200);

        btnFinalBook.setOnAction(e -> {
            String firstName = txtFirstName.getText().trim();
            String lastName = txtLastName.getText().trim();

            if (firstName.isBlank() || lastName.isBlank()) {
                showAlert(Alert.AlertType.ERROR, "Fehler", "Bitte füllen Sie alle Namensfelder aus!");
                return;
            }

            String nameRegex = "^[a-zA-ZäöüÄÖÜß\\s\\-]+$";

            if (!firstName.matches(nameRegex) || !lastName.matches(nameRegex)) {
                showAlert(Alert.AlertType.WARNING,
                    "Ungültige Namenseingabe",
                    "Die Namensfelder dürfen keine Zahlen oder Sonderzeichen enthalten. Bitte korrigieren Sie Ihre Eingabe.");
                    return;
            }

            Customer customer = new Customer(customerIdCounter++, txtFirstName.getText(), txtLastName.getText(), cbCustomerType.getValue());

            List<Ticket> generatedTickets = new ArrayList<>();
            double totalExtendedPrice = 0.0;

            try {
                if (currentSelectedSection instanceof SeatedSection) {

                    // Ausführung der Buchung im Service
                    for (Seat seat : chosenSeats) {
                        Ticket ticket = bookingService.bookSpecificTicket(
                            currentSelectedEvent.getId(),
                            currentSelectedSection.getName(),
                            seat.getRowNumber(),
                            seat.getSeatNumber(),
                            customer
                        );
                        generatedTickets.add(ticket);
                        totalExtendedPrice += ticket.getFinalPrice();
                    }
                } else if (currentSelectedSection instanceof StandingSection) {

                    for (Seat seat : chosenSeats) {
                        Ticket ticket = bookingService.bookTicket(
                            currentSelectedEvent.getId(), 
                            currentSelectedSection.getName(), 
                            customer
                        );
                        generatedTickets.add(ticket);
                        totalExtendedPrice += ticket.getFinalPrice();
                    }

                }
                    
                // Erfolgsmeldung
                StringBuilder successMessage = new StringBuilder();
                successMessage.append(String.format("Kunde: %s\nGesamtpreis: %.2f EUR\n\nGenerierte Ticket-IDs:\n",
                        customer.getFullName(), totalExtendedPrice));
                for (Ticket t : generatedTickets) {
                    successMessage.append("- ").append(t.getTicketId()).append("\n");
                }

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Buchung erfolgreich!");
                successAlert.setHeaderText("Tickets erfolgreich gebucht.");
                successAlert.setContentText(successMessage.toString());
                successAlert.showAndWait();

                // Zurück zum Hauptmenü
                showMainMenu();

            } catch (SeatAlreadyBookedException ex) {
                showAlert(Alert.AlertType.ERROR, "Buchung fehlgeschlagen", ex.getMessage());
            }
        });

        Button btnCancel = new Button("Abbrechen");
        btnCancel.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white;");
        btnCancel.setOnAction(e -> showGraphicSectionSelection());

        root.getChildren().addAll(title, infoLabel, new Label("Vorname:"), txtFirstName, new Label("Nachname:"), txtLastName,
                new Label("Kundentyp:"), cbCustomerType, btnFinalBook, btnCancel);
        Scene scene = new Scene(root, 800, 700);
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
        if (currentSelectedEvent == null) {
            return;
        }
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #ebe4e4;");

        Label title = new Label("Blockauswahl für: " + currentSelectedEvent.getTitle());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        // Auswahl des Saalplans
        StackPane mapContainer = null;

        if (currentSelectedEvent.getEventType() == EventType.BASKETBALL) {
            mapContainer = createBasketballLayout();
        } else if (currentSelectedEvent.getEventType() == EventType.CONCERT) {
            mapContainer = createConcertLayout();
        } else {
            mapContainer = createGalaLayout();
        }
        
        // Sicherheitscheck falls eine der drei Methoden null zurückgibt
        if (mapContainer == null) {
            System.err.println("KRITISCH: mapContainer ist null! Ein Fallback-Layout wird erzeugt.");
            mapContainer = new StackPane(new Label("Fehler: Saalplan-Layout ist null!"));
            mapContainer.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red;");
            mapContainer.setPrefSize(600, 400);
        }

        Button backButton = new Button("Zurück zu den Events");
        backButton.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white;");
        backButton.setOnAction(e -> showMainMenu());
 
        root.getChildren().add(title);
        root.getChildren().add(mapContainer);
        root.getChildren().add(backButton);

        Scene scene = new Scene(root, 800, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private StackPane createConcertLayout() {
        StackPane mapContainer = new StackPane();
        Pane clickLayer = new Pane();
        mapContainer.setStyle("-fx-border-color: rgba(0,0,0,0.1);");
        
        ImageView imageView = new ImageView();
        try {
            Image arenaMapImage = new Image(getClass().getResourceAsStream("/saalplan_stehplätze_innenraum.png"));
            imageView.setImage(arenaMapImage);
            imageView.setFitWidth(600);
            imageView.setPreserveRatio(true);
            mapContainer.getChildren().add(imageView);
        } catch (Exception e) {
            System.err.println("WARNUNG: Konzert-Bild konnte nicht geladen werden! Pfad prüfen.");
            mapContainer.setStyle("-fx-background-color: #cccccc; -fx-border-color: red");
            mapContainer.setPrefSize(600, 450);
        }
        /* 
        // 1. Hintergrundbild laden (Bilddatei muss im "resources" oder Hauptordner liegen)
        Image arenaMapImage = new Image(getClass().getResourceAsStream("/saalplan_stehplätze_innenraum.png"));
        ImageView imageView = new ImageView(arenaMapImage);
        imageView.setFitWidth(600);
        imageView.setPreserveRatio(true);
        */
        
        // 3. Klickbare Bereiche für die Blöcke erstellen
        // Block 1
        Polygon block1 = new Polygon(new double[]{
            373.6, 124.0,
            373.6, 68.0,
            583.2, 68.0,
            583.2, 159.2,
            423.2, 159.2,
            422.4, 124.0,
        });
        setupStandardBlock(block1, "Block 1");
    
        // Block 2
        Polygon block2 = new Polygon(new double[]{
            158.4, 160.0,
            320.0, 160.0,
            320.0, 125.0,
            368.8, 125.0,
            368.8, 68.0,
            158.4, 68.0
        });
        setupStandardBlock(block2, "Block 2");
    
        // Block 3
        Polygon block3 = new Polygon(new double[]{
            155.2, 354.4,
            368.8, 354.4,
            369.6, 447.2,
            156.0, 447.2
        });
        setupStandardBlock(block3, "Block 3");
    
        // Block 4
        Polygon block4 = new Polygon(new double[]{
            372.8, 354.4,
            583.2, 354.4,
            583.2, 447.2,
            372.8, 447.2
        });
        setupStandardBlock(block4, "Block 4");
    
        // Block 6
        Polygon block6 = new Polygon(new double[]{
            97.6, 176.0,
            166.4, 176.8,
            165.6, 336.0,
            97.6, 336.0
        });
        setupStandardBlock(block6, "Block 6");
        
        // VIP Block
        Polygon vipBlock = new Polygon(new double[]{
            319.2, 160.0,
            319.2, 124.0,
            421.6, 124.0,
            423.2, 160.0,
        });
        setupStandardBlock(vipBlock, "VIP");
    
        // Stehplätze
        Polygon standingArea = new Polygon(new double[]{
            185.6, 176.8,
            548.8, 176.8,
            548.8, 336.0,
            185.6, 336.0
        });
        standingArea.setFill(Color.web("#2c3e50", 0.15));
        standingArea.setStroke(Color.web("#2c3e50", 0.4));
        standingArea.setStrokeWidth(1);
        standingArea.setOnMouseEntered(e -> standingArea.setFill(Color.web("#2c3e50", 0.5)));
        standingArea.setOnMouseExited(e -> standingArea.setFill(Color.web("#2c3e50", 0.15)));
        standingArea.setOnMouseClicked(e -> {
            currentSelectedSection = findSectionByName("Innenraum (Stehplatz)");
            if (currentSelectedSection instanceof StandingSection) {
                Seat virtualStandingSeat = new Seat(0,0);
                List<Seat> chosenSeats = new ArrayList<>();
                chosenSeats.add(virtualStandingSeat);
                
                showBookingForm(chosenSeats);
            }
            
            if (currentSelectedSection != null) {
                showStandingAreaSelection();
            }
        });
        
        clickLayer.getChildren().addAll(block1, block2, block3, block4, block6, vipBlock, standingArea);
        mapContainer.getChildren().addAll(clickLayer);
        return mapContainer;
}

private StackPane createBasketballLayout() {
    StackPane mapContainer = new StackPane();
    Pane clickLayer = new Pane();
    mapContainer.setStyle("-fx-border-color: rgba(0,0,0,0.1);");

    Image arenaMapImage = new Image(getClass().getResourceAsStream("/saalplan_basketball.png"));
    ImageView imageView = new ImageView(arenaMapImage);
    imageView.setFitWidth(600);
    imageView.setPreserveRatio(true);

    /* 
    // Koordinaten für Polygone ausgeben lassen
    mapContainer.setOnMouseClicked(e -> {
        System.out.println("Punkt: " + e.getX() + ", " + e.getY() + ",");
    });
    
    mapContainer.getChildren().addAll(imageView, clickLayer);
    */
    

    Polygon block2 = new Polygon(new double[]{
        191.0, 96.0,
        369.6, 96.0,
        369.6, 137.6,
        318.4, 138.4,
        318.4, 174.4,
        191.0, 174.4
    });
    setupStandardBlock(block2, "Block 2");

    Polygon vipBlock = new Polygon(new double[]{
        318.4, 175.2,
        318.4, 139.0,
        422.4, 139.0,
        422.4, 175.2
    });
    setupStandardBlock(vipBlock, "VIP");

    Polygon block1 = new Polygon(new double[]{
        551.0, 96.0,
        373.0, 96.0,
        373.0, 137.6,
        422.4, 137.6,
        422.4, 174.4,
        551.0, 174.4
    });
    setupStandardBlock(block1, "Block 1");

    Polygon block6 = new Polygon(new double[]{
        140.8, 190.4,
        198.4, 190.4,
        198.4, 324.8,
        140.8, 324.8
    });
    setupStandardBlock(block6, "Block 6");

    Polygon block5 = new Polygon(new double[]{
        541.6, 190.4,
        600.8, 190.4,
        600.8, 324.8,
        541.6, 324.8
    });
    setupStandardBlock(block5, "Block 5");

    Polygon block3 = new Polygon(new double[]{
        190.4, 340.0,
        370.4, 340.0,
        370.4, 419.2,
        190.4, 419.2
    });
    setupStandardBlock(block3, "Block 3");

    Polygon block4 = new Polygon(new double[]{
        372.8, 340.0,
        552.8, 340.0,
        552.8, 419.2,
        372.8, 419.2
    });
    setupStandardBlock(block4, "Block 4");

    clickLayer.getChildren().addAll(block1, block2, vipBlock, block3, block4, block5, block6);
    mapContainer.getChildren().addAll(imageView, clickLayer);
    return mapContainer;
}

// Hilfsmethode für das Stylen der Polygon um Code zu sparen
private void setupStandardBlock(Polygon block, String sectionName) {
    block.setFill(Color.web("#2c3e50", 0.15));
    block.setStroke(Color.web("#2c3e50", 0.4));
    block.setStrokeWidth(1);

    block.setOnMouseEntered(e -> block.setFill(Color.web("#2c3e50", 0.5)));
    block.setOnMouseExited(e -> block.setFill(Color.web("#2c3e50", 0.15)));

    block.setOnMouseClicked(e -> {
        currentSelectedSection = findSectionByName(sectionName);
        if (currentSelectedSection != null) {
            showSeatSelection();
        }
    });
}

private StackPane createGalaLayout() {
    StackPane mapContainer = new StackPane();
    mapContainer.setStyle("-fx-background-color: #34495e; -fx-border-color: gold;");
    mapContainer.setPrefSize(600,400);

    Label placeholder = new Label("Gala-Saalplan (Noch in Entwicklung)");
    placeholder.setStyle("-fx-text-fill: white; -fx-font-size: 16px");

    mapContainer.getChildren().add(placeholder);
    return mapContainer;
}

        
        // 2. Container für das Bild und die klickbaren Bereiche
       

        

            //currentSelectedSection = findSectionByName("Innenraum (Stehplatz)");
            //if (currentSelectedSection != null) {
            //    showStandingAreaSelection();
            //}


    public Section findSectionByName(String name) {
        if (currentSelectedEvent == null || currentSelectedEvent.getSections() == null) {
            return null;
        }
        for (Section section : currentSelectedEvent.getSections()) {
            if (section.getName().equalsIgnoreCase(name)) {
                return section;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
