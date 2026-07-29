package ui.screens;

import domain.Event;
import domain.Event.EventType;
import domain.Event.MapType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import repository.EventRepository;
import ui.App;
import ui.ScreenManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmployeeEventScreen extends BaseScreen {

    private final App app;
    private final EventRepository eventRepo;
    // Format für das Zusammenfügen von Datum und Uhrzeit
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public EmployeeEventScreen(App app, EventRepository eventRepo) {
        this.app = app;
        this.eventRepo = eventRepo;
    }

    @Override
    public Scene buildScene() {
        VBox root = createRoot(20, new Insets(30), Pos.TOP_CENTER);
        Label title = createTitle("MITARBEITER PORTAL - EVENT VERWALTUNG");

        HBox mainContent = new HBox(30);
        mainContent.setAlignment(Pos.TOP_CENTER);

        // ==========================================
        // LINKE SEITE: FORMULAR FÜR NEUES EVENT
        // ==========================================
        VBox formBox = createVBox(10, Pos.TOP_LEFT);
        formBox.setPrefWidth(350);
        formBox.setStyle(
                "-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 8; -fx-border-color: #bdc3c7; -fx-border-radius: 8;");

        Label formTitle = new Label("Neues Event anlegen");
        formTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField titleField = new TextField();
        titleField.setPromptText("Event Titel");

        // TEXTAREA MIT 160 ZEICHEN LIMIT
        TextArea descArea = new TextArea();
        descArea.setPromptText("Beschreibung (max. 160 Zeichen)");
        descArea.setPrefRowCount(3);
        descArea.setWrapText(true);
        // Formatter verhindert das Eintippen von mehr als 160 Zeichen
        descArea.setTextFormatter(
                new TextFormatter<String>(change -> change.getControlNewText().length() <= 160 ? change : null));

        Label descHint = new Label("Maximal 160 Zeichen erlaubt.");
        descHint.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");

        ComboBox<EventType> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(EventType.values());
        typeBox.setPromptText("Event-Typ wählen");
        typeBox.setPrefWidth(Double.MAX_VALUE);

        ComboBox<MapType> mapBox = new ComboBox<>();
        mapBox.getItems().addAll(MapType.values());
        mapBox.setPromptText("Saalplan wählen");
        mapBox.setPrefWidth(Double.MAX_VALUE);

        // GETRENNTE FELDER FÜR DATUM UND UHRZEIT
        HBox dateTimeBox = new HBox(10);
        TextField dateField = new TextField();
        dateField.setPromptText("Datum (z.B. 24.12.2026)");
        dateField.setPrefWidth(150);

        TextField timeField = new TextField();
        timeField.setPromptText("Uhrzeit (z.B. 20:00)");
        timeField.setPrefWidth(150);
        dateTimeBox.getChildren().addAll(dateField, timeField);

        TextField priceField = new TextField();
        priceField.setPromptText("Basispreis (z.B. 50.0)");

        Button saveBtn = createConfirmButton("Event Speichern");
        saveBtn.setPrefWidth(Double.MAX_VALUE);
        saveBtn.setStyle(
                "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        saveBtn.setOnAction(e -> {
            try {
                String eventTitle = titleField.getText().trim();
                String desc = descArea.getText().trim();
                EventType eType = typeBox.getValue();
                MapType mType = mapBox.getValue();

                double price = Double.parseDouble(priceField.getText().trim().replace(",", "."));

                // Datum und Uhrzeit zusammensetzen und parsen
                String combinedDateTime = dateField.getText().trim() + " " + timeField.getText().trim();
                LocalDateTime dateTime = LocalDateTime.parse(combinedDateTime, formatter);

                if (eventTitle.isEmpty() || eType == null || mType == null) {
                    app.showAlert(Alert.AlertType.WARNING, "Fehler", "Bitte alle Pflichtfelder ausfüllen.");
                    return;
                }

                Long newId = eventRepo.nextEventId();
                Event newEvent = new Event(newId, eventTitle, desc, eType, dateTime, price, mType);
                eventRepo.save(newEvent);

                app.showAlert(Alert.AlertType.INFORMATION, "Erfolg", "Event erfolgreich hinzugefügt!");
                app.navigateTo(ScreenManager.Screen.EMPLOYEE_EVENTS);

            } catch (Exception ex) {
                app.showAlert(Alert.AlertType.ERROR, "Eingabefehler",
                        "Bitte Eingaben prüfen.\nDatum muss sein: dd.MM.yyyy\nUhrzeit muss sein: HH:mm\nPreis muss eine Zahl sein.");
            }
        });

        formBox.getChildren().addAll(formTitle, titleField, descArea, descHint, typeBox, mapBox, dateTimeBox,
                priceField, saveBtn);

        // ==========================================
        // RECHTE SEITE: LISTE ZUM LÖSCHEN
        // ==========================================
        VBox listBox = createVBox(10, Pos.TOP_LEFT);
        listBox.setPrefWidth(420);

        Label listTitle = new Label("Bestehende Events");
        listTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox eventsContainer = new VBox(10);
        List<Event> allEvents = eventRepo.getAllEvents();

        for (Event ev : allEvents) {
            HBox eventRow = new HBox(10);
            eventRow.setAlignment(Pos.CENTER_LEFT);
            eventRow.setStyle(
                    "-fx-background-color: white; -fx-padding: 10; -fx-border-color: #bdc3c7; -fx-border-radius: 4; -fx-background-radius: 4;");

            VBox infoBox = new VBox(3);
            Label eTitle = new Label(ev.getTitle());
            eTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            Label eDate = new Label(ev.getDateTime().format(formatter) + " | ID: " + ev.getId());
            eDate.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");
            infoBox.getChildren().addAll(eTitle, eDate);

            Region spacer = createHorizontalSpacer();

            Button delBtn = createDangerButton("Löschen");
            delBtn.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Löschen bestätigen");
                confirm.setHeaderText("Event wirklich löschen?");
                confirm.setContentText("Soll das Event '" + ev.getTitle() + "' unwiderruflich gelöscht werden?");

                confirm.showAndWait().ifPresent(res -> {
                    if (res == ButtonType.OK) {
                        eventRepo.deleteEvent(ev.getId());
                        app.navigateTo(ScreenManager.Screen.EMPLOYEE_EVENTS);
                    }
                });
            });

            eventRow.getChildren().addAll(infoBox, spacer, delBtn);
            eventsContainer.getChildren().add(eventRow);
        }

        ScrollPane scroll = createTransparentScrollPane(eventsContainer);
        scroll.setPrefHeight(450);

        listBox.getChildren().addAll(listTitle, scroll);
        mainContent.getChildren().addAll(formBox, listBox);

        Button backBtn = createBackButton("Abmelden / Zurück zum Login");
        backBtn.setOnAction(e -> app.navigateTo(ScreenManager.Screen.LOGIN));

        root.getChildren().addAll(title, mainContent, backBtn);

        return createDefaultScene(root);
    }
}