package ui;

import domain.*;
import domain.Event.EventType;
import exceptions.*;
import repository.*;
import service.BookingService;
import controller.SeatSelectionController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.format.DateTimeFormatter;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import javafx.scene.paint.Color;

public class App extends Application {
    
    private Stage primaryStage;
    private final EventRepository eventRepo = EventRepository.getInstance();
    private final BookingService bookingService = new BookingService();
    private final UserRepository userRepo = new UserRepository();
    private User loggedInUser = null;
    private Runnable postLoginAction = null; // Merkt sich was nach dem Login passieren soll
    private List<Seat> cartSeats = new ArrayList<>();

    // Globale Zustände für den Buchungsprozess
    private Event currentSelectedEvent = null;
    private Section currentSelectedSection = null;
    private Label selectionStatusLabel = new Label("Kein Platz ausgewählt");
    private long customerIdCounter = 1L;

    // Methode, zum prüfen, ob jemand eingeloggt ist
    public boolean isLoggedIn() {
        return loggedInUser != null;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Arena Ticketsystem OOP");

        showMainMenu();
    }

    // --- SCREEN 1: HAUPTMENÜ ---
    private void showMainMenu() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f5f5f7;");

        // Dynamische Header Bar
        javafx.scene.layout.HBox headerBar = new javafx.scene.layout.HBox(15);
        headerBar.setPadding(new Insets(10, 15, 10, 15));
        headerBar.setAlignment(Pos.CENTER_RIGHT);
        headerBar.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 8px;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);"
        );

        Label dateTimeLabel = new Label();
        dateTimeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e; -fx-font-size: 13px;");

        DateTimeFormatter clockFormatter = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy | HH:mm:ss", java.util.Locale.GERMAN);

        javafx.animation.Timeline clockTimer = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.ZERO, e -> 
                dateTimeLabel.setText(java.time.LocalDateTime.now().format(clockFormatter))
            ),
            new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1))
        );
        clockTimer.setCycleCount(javafx.animation.Animation.INDEFINITE);
        clockTimer.play();

        Region headSpacer = new Region();
        HBox.setHgrow(headSpacer, Priority.ALWAYS);

        headerBar.getChildren().addAll(dateTimeLabel, headSpacer);

        if (isLoggedIn()) {
            Label welcomeLabel = new Label("Angemeldet als: " + loggedInUser.getFirstName() + " " + loggedInUser.getLastName());
            welcomeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            Button myTicketsButton = new Button("Meine Tickets");
            myTicketsButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px; -fx-cursor: hand;");
            myTicketsButton.setOnAction(e -> {
                clockTimer.stop();
                showMyTicketsView();
            });

            Button logoutButton = new Button("Abmelden");
            logoutButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 4px; -fx-cursor: hand;");
            logoutButton.setOnAction(e -> {
                clockTimer.stop();
                loggedInUser = null;
                showMainMenu();
            });

            headerBar.getChildren().addAll(welcomeLabel, myTicketsButton, logoutButton);
        } else {
            Label guestLabel = new Label("Sie sind als Gast unterwegs.");
            guestLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");

            Button loginButton = new Button("Anmelden / Registrieren");
            loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px; -fx-cursor: hand;");
            loginButton.setOnAction(e -> {
                clockTimer.stop();
                showLoginView();
            });

            headerBar.getChildren().addAll(guestLabel, loginButton);
        }

        Label title = new Label("ARENA TICKETSYSTEM");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Wählen Sie ein Event aus:");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d");

        DateTimeFormatter germanDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm 'Uhr'");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        javafx.scene.layout.FlowPane cardContainer = new javafx.scene.layout.FlowPane();
        cardContainer.setHgap(20);
        cardContainer.setVgap(20);
        cardContainer.setPadding(new Insets(10));
        cardContainer.setAlignment(Pos.CENTER);

        List<Event> events = eventRepo.getAllEvents();
        List<VBox> eventCards = new ArrayList<>();

        for (Event event : events) {
            VBox card = new VBox(10);
            card.setPadding(new Insets(20));
            card.setMinWidth(220);
            card.setPrefWidth(220);
            card.setMaxWidth(220);
            card.setMinHeight(160);
            card.setAlignment(Pos.TOP_LEFT);
            card.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #bdc3c7;" + 
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: Hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 3);"
            );

            // Titel des Events
            Label eventTitle = new Label(event.getTitle());
            eventTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");
            eventTitle.setWrapText(true);
            eventTitle.setMinHeight(Region.USE_PREF_SIZE);
            eventTitle.setMaxHeight(Double.MAX_VALUE);
            eventTitle.setAlignment(Pos.TOP_LEFT);

            // Flexibler Abstandshalter
            javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
            javafx.scene.layout.VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            // Datum des Events
            Label eventDate = new Label(event.getDateTime().format(germanDateTimeFormatter));
            eventDate.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

            // Eventtyp auf der Karte
            Label eventTypeLabel = new Label(event.getEventType().toString());
            eventTypeLabel.setStyle(
                "-fx-background-color: #2c3e50;" + 
                "-fx-text-fill: white;" +
                "-fx-padding: 3px 8px;" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 4px;"
            );

            card.getChildren().addAll(eventTitle, spacer, eventDate, eventTypeLabel);

            // Hover Effekt
            card.setOnMouseEntered(e -> {
                if (currentSelectedEvent != event) {
                    card.setStyle(
                        "-fx-background-color: #fdfdfd;" +
                        "-fx-border-color: #2c3e50;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(41,128,185,0.2), 8, 0, 0, 4);"
                    );
                }
            });

            card.setOnMouseExited(e -> {
                if (currentSelectedEvent != event) {
                    card.setStyle(
                        "-fx-background-color: white;" +
                        "-fx-border-color: #bdc3c7;" + 
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 3);"
                    );
                }
            });

            // Klick & Doppelklick
            card.setOnMouseClicked(mouseEvent -> {
                for (VBox otherCard : eventCards) {
                    otherCard.setStyle(
                        "-fx-background-color: white;" +
                        "-fx-border-color: #bdc3c7;" + 
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-cursor: Hand;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 3);"
                    );
                }

                // Event als ausgewählt markieren
                currentSelectedEvent = event;
                card.setStyle(
                    "-fx-background-color: #ebf5fb;" +
                    "-fx-border-color: #2c3e50;" +
                    "-fx-border-width: 2px;" +
                    "-fx-border-radius: 8px;" + 
                    "-fx-background-radius: 8px;" +
                    "-fx-cursor: Hand;" +
                    "-fx-effect: dropshadow(three-pass-box, rgba(41,128,185,0.3), 10, 0, 0, 5);"
                );

                // Doppelklick
                if (mouseEvent.getClickCount() == 2) {
                    showGraphicSectionSelection();
                }
            });

            eventCards.add(card);
            cardContainer.getChildren().add(card);
        }

        scrollPane.setContent(cardContainer);

        // Bestätigungs Button
        Button nextButton = new Button("Blöcke anzeigen");
        nextButton.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 6px; -fx-padding: 8px 15px;");
        nextButton.setPrefWidth(200);

        nextButton.setOnAction(e -> {
            if (currentSelectedEvent != null) {
                showGraphicSectionSelection();
            } else {
                showAlert(Alert.AlertType.WARNING, "Auswahl fehlt", "Bitte wählen Sie zuerst ein Event aus!");
            }
        });

        Label teamLabel = new Label("Entwickelt von: Lukas Beck, Maren Bohlig, Gian-Luca Levels, Hayat van Eck");
        teamLabel.setStyle(
            "-fx-font-site: 4px;" +
            "-fx-text-fill: #2c3e50;" +
            "-fx-font-style: italic;"
        );

        HBox footerBar = new HBox(teamLabel);
        footerBar.setAlignment(Pos.BOTTOM_RIGHT);
        footerBar.setPadding(new Insets(10,0,0,0));

        Region bottomSpacer = new Region();
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);

        root.getChildren().addAll(headerBar, title, subtitle, scrollPane, nextButton, bottomSpacer, footerBar);
        Scene scene = new Scene(root, 800, 700);
        primaryStage.setScene(scene);
        primaryStage.show();

        /* 
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f7;");
        
        Label title = new Label("ARENA TICEKETSYSTEM");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Deutsches Datenformat einfügen
        DateTimeFormatter germanDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm 'Uhr'");
        
        ListView<String> eventListView = new ListView<>();
        List<Event> events = eventRepo.getAllEvents();
        for (Event event : events) {
            eventListView.getItems().add(event.getTitle() + " | " + event.getDateTime().format(germanDateTimeFormatter));
        }
        
        // Doppelklick Event auf die Events
        eventListView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                int selectedIndex = eventListView.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0) {
                    currentSelectedEvent = events.get(selectedIndex);
                    showGraphicSectionSelection();
                }
            }
        });
        
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
    */
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


    // TICKET VIEW
    private void showMyTicketsView() {
        ensureLoggedIn(() -> {
            VBox root = new VBox(20);
            root.setPadding(new Insets(30));
            root.setAlignment(Pos.TOP_CENTER);
            root.setStyle("-fx-background-color: #f5f5f7;");

            Label title = new Label("MEINE TICKETS");
            title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

            VBox ticketContainer = new VBox(15);
            ticketContainer.setPadding(new Insets(10));
            ticketContainer.setAlignment(Pos.TOP_CENTER);

            List<Ticket> myTickets = loggedInUser.getPurchasedTickets();

            if (myTickets == null || myTickets.isEmpty()) {
                Label noTicketsLabel = new Label("Sie haben bisher noch keine Tickets gebucht.");
                noTicketsLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #7f8c8d; -fx-font-size: 14px");
                ticketContainer.getChildren().add(noTicketsLabel);
            } else {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm 'Uhr'");

                for (Ticket ticket : myTickets) {
                    HBox ticketCard = new HBox(20);
                    ticketCard.setPadding(new Insets(15));
                    ticketCard.setAlignment(Pos.CENTER_LEFT);
                    ticketCard.setStyle(
                        "-fx-background-color: white; " +
                        "-fx-border-color: #2ecc71; " + 
                        "-fx-border-width: 1px 1px 1px 5px; " + 
                        "-fx-border-radius: 4px; " +
                        "-fx-background-radius: 4px; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);"
                    );

                    VBox details = new VBox(5);

                    String eventTitle = ticket.getEvent() != null ? ticket.getEvent().getTitle() : "Event-Ticket";
                    String eventDate = ticket.getEvent() != null ? ticket.getEvent().getDateTime().format(dtf) : "";

                    Label lblEvent = new Label(eventTitle);
                    lblEvent.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");

                    Label lblDate = new Label(eventDate);
                    lblDate.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

                    String seatInfo;
                    if (ticket.getSection() instanceof StandingSection) {
                        seatInfo = ticket.getSection().getName();
                    } else if (ticket.getSection() instanceof SeatedSection) {
                        int[] rowAndSeat = parseRowAndSeat(ticket.getSeatInfo());
                        if (rowAndSeat[0] > 0 && rowAndSeat[1] > 0){
                            seatInfo = String.format("%s | Reihe %d | Sitz %d",
                                    ticket.getSection().getName(),
                                    rowAndSeat[0],
                                    rowAndSeat[1]
                            );
                        } else {
                            seatInfo = "Bereich: " + ticket.getSection().getName() + " | " + ticket.getSeatInfo();
                        }
                    } else {
                        seatInfo = "Bereich: " + ticket.getSection().getName();
                    }
                    Label lblSeat = new Label(seatInfo);
                    lblSeat.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60; -fx-font-size: 13px;");

                    String customerType = (ticket.getCustomer() != null && ticket.getCustomerType() != null)
                            ? ticket.getCustomerType()
                            : (ticket.getCustomerType() != null ? ticket.getCustomerType() : "Standard");

                    Label lblType = new Label("Typ: " + customerType);
                    lblType.setStyle("-fx-font-style: italic; -fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

                    details.getChildren().addAll(lblEvent, lblDate, lblSeat, lblType);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Label lblPrice = new Label(String.format("%.2f €", ticket.getFinalPrice()));
                    lblPrice.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

                    Button btnCancel = new Button("Stornieren");
                    btnCancel.setStyle(
                        "-fx-background-color: #e74c3c;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-background-radius: 4px;" +
                        "-fx-padding: 6px 12px;" +
                        "-fx-cursor: Hand;"
                    );

                    btnCancel.setOnAction(e -> {
                        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmAlert.setTitle("Ticket stornieren");
                        confirmAlert.setHeaderText("Möchten Sie dieses Ticket wirklich stornieren?");
                        String placeText = (ticket.getSection() instanceof StandingSection)
                                ? "Bereich: " + ticket.getSection().getName()
                                : "Platz: " + ticket.getSeatInfo();
                        confirmAlert.setContentText(
                            "Event: " + eventTitle + "\n" + 
                            placeText + "\n" +
                            "Preis: " + String.format("%.2f €", ticket.getFinalPrice())
                        );

                        Optional<ButtonType> result = confirmAlert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            boolean success = bookingService.cancelTicket(ticket, loggedInUser);

                            if (success) {
                                userRepo.saveUsersToFile();

                                showAlert(Alert.AlertType.INFORMATION, "Storniert", "Das Ticket wurde erfolgreich storniert.");
                                showMyTicketsView();
                            } else {
                                showAlert(Alert.AlertType.ERROR, "Fehler", "Das Ticket konnte leider nicht storniert werden.");
                            }
                        }
                    });

                    ticketCard.getChildren().addAll(details, spacer, lblPrice, btnCancel);
                    ticketContainer.getChildren().add(ticketCard);
                }
            }

            scrollPane.setContent(ticketContainer);

            Button backButton = new Button("Zurück zum Hauptmenü");
            backButton.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 6px; -fx-padding: 8px 15px; -fx-cursor: Hand;");
            backButton.setOnAction(e -> showMainMenu());

            root.getChildren().addAll(title, scrollPane, backButton);

            Scene scene = new Scene(root, 800, 700);
            primaryStage.setScene(scene);
            primaryStage.show();
        });
    }

    // LOGIN SCREEN
    private void showLoginView() {
        BorderPane mainRoot = new BorderPane();
        mainRoot.setStyle("-fx-background-color: #f5f5f7");

        VBox loginRoot = new VBox(15);
        loginRoot.setPadding(new Insets(40));
        loginRoot.setAlignment(Pos.CENTER);
        loginRoot.setStyle("-fx-background-color: transparent");

        Label title = new Label("KUNDEN LOGIN");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        TextField emailField = new TextField();
        emailField.setPromptText("Benutzername");
        emailField.setPrefWidth(250);
        emailField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Passwort");
        passwordField.setPrefWidth(250);
        passwordField.setMaxWidth(250);

        Button loginBtn = new Button("Einloggen");
        loginBtn.setDefaultButton(true);
        loginBtn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: Hand;");
        loginBtn.setPrefWidth(250);

        
        Label registerLink = new Label("Noch kein Konto? Hier registrieren");
        registerLink.setStyle("-fx-text-fill: #2980b9; -fx-cursor: hand;");
        
        loginRoot.getChildren().addAll(title, emailField, passwordField, loginBtn, registerLink);
        
        Button btnBackToMain = new Button("Zurück zum Hauptmenü");
        btnBackToMain.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand;");
        btnBackToMain.setPrefWidth(150);

        HBox bottomBar = new HBox(btnBackToMain);
        bottomBar.setPadding(new Insets(20));
        bottomBar.setAlignment(Pos.BOTTOM_LEFT);

        mainRoot.setCenter(loginRoot);
        mainRoot.setBottom(bottomBar);

        // Login Logik
        loginBtn.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();

            //Hilfsobjekt zur Verwaltung der registrierten Nutzer
            User user = userRepo.validateUser(email, password);
            if (user != null) {
                this.loggedInUser = user;

                if (this.postLoginAction != null) {
                    Runnable action = this.postLoginAction;
                    this.postLoginAction = null;
                    action.run();
                } else {
                    showMainMenu();
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Fehler", "Falscher Benutzername oder Passwort.");
            }
        });

        btnBackToMain.setOnAction(e -> {
            this.postLoginAction = null;
            showMainMenu();
        });

        registerLink.setOnMouseClicked(e -> showRegisterView());

        primaryStage.setScene(new Scene(mainRoot, 800, 700));
    }

    // REGISTRIERUNGS SCREEN
    private void showRegisterView() {
        BorderPane mainRoot = new BorderPane();
        mainRoot.setStyle("-fx-background-color: #f5f5f7");

        VBox registerRoot = new VBox(15);
        registerRoot.setPadding(new Insets(40));
        registerRoot.setAlignment(Pos.CENTER);
        registerRoot.setStyle("-fx-background-color: #f5f5f7;");

        Label title = new Label("NEUES KONTO ERSTELLEN");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Vorname");
        firstNameField.setPrefWidth(250);
        firstNameField.setMaxWidth(250);

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Nachname");
        lastNameField.setPrefWidth(250);
        lastNameField.setMaxWidth(250);

        TextField emailField = new TextField();
        emailField.setPromptText("E-Mail-Adresse");
        emailField.setPrefWidth(250);
        emailField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Passwort");
        passwordField.setPrefWidth(250);
        passwordField.setMaxWidth(250);

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Passwort bestätigen");
        confirmPasswordField.setPrefWidth(250);
        confirmPasswordField.setMaxWidth(250);

        Button registerBtn = new Button("Registrieren");
        registerBtn.setDefaultButton(true);
        registerBtn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: Hand;");
        registerBtn.setPrefWidth(250);

        Label backToLoginLink = new Label("Bereits ein Konto? Zum Login");
        backToLoginLink.setStyle("-fx-text-fill: #2980b9; -fx-cursor: hand;");

        registerBtn.setOnAction(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Fehler", "Bitte füllen Sie alle Felder aus.");
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                showAlert(Alert.AlertType.WARNING, "Fehler", "Bitte geben Sie eine gültige E-Mail-Adresse ein.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                showAlert(Alert.AlertType.ERROR, "Fehler", "Die eingegebenen Passwörter stimmen nicht überein!");
                passwordField.clear();
                confirmPasswordField.clear();
                return;
            }

            User newUser = new User(firstName, lastName, email, password);

            boolean success = userRepo.registerUser(newUser);

            if(success) {
                showAlert(Alert.AlertType.INFORMATION, "Erfolg", "Registrierung erfolgreich!");
                showLoginView();
            } else {
                showAlert(Alert.AlertType.ERROR, "Fehler", "Diese E-Mail-Adresse ist bereits registriert.");
            }
        });

        backToLoginLink.setOnMouseClicked(e -> showLoginView());

        registerRoot.getChildren().addAll(
            title,
            firstNameField,
            lastNameField,
            emailField,
            passwordField,
            confirmPasswordField,
            registerBtn,
            backToLoginLink
        );

        primaryStage.setScene(new Scene(registerRoot, 800, 700));
    }

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
        stageLabel.setStyle("-fx-background-color: #ff0000; -fx-padding: 5 50 5 50; -fx-text-fill: white;");

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
        controller.populateSeatPlan(currentSelectedSection, cartSeats);

        selectionStatusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        selectionStatusLabel.setText("Kein Platz ausgewählt");

        Button confirmButton = new Button("Sitzplatz bestätigen");
        confirmButton.setStyle("-fx-background-color: #d4af37; -fx-text-fill: #2c3e50;");

        confirmButton.setOnAction(e -> {
            List<Seat> newSeats = controller.getSelectedSeats();
            if (!newSeats.isEmpty()) {
                cartSeats.addAll(newSeats);
                showCartView();
            } else if (!cartSeats.isEmpty()) {
                showCartView();
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
        confirmButton.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
        confirmButton.setPrefWidth(200);

        confirmButton.setOnAction(e -> {
            int count = ticketSpinner.getValue();
            for (int i = 1; i <= count; i++) {
                Seat seat = new Seat(0, i);
                seat.setSection(currentSelectedSection);
                cartSeats.add(seat);
            }
            showCartView();
        });

        Button backButton = new Button("Zurück zum Saalplan");
        backButton.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white;");
        backButton.setOnAction(e -> showGraphicSectionSelection());

        root.getChildren().addAll(header, infoLabel, ticketSpinner, confirmButton, backButton);
        Scene scene = new Scene(root, 800, 700);
        primaryStage.setScene(scene);
    }

    // --- SCREEN 3: WARENKORB ANSICHT ---
    private void showCartView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f7");

        Label title = new Label("WARENKORB");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50");

        VBox formContainer = new VBox(10);
        formContainer.setAlignment(Pos.CENTER);

        List<ComboBox<String>> typeComboBoxes = new ArrayList<>();

        double basePrice = (currentSelectedEvent != null) ? currentSelectedEvent.getBasePrice() : 0.0;

        if (cartSeats == null || cartSeats.isEmpty()) {
            Label emptyLabel = new Label("Ihr Warenkorb ist derzeit leer.");
            emptyLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
            formContainer.getChildren().add(emptyLabel);
        } else {
            for (int i = 0; i < cartSeats.size(); i++) {
                final int index = i;
                Seat seat = cartSeats.get(i);

                Section seatSection = (seat != null && seat.getSection() != null)
                                    ? seat.getSection()
                                    : currentSelectedSection;

                double sectionFactor = (seatSection != null) ? seatSection.getPriceFactor() : 1.0;
                double singleTicketPrice = basePrice * sectionFactor;

                HBox row = new HBox(15);
                row.setAlignment(Pos.CENTER);
                row.setPadding(new Insets(5,10,5,10));
                row.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: #dcdde1;" +
                    "-fx-border-radius: 4px;" +
                    "-fx-background-radius: 4px;"
                );

                String seatLabelText;
                if (seatSection instanceof StandingSection) {
                    seatLabelText = "Innenraum (Stehplatz)";
                } else if (seat != null) {
                    seatLabelText = seatSection.getName() + ", Reihe " + seat.getRowNumber() + ", Platz " + seat.getSeatNumber();
                } else {
                    seatLabelText = "Ticket " + (i + 1);
                }

                Label lblSeat = new Label(seatLabelText);
                lblSeat.setStyle("-fx-pref-width: 180px; -fx-alignment: center-left;");

                Label lblPrice = new Label(String.format("%.2f €", singleTicketPrice));
                lblPrice.setStyle("-fx-pref-width: 80px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

                ComboBox<String> cbType = new ComboBox<>();
                cbType.getItems().addAll("Standard","Student","Rentner","Kind");
                cbType.setValue("Standard");
                cbType.setPrefWidth(110);

                cbType.setOnAction(e -> {
                    String selectedType = cbType.getValue();
                    double discount = getDiscountFactor(selectedType);
                    double finalPrice = singleTicketPrice * discount;

                    lblPrice.setText(String.format("%.2f €", finalPrice));
                });

                Button btnDelete = new Button("X");
                btnDelete.setStyle(
                    "-fx-background-color: #e74c3c;" + 
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 4px;" +
                    "-fx-cursor: hand"
                );

                btnDelete.setOnAction(e -> {
                    cartSeats.remove(index);
                    showCartView();
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

        Button btnAddMore = new Button("Weitere Tickets hinzufügen");
        btnAddMore.setStyle(
            "-fx-background-color: #413f3ff7;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-pref-width: 250px;" +
            "-fx-pref-height: 35px;"
        );
        btnAddMore.setOnAction(e -> showGraphicSectionSelection());

        Button btnFinalBook = new Button("Jetzt kostenpflichtig buchen");
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
            ensureLoggedIn(() -> {
                List<String> chosenTypes = new ArrayList<>();
                for (ComboBox<String> cb : typeComboBoxes) {
                    chosenTypes.add(cb.getValue());
                }

                if (chosenTypes.size() != cartSeats.size()) {
                    showAlert(Alert.AlertType.ERROR, "Fehler", "Fehler bei der Zuordnung der Ticket-Typen.");
                }
    
                executeBooking(new ArrayList<>(cartSeats), chosenTypes);
                cartSeats.clear();
            });
        });

        buttonBox.getChildren().addAll(btnFinalBook, btnAddMore);

        root.getChildren().addAll(title, scrollPane, buttonBox);
        primaryStage.setScene(new Scene(root, 800, 700));
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

    private void executeBooking(List<Seat> chosenSeats, List<String> chosenTypes) {
        String firstName = loggedInUser.getFirstName();
        String lastName = loggedInUser.getLastName();
        String userEmail = loggedInUser.getEmail();

        List<Ticket> generatedTickets = new ArrayList<>();
        double totalExtendedPrice = 0.0;

        try {
            for (int i = 0; i < chosenSeats.size(); i++) {
                Seat seat = chosenSeats.get(i);
                String currentType = chosenTypes.get(i);

                Section seatSection = (seat != null && seat.getSection() != null)
                                        ? seat.getSection()
                                        : currentSelectedSection;

                Customer customer = new Customer(customerIdCounter++, firstName, lastName, currentType);

                Ticket ticket;
                if (seatSection instanceof SeatedSection) {
                    ticket = bookingService.bookSpecificTicket(currentSelectedEvent.getId(), seatSection.getName(), seat.getRowNumber(), seat.getSeatNumber(), customer, userEmail);
                } else {
                    ticket = bookingService.bookTicket(currentSelectedEvent.getId(), seatSection.getName(), customer, userEmail);
                }

                generatedTickets.add(ticket);
                totalExtendedPrice += ticket.getFinalPrice();
            }

            for (Ticket ticket : generatedTickets) {
                loggedInUser.addTicket(ticket);
            }
            userRepo.saveUsersToFile();

            StringBuilder successMessage = new StringBuilder();
            successMessage.append(String.format("Käufer: %s %s\nGesamtpreis: %.2f EUR\n\nGekaufte Tickets:\n", firstName, lastName, totalExtendedPrice));

            for (Ticket t : generatedTickets) {
                successMessage.append(String.format("- %s | (%s) - %.2f EUR\n",
                    t.getSection() != null ? t.getSection().getName() : "Bereich",
                    //t.getSeatInfo(),
                    t.getCustomer().getCustomerType(),
                    t.getFinalPrice()
                ));
            }
                
                /* 
                if (currentSelectedSection instanceof StandingSection) {
                    successMessage.append(String.format("- Stehplatz %s (%s) - %.2f EUR\n", 
                    t.getTicketId(), 
                    t.getCustomer().getCustomerType(), 
                    t.getFinalPrice()
                ));
            } else {
                successMessage.append(String.format("- Sitzplatz %s (%s) - %.2f EUR\n",
            t.getTicketId(),
            t.getCustomer().getCustomerType(),
            t.getFinalPrice()
        ));
    }
}
*/

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Buchung erfolgreich!");
            successAlert.setHeaderText("Tickets erfolgreich gebucht.");
            successAlert.setContentText(successMessage.toString());
            successAlert.showAndWait();

            cartSeats.clear();
            showMainMenu();
    } catch (Exception ex) {
        showAlert(Alert.AlertType.ERROR, "Fehler bei der Buchung", ex.getMessage());
    }
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
            583.2, 160.0,
            423.2, 160.0,
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
        standingArea.setStyle("-fx-cursor: hand;");
        standingArea.setFill(Color.web("#2c3e50", 0.15));
        standingArea.setStroke(Color.web("#2c3e50", 0.4));
        standingArea.setStrokeWidth(1);

        // Tooltip für den Stehplatzbereich
        Section standingSection = findSectionByName("Innenraum (Stehplatz)");
        if (standingSection != null && currentSelectedEvent != null) {
            double calculatedPrice = currentSelectedEvent.getBasePrice() * standingSection.getPriceFactor();

            Tooltip standingTooltip = new Tooltip(String.format(
                "Innenraum (Stehplatz)\n" +
                "-----------------------\n"+
                "Ticketpreis: %.2f €",
                calculatedPrice
            ));
            standingTooltip.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-background-color: #2c3e50;" +
                "-fx-text-fill: white;" +
                "-fx-padding: 8px;" +
                "-fx-background-radius: 4px;"
            );
            standingTooltip.setShowDelay(javafx.util.Duration.millis(100));
            Tooltip.install(standingArea, standingTooltip);
        }

        standingArea.setOnMouseEntered(e -> standingArea.setFill(Color.web("#2c3e50", 0.5)));
        standingArea.setOnMouseExited(e -> standingArea.setFill(Color.web("#2c3e50", 0.15)));
        standingArea.setOnMouseClicked(e -> {
            currentSelectedSection = findSectionByName("Innenraum (Stehplatz)");
            if (currentSelectedSection instanceof StandingSection) {
                showStandingAreaSelection();
            } else {
                showAlert(Alert.AlertType.WARNING, "Fehler", "Der Stehplatzbereich konnte nicht geladen werden.");
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
        369.0, 96.0,
        369.0, 137.0,
        318.0, 138.0,
        318.0, 174.0,
        191.0, 174.0
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
        372.0, 96.0,
        551.0, 96.0,
        551.0, 174.0,
        422.0, 174.0,
        422.0, 138.0,
        372.0, 138.0
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

private StackPane createGalaLayout() {
    StackPane mapContainer = new StackPane();
    mapContainer.setStyle("-fx-background-color: #34495e; -fx-border-color: gold;");
    mapContainer.setPrefSize(600,400);

    Label placeholder = new Label("Gala-Saalplan (Noch in Entwicklung)");
    placeholder.setStyle("-fx-text-fill: white; -fx-font-size: 16px");

    mapContainer.getChildren().add(placeholder);
    return mapContainer;
}

// Hilfsmethode für das Stylen der Polygon um Code zu sparen
private void setupStandardBlock(Polygon block, String sectionName) {
    block.setStyle("-fx-cursor: hand;");
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

    Section section = findSectionByName(sectionName);

    if (section != null && currentSelectedEvent != null) {
        double calculatedPrice = currentSelectedEvent.getBasePrice() * section.getPriceFactor();

        String tooltipText = String.format(
            "%s\n" +
            "-----------------------\n" +
            "Ticketpreis: %.2f €",
            sectionName,
            calculatedPrice
        );

        Tooltip tooltip = new Tooltip(tooltipText);
        tooltip.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-background-color: #2c3e50;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 8px;" +
            "-fx-background-radius: 4px"
        );
        tooltip.setShowDelay(javafx.util.Duration.millis(100));

        Tooltip.install(block, tooltip);
    }
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

    private void ensureLoggedIn(Runnable onLoggedInAction) {
        if (this.loggedInUser != null) {
            onLoggedInAction.run();
        } else {
            this.postLoginAction = onLoggedInAction;

            showAlert(Alert.AlertType.INFORMATION, "Anmeldung erforderlich", "Bitte logge dich ein oder erstelle ein Konto, um die Buchung abzuschließen.");
        
            showLoginView();
        }
    }

    private int[] parseRowAndSeat(String seatInfoStr) {
        int[] result = new int[]{0,0};
        if (seatInfoStr == null) {
            return result;
        }
        try {
            String[] numbers = seatInfoStr.replaceAll("[^0-9]+", " ").trim().split("\\s+");
            if (numbers.length >= 2) {
                result[0] = Integer.parseInt(numbers[0]);
                result[1] = Integer.parseInt(numbers[1]);
            }
        } catch (Exception e) {

        }
        return result;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
