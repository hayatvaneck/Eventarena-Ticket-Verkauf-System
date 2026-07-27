package ui;

import domain.*;
import domain.Event.EventType;
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

import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
        Scene scene = new Scene(root, 900, 700);
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
           
            //Hover / doesn't work
            //backButton.setOnMouseEntered(e -> btn.setStyle(btn.getStyle().replace(bgHex, "#ffffff")));
            //backButton.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace("#ffffff", bgHex)));
            
            root.getChildren().addAll(title, scrollPane, backButton);

            Scene scene = new Scene(root, 900, 700);
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

        primaryStage.setScene(new Scene(mainRoot, 900, 700));
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

        primaryStage.setScene(new Scene(registerRoot, 900, 700));
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
        stageLabel.setStyle("-fx-background-color: #7f8c8d; -fx-padding: 5 50 5 50; -fx-text-fill: white;");

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
        Scene scene = new Scene(root, 900, 700);
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
        Scene scene = new Scene(root, 900, 700);
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
        primaryStage.setScene(new Scene(root, 900, 700));
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
        root.setStyle("-fx-background-color: #ffffff;");

        javafx.scene.layout.FlowPane labelContainer = new javafx.scene.layout.FlowPane();
        labelContainer.setHgap(5);
        labelContainer.setVgap(5);
        labelContainer.setPadding(new Insets(5));
        labelContainer.setAlignment(Pos.CENTER);

        Label title = new Label("Blockauswahl für: " + currentSelectedEvent.getTitle());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");

        VBox titleBox = new VBox(10);
            titleBox.setPadding(new Insets(5));
            //titleBox.setMinWidth(220);
            //titleBox.setPrefWidth(220);
            //titleBox.setMaxWidth(220);
            //titleBox.setMinHeight(160);
            titleBox.setAlignment(Pos.CENTER);
            titleBox.setStyle(
                "-fx-background-color: #2c3e50;" +
                "-fx-border-color: #bdc3c7;" + 
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;" +
                //"-fx-cursor: Hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 3);"
            );

        titleBox.getChildren().add(title);
        labelContainer.getChildren().add(titleBox);
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
            mapContainer.setStyle("-fx-background-color: #7f8c8d; -fx-border-color: red;");
            mapContainer.setPrefSize(600, 400);
        }

        Button backButton = new Button("Zurück zu den Events");
        backButton.setStyle("-fx-background-color: #333333; -fx-text-fill: white;");
        backButton.setOnAction(e -> showMainMenu());
 
        root.getChildren().add(labelContainer);
        root.getChildren().add(mapContainer);
        root.getChildren().add(backButton);

        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

   private StackPane createConcertLayout() {
        StackPane mapContainer = new StackPane();

        VBox arenaWrapper = new VBox(10);
        arenaWrapper.setAlignment(Pos.CENTER);
        arenaWrapper.setPadding(new Insets(10));
        arenaWrapper.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 16; -fx-border-color: #cbd5e1; -fx-border-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.04), 8, 0, 0, 2);");

        // GRID OF SECTIONS
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        // TOP ROW: Block 2 & Block 1
        Button block2Btn = createBlockButton("Block 2", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 215+20, 85+10);
        Button block1Btn = createBlockButton("Block 1", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 215+20, 85+10);

        // MIDDLE ROW: Block 6, VIP Balkon, INNENRAUM, BÜHNE
        Button block6Btn = createBlockButton("Block 6", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 100, 180+20);
        Button vipBtn = createBlockButton("VIP", "#fde047", "#d97706", "#78350f", "BALKON", 70, 180+20);
        Button standingBtn = createBlockButton("Innenraum (Stehplatz)", "#e0e7ff", "#2563eb", "#1d4ed8", "INNENRAUM (Stehplätze)", 442, 180);

        // BÜHNE Block
        StackPane stageBox = new StackPane();
        Rectangle stageRect = new Rectangle(90, 180, Color.web("#0f172a"));
        stageRect.setArcWidth(14);
        stageRect.setArcHeight(14);
        stageRect.setStroke(Color.web("#334155"));
        stageRect.setStrokeWidth(2);

        Label stageLabel = new Label("B Ü H N E");
        stageLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        stageLabel.setTextFill(Color.WHITE);
        stageLabel.setRotate(-90);
        stageBox.getChildren().addAll(stageRect, stageLabel);

        // BOTTOM ROW: Block 3 & Block 4
        Button block3Btn = createBlockButton("Block 3", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 215+20, 85+10);
        Button block4Btn = createBlockButton("Block 4", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 215, 85+10);

        // GRID ASSEMBLY
        grid.add(block2Btn, 2, 0);
        grid.add(block1Btn, 3, 0);

        grid.add(block6Btn, 0, 1);
        grid.add(vipBtn, 1, 1);
        grid.add(standingBtn, 2, 1, 2, 1); // Spans cols 2 & 3
        grid.add(stageBox, 4, 1);

        grid.add(block3Btn, 2, 2);
        grid.add(block4Btn, 3, 2);

        // Legend
        HBox legend = new HBox(28);
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(10, 0, 0, 0));
        legend.getChildren().addAll(
            createLegendItem("Sitzplatz Blöcke", "#bae6fd", "#0284c7"),
            createLegendItem("Balkon", "#fde047", "#d97706"),
            createLegendItem("Innenraum (Stehplatz)", "#e0e7ff", "#2563eb"),
            createLegendItem("Bühne", "#0f172a", "#334155")
        );

        arenaWrapper.getChildren().addAll(grid, legend);
        mapContainer.getChildren().add(arenaWrapper);
        
        return mapContainer;
    }

    // --- HILFSMETHODEN (direkt in die App.java einfügen) ---

    private Button createBlockButton(String name, String bgHex, String borderHex, String textHex, String subtitle, double width, double height) {
        // Sucht die Section mit der vorhandenen Methode aus App.java
        Section sec = findSectionByName(name);
        if (sec == null) {
            if (name.equals("VIP")) sec = findSectionByName("Loge");
            if (name.contains("Innenraum")) sec = findSectionByName("Parkett (Sitzplätze)");
        }

        Button btn = new Button();
        btn.setPrefSize(width, height);

        String secName = sec != null ? sec.getName() : name;
        double priceFactor = sec != null ? sec.getPriceFactor() : 1.0;
        
        // Greift auf das Event in App.java zu
        double calcPrice = (currentSelectedEvent != null) ? currentSelectedEvent.getBasePrice() * priceFactor : 0.0;

        VBox content = new VBox(3);
        content.setAlignment(Pos.CENTER);

        Label nameLbl = new Label(secName.toUpperCase());
        nameLbl.setFont(Font.font("System", FontWeight.BOLD, 14));
        nameLbl.setTextFill(Color.web(textHex));

        Label subLbl = new Label(subtitle);
        subLbl.setFont(Font.font("System", 11));
        subLbl.setTextFill(Color.web(textHex));

        Label priceLbl = new Label(String.format("%.2f €", calcPrice));
        priceLbl.setFont(Font.font("System", FontWeight.BOLD, 13));
        priceLbl.setTextFill(Color.web(borderHex));

        content.getChildren().addAll(nameLbl, subLbl, priceLbl);
        btn.setGraphic(content);

        btn.setStyle(String.format(
            "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 2; -fx-border-radius: 12; -fx-background-radius: 12; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 4, 0, 0, 1);",
            bgHex, borderHex
        ));

        // Klick-Logik an App.java angepasst
        final Section targetSection = sec;
        btn.setOnAction(e -> {
            if (targetSection != null) {
                currentSelectedSection = targetSection;
                
                // Entscheiden, ob es ein Stehplatz oder Sitzplatz ist
                if (targetSection.getName().contains("Stehplatz") || targetSection instanceof StandingSection) {
                    showStandingAreaSelection();
                } else {
                    // Hier die Methode aufrufen, die du für normale Sitzplätze nutzt. 
                    // Passe den Namen an, falls deine Methode anders heißt (z.B. showSeatSelection())
                    showSeatSelection(); 
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Fehler", "Der Bereich '" + name + "' konnte im Event nicht gefunden werden.");
            }
        });

        // Hover-Effekte für besseres Feedback
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle().replace(bgHex, "#ffffff")));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace("#ffffff", bgHex)));

        return btn;
    }

    private HBox createLegendItem(String labelText, String bgHex, String borderHex) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER);

        Rectangle r = new Rectangle(14, 14, Color.web(bgHex));
        r.setArcWidth(4);
        r.setArcHeight(4);
        r.setStroke(Color.web(borderHex));
        r.setStrokeWidth(1.5);

        Label lbl = new Label(labelText);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 12));
        lbl.setTextFill(Color.web("#475569"));

        box.getChildren().addAll(r, lbl);
        return box;
    }

private StackPane createBasketballLayout() {
        StackPane mapContainer = new StackPane();
        
        VBox arenaWrapper = new VBox(20);
        arenaWrapper.setAlignment(Pos.CENTER);
        arenaWrapper.setPadding(new Insets(24));
        arenaWrapper.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 16; -fx-border-color: #cbd5e1; -fx-border-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.04), 8, 0, 0, 2);");

        // GRID OF SECTIONS
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        // Wir berechnen die Breite des Spielfelds passend zu Block 1 & 2 (235 + 235 + 12 Lücke = 482)
        double blockWidth = 235;
        double blockHeight = 95;
        double centerHeight = 200;

        // TOP ROW: Block 2 & Block 1
        Button block2Btn = createBlockButton("Block 2", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", blockWidth, blockHeight);
        Button block1Btn = createBlockButton("Block 1", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", blockWidth, blockHeight);

        // MIDDLE ROW: Block 6, VIP Balkon, SPIELFELD, Block 5
        Button block6Btn = createBlockButton("Block 6", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 100, centerHeight);
        Button vipBtn = createBlockButton("VIP", "#fde047", "#d97706", "#78350f", "VIP BALKON", 70, centerHeight);
        
        // SPIELFELD (Visuelles Element, nicht klickbar)
        StackPane courtBox = new StackPane();
        Rectangle courtRect = new Rectangle(482, centerHeight, Color.web("#fef3c7")); // Helle Holz-Farbe (Parkett)
        courtRect.setArcWidth(14);
        courtRect.setArcHeight(14);
        courtRect.setStroke(Color.web("#d97706")); // Dunkelorange Begrenzungslinie
        courtRect.setStrokeWidth(3);

        // Basketball-Flair: Mittellinie und Mittelkreis
        Rectangle centerLine = new Rectangle(3, centerHeight, Color.web("#d97706"));
        javafx.scene.shape.Circle centerCircle = new javafx.scene.shape.Circle(25, Color.TRANSPARENT);
        centerCircle.setStroke(Color.web("#d97706"));
        centerCircle.setStrokeWidth(3);

        Label courtLabel = new Label("S P I E L F E L D");
        courtLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        courtLabel.setTextFill(Color.web("#b45309")); // Dunkelbraun/Orange für Text
        courtLabel.setTranslateY(-centerHeight / 2 + 60); // Positioniert den Text oben im Spielfeld
        courtLabel.setTranslateX(-4); // Positioniert den Text leicht links

        // Alles übereinanderlegen
        courtBox.getChildren().addAll(courtRect, centerLine, centerCircle, courtLabel);

        // RECHTE SEITE: Block 5 (statt der Bühne)
        Button block5Btn = createBlockButton("Block 5", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 100, centerHeight);

        // BOTTOM ROW: Block 3 & Block 4
        Button block3Btn = createBlockButton("Block 3", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", blockWidth, blockHeight);
        Button block4Btn = createBlockButton("Block 4", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", blockWidth, blockHeight);

        // GRID ASSEMBLY
        // Reihe 0: Block 2 und 1
        grid.add(block2Btn, 2, 0);
        grid.add(block1Btn, 3, 0);

        // Reihe 1: Block 6, VIP, Spielfeld (nimmt 2 Spalten ein), Block 5
        grid.add(block6Btn, 0, 1);
        grid.add(vipBtn, 1, 1);
        grid.add(courtBox, 2, 1, 2, 1); // Spans cols 2 & 3
        grid.add(block5Btn, 4, 1);

        // Reihe 2: Block 3 und 4
        grid.add(block3Btn, 2, 2);
        grid.add(block4Btn, 3, 2);

        // Legende (Angepasst für Basketball)
        HBox legend = new HBox(28);
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(10, 0, 0, 0));
        legend.getChildren().addAll(
            createLegendItem("Sitzplatz Blöcke", "#bae6fd", "#0284c7"),
            createLegendItem("VIP Balkon", "#fde047", "#d97706"),
            createLegendItem("Spielfeld (Parkett)", "#fef3c7", "#d97706")
        );

        arenaWrapper.getChildren().addAll(grid, legend);
        mapContainer.getChildren().add(arenaWrapper);
        
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
