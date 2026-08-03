package ui.screens;

import domain.Event;
import domain.User;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;
import repository.EventRepository;
import ui.App;
import ui.ScreenManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Die Klasse MainMenuScreen zeigt die Eventauswahl und den Einstieg in den
 * Buchungsprozess.
 */

public class MainMenuScreen extends BaseScreen {

    private static final String CARD_DEFAULT_STYLE = "-fx-background-color: white;" +
            "-fx-border-color: #bdc3c7;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: Hand;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 3);";

    private static final String CARD_HOVER_STYLE = "-fx-background-color: #fdfdfd;" +
            "-fx-border-color: #2c3e50;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(41,128,185,0.2), 8, 0, 0, 4);";

    private static final String CARD_SELECTED_STYLE = "-fx-background-color: #ebf5fb;" +
            "-fx-border-color: #2c3e50;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: Hand;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(41,128,185,0.3), 10, 0, 0, 5);";

    private final App app;
    private final EventRepository eventRepo;

    public MainMenuScreen(
            App app,
            EventRepository eventRepo) {
        this.app = app;
        this.eventRepo = eventRepo;
    }

    @Override
    public Scene buildScene() {
        // Das Hauptlayout, das Mitte und Unten strikt voneinander trennt!
        javafx.scene.layout.BorderPane root = new javafx.scene.layout.BorderPane();
        root.setStyle("-fx-background-color: #f5f5f7;");

        // --- 1. OBERE BOX (Mitte) ---
        VBox topBox = createRoot(20, new Insets(30, 30, 20, 30), Pos.TOP_CENTER);

        HBox headerBar = createHeaderBar();
        Label title = createTitle("ARENA TICKETSYSTEM");
        Label subtitle = createSubtitle("Wählen Sie ein Event aus:");

        VBox headerBox = new VBox(5); // 5 Pixel Abstand zwischen Titel und Untertitel
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setMaxWidth(400); // Breite des Kastens (kannst du beliebig anpassen)
        headerBox.setStyle(
                "-fx-background-color: white; " +
                        "-fx-padding: 15 30 15 30; " + // Innenabstand, damit der Text Luft hat
                        "-fx-background-radius: 10; " +
                        // "-fx-border-color: #81b9ed; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 5, 0, 0, 2);");
        headerBox.getChildren().addAll(title, subtitle);

        FlowPane cardContainer = new FlowPane();
        cardContainer.setHgap(20);
        cardContainer.setVgap(20);
        cardContainer.setPadding(new Insets(10));
        cardContainer.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = createTransparentScrollPane(cardContainer);

        DateTimeFormatter germanDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm 'Uhr'");
        List<Event> events = eventRepo.getAllEvents();
        List<VBox> eventCards = new ArrayList<>();

        for (Event event : events) {
            VBox card = createEventCard(event, germanDateTimeFormatter, eventCards);
            eventCards.add(card);
            cardContainer.getChildren().add(card);

            if (app.getCurrentSelectedEvent() != null && app.getCurrentSelectedEvent().equals(event)) {
                card.setStyle(CARD_SELECTED_STYLE);
            }
        }
        scrollPane.setContent(cardContainer);

        // Die obere Box ist fertig! (KEIN Spacer mehr nötig)
        topBox.getChildren().addAll(headerBar, headerBox, scrollPane);

        // --- 2. UNTERE BOX (Button + Footer) ---
        VBox bottomBox = createRoot(10, new Insets(0, 30, 30, 30), Pos.BOTTOM_CENTER);

        Button nextButton = createConfirmButton("Blöcke anzeigen");
        nextButton.setPrefWidth(300);
        nextButton.setMinHeight(45);
        nextButton.setMaxHeight(45);
        nextButton.setOnAction(e -> {
            if (app.getCurrentSelectedEvent() != null) {
                app.navigateTo(ScreenManager.Screen.GRAPHIC_SECTION_SELECTION);
            } else {
                app.showAlert(Alert.AlertType.WARNING, "Auswahl fehlt", "Bitte wählen Sie zuerst ein Event aus!");
            }
        });

        // Lädt den Footer aus der BaseScreen
        HBox footerBar = createStandardFooter();

        bottomBox.getChildren().addAll(nextButton, footerBar);

        // --- 3. ZUSAMMENBAUEN ---
        root.setCenter(topBox);
        root.setBottom(bottomBox); // Nagelt die untere Box absolut fest an den Bildschirmrand!

        return createDefaultScene(root);
    }

    private HBox createHeaderBar() {
        HBox headerBar = createHBox(15, Pos.CENTER_RIGHT);
        headerBar.setPadding(new Insets(10, 15, 10, 15));
        headerBar.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);");

        Label dateTimeLabel = new Label();
        dateTimeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e; -fx-font-size: 13px;");

        DateTimeFormatter clockFormatter = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy | HH:mm:ss", Locale.GERMAN);
        Timeline clockTimer = new Timeline(
                new KeyFrame(Duration.ZERO, e -> dateTimeLabel.setText(LocalDateTime.now().format(clockFormatter))),
                new KeyFrame(Duration.seconds(1)));
        clockTimer.setCycleCount(Animation.INDEFINITE);
        clockTimer.play();

        Region headSpacer = new Region();
        HBox.setHgrow(headSpacer, Priority.ALWAYS);
        headerBar.getChildren().addAll(dateTimeLabel, headSpacer);

        if (app.isLoggedIn()) {
            User loggedInUser = app.getLoggedInUser();
            String firstName = loggedInUser != null ? loggedInUser.getFirstName() : "";
            String lastName = loggedInUser != null ? loggedInUser.getLastName() : "";

            Label welcomeLabel = new Label("Angemeldet als: " + firstName + " " + lastName);
            welcomeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            Button myTicketsButton = new Button("Meine Tickets");
            myTicketsButton.setStyle(
                    "-fx-background-color: #4b9c6de1; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px; -fx-cursor: hand;");
            myTicketsButton.setOnAction(e -> {
                clockTimer.stop();
                app.navigateTo(ScreenManager.Screen.MY_TICKETS);
            });

            Button logoutButton = new Button("Abmelden");
            logoutButton.setStyle(
                    "-fx-background-color: #af645bea; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px; -fx-cursor: hand;");
            logoutButton.setOnAction(e -> {
                clockTimer.stop();
                app.logoutUser();
                app.navigateTo(ScreenManager.Screen.MAIN_MENU);
            });

            headerBar.getChildren().addAll(welcomeLabel, myTicketsButton, logoutButton);
        } else {
            Label guestLabel = new Label("Sie sind als Gast unterwegs.");
            guestLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");

            Button loginButton = new Button("Zum Login");
            loginButton.setStyle(
                    "-fx-background-color: #aee8f0; -fx-text-fill: black; -fx-background-radius: 6px; -fx-cursor: hand; -fx-border-color: #2c3e50; -fx-border-width: 1px; -fx-border-radius: 6px;");
            loginButton.setOnAction(e -> {
                clockTimer.stop();
                app.navigateTo(ScreenManager.Screen.LOGIN);
            });

            headerBar.getChildren().addAll(guestLabel, loginButton);
        }

        return headerBar;
    }

    // Erstellt die Event-Karten für die Eventauswahl. Jede Karte zeigt den Titel,
    // das Datum und die Beschreibung des Events an. Außerdem wird der Typ des
    // Events angezeigt.
    private VBox createEventCard(Event event, DateTimeFormatter formatter, List<VBox> eventCards) {
        VBox card = createVBox(8, Pos.TOP_LEFT);
        card.setPadding(new Insets(10));
        card.setMinWidth(320);
        card.setPrefWidth(320);
        card.setMaxWidth(320);
        card.setMinHeight(150);
        card.setStyle(CARD_DEFAULT_STYLE);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Header-Bereich der Karte mit Titel und Datum
        VBox header = createVBox(2, Pos.TOP_LEFT);
        header.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 10 10 0 0;-fx-padding: 15;");
        header.setPadding(new Insets(10, 5, 10, 5));

        Label eventTitle = new Label(event.getTitle().toUpperCase());
        eventTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #f3f6f8; -fx-font-size: 16px;");
        eventTitle.setWrapText(false);
        eventTitle.setTextOverrun(OverrunStyle.ELLIPSIS);
        eventTitle.setMinHeight(Region.USE_PREF_SIZE);
        eventTitle.setMaxWidth(Double.MAX_VALUE);
        eventTitle.setAlignment(Pos.TOP_LEFT);

        Tooltip fullTitleTooltip = new Tooltip(event.getTitle());
        eventTitle.setTooltip(fullTitleTooltip);

        Label eventDate = new Label(event.getDateTime().format(formatter));
        eventDate.setStyle("-fx-text-fill: #aee8f0; -fx-font-size: 16px;");
        eventDate.setAlignment(Pos.BOTTOM_LEFT);

        header.getChildren().addAll(eventTitle, eventDate);

        // Content-Bereich der Karte mit Beschreibung und Event-Typ
        VBox content = createVBox(10, Pos.TOP_LEFT);
        content.setPadding(new Insets(0, 12, 0, 12));

        String eventDescription;
        if (event.getDescription() != null) {
            eventDescription = event.getDescription().trim();
        } else {
            eventDescription = "";
        }

        Label eventDescriptionLabel = new Label(eventDescription);
        eventDescriptionLabel.setWrapText(true);
        eventDescriptionLabel.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 14px;");

        Label mapTypeLabel = new Label(event.getEventType().toString());
        mapTypeLabel.setStyle(
                "-fx-background-color: #2c3e50;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 3px 8px;" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 4px;");
        mapTypeLabel.setAlignment(Pos.BOTTOM_LEFT);

        content.getChildren().addAll(eventDescriptionLabel);

        // Fügt Header, Content und Spacer zur Karte hinzu
        card.getChildren().addAll(header, content, spacer, mapTypeLabel);

        // Fügt Hover- und Klick-Effekte hinzu, um die Karte interaktiv zu machen
        card.setOnMouseEntered(e -> {
            if (app.getCurrentSelectedEvent() != event) {
                card.setStyle(CARD_HOVER_STYLE);
            }
        });

        card.setOnMouseExited(e -> {
            if (app.getCurrentSelectedEvent() != event) {
                card.setStyle(CARD_DEFAULT_STYLE);
            }
        });

        card.setOnMouseClicked(mouseEvent -> {
            for (VBox otherCard : eventCards) {
                otherCard.setStyle(CARD_DEFAULT_STYLE);
            }

            app.setCurrentSelectedEvent(event);
            card.setStyle(CARD_SELECTED_STYLE);

            if (mouseEvent.getClickCount() == 2) {
                app.navigateTo(ScreenManager.Screen.GRAPHIC_SECTION_SELECTION);
            }
        });

        return card;
    }
}
