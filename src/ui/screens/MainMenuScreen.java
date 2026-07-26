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
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import repository.EventRepository;
import ui.App;
import ui.ScreenManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainMenuScreen extends BaseScreen {

    private static final String CARD_DEFAULT_STYLE =
        "-fx-background-color: white;" +
        "-fx-border-color: #bdc3c7;" +
        "-fx-border-width: 1px;" +
        "-fx-border-radius: 8px;" +
        "-fx-background-radius: 8px;" +
        "-fx-cursor: Hand;" +
        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 3);";

    private static final String CARD_HOVER_STYLE =
        "-fx-background-color: #fdfdfd;" +
        "-fx-border-color: #2c3e50;" +
        "-fx-border-width: 1px;" +
        "-fx-border-radius: 8px;" +
        "-fx-background-radius: 8px;" +
        "-fx-cursor: hand;" +
        "-fx-effect: dropshadow(three-pass-box, rgba(41,128,185,0.2), 8, 0, 0, 4);";

    private static final String CARD_SELECTED_STYLE =
        "-fx-background-color: #ebf5fb;" +
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
        EventRepository eventRepo
    ) {
        this.app = app;
        this.eventRepo = eventRepo;
    }

    @Override
    public Scene buildScene() {
        VBox root = createRoot(20, new Insets(30), Pos.TOP_CENTER);

        HBox headerBar = createHeaderBar();

        Label title = createTitle("ARENA TICKETSYSTEM");
        Label subtitle = createSubtitle("Waehlen Sie ein Event aus:");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        FlowPane cardContainer = new FlowPane();
        cardContainer.setHgap(20);
        cardContainer.setVgap(20);
        cardContainer.setPadding(new Insets(10));
        cardContainer.setAlignment(Pos.CENTER);

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

        Button nextButton = createPrimaryButton("Bloecke anzeigen");
        nextButton.setPrefWidth(200);
        nextButton.setOnAction(e -> {
            if (app.getCurrentSelectedEvent() != null) {
                app.navigateTo(ScreenManager.Screen.GRAPHIC_SECTION_SELECTION);
            } else {
                app.showAlert(Alert.AlertType.WARNING, "Auswahl fehlt", "Bitte waehlen Sie zuerst ein Event aus!");
            }
        });

        Label teamLabel = new Label("Entwickelt von: Lukas Beck, Maren Bohlig, Gian-Luca Levels, Hayat van Eck");
        teamLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-style: italic;");

        HBox footerBar = createHBox(0, Pos.BOTTOM_RIGHT);
        footerBar.getChildren().add(teamLabel);
        footerBar.setPadding(new Insets(10, 0, 0, 0));

        root.getChildren().addAll(headerBar, title, subtitle, scrollPane, nextButton, createVerticalSpacer(), footerBar);
        return createDefaultScene(root);
    }

    private HBox createHeaderBar() {
        HBox headerBar = createHBox(15, Pos.CENTER_RIGHT);
        headerBar.setPadding(new Insets(10, 15, 10, 15));
        headerBar.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 8px;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);"
        );

        Label dateTimeLabel = new Label();
        dateTimeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e; -fx-font-size: 13px;");

        DateTimeFormatter clockFormatter = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy | HH:mm:ss", Locale.GERMAN);
        Timeline clockTimer = new Timeline(
            new KeyFrame(Duration.ZERO, e -> dateTimeLabel.setText(LocalDateTime.now().format(clockFormatter))),
            new KeyFrame(Duration.seconds(1))
        );
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
            myTicketsButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px; -fx-cursor: hand;");
            myTicketsButton.setOnAction(e -> {
                clockTimer.stop();
                app.navigateTo(ScreenManager.Screen.MY_TICKETS);
            });

            Button logoutButton = new Button("Abmelden");
            logoutButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 4px; -fx-cursor: hand;");
            logoutButton.setOnAction(e -> {
                clockTimer.stop();
                app.logoutUser();
                app.navigateTo(ScreenManager.Screen.MAIN_MENU);
            });

            headerBar.getChildren().addAll(welcomeLabel, myTicketsButton, logoutButton);
        } else {
            Label guestLabel = new Label("Sie sind als Gast unterwegs.");
            guestLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");

            Button loginButton = new Button("Anmelden / Registrieren");
            loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4px; -fx-cursor: hand;");
            loginButton.setOnAction(e -> {
                clockTimer.stop();
                app.navigateTo(ScreenManager.Screen.LOGIN);
            });

            headerBar.getChildren().addAll(guestLabel, loginButton);
        }

        return headerBar;
    }

    private VBox createEventCard(Event event, DateTimeFormatter formatter, List<VBox> eventCards) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setMinWidth(220);
        card.setPrefWidth(220);
        card.setMaxWidth(220);
        card.setMinHeight(160);
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle(CARD_DEFAULT_STYLE);

        Label eventTitle = new Label(event.getTitle());
        eventTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");
        eventTitle.setWrapText(true);
        eventTitle.setMinHeight(Region.USE_PREF_SIZE);
        eventTitle.setMaxHeight(Double.MAX_VALUE);
        eventTitle.setAlignment(Pos.TOP_LEFT);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label eventDate = new Label(event.getDateTime().format(formatter));
        eventDate.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

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
