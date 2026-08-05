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

/**
 * Stellt Mitarbeitenden eine gemeinsame Oberfläche zum Anlegen, Bearbeiten und
 * Löschen von Veranstaltungen bereit.
 */
public class EmployeeEventScreen extends BaseScreen {

    /** Anwendungskontext für Hinweise und Navigation. */
    private final App app;

    /** Datenzugriff für die angezeigten und bearbeiteten Events. */
    private final EventRepository eventRepo;

    /** Einheitliches Eingabe- und Anzeigeformat für Eventzeitpunkte. */
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    /** ID des aktuell bearbeiteten Events oder {@code null} im Anlegemodus. */
    private Long editingEventId = null;

    /**
     * Erstellt die Eventverwaltung für Mitarbeitende.
     *
     * @param app zentraler Anwendungskontext
     * @param eventRepo Repository für Eventdaten
     */
    public EmployeeEventScreen(App app, EventRepository eventRepo) {
        this.app = app;
        this.eventRepo = eventRepo;
    }

    /**
     * Baut das Eingabeformular und die Liste vorhandener Events mit Bearbeitungs-
     * und Löschaktionen auf.
     *
     * @return vollständige Szene der Eventverwaltung
     */
    @Override
    public Scene buildScene() {
        javafx.scene.layout.BorderPane root = new javafx.scene.layout.BorderPane();
        root.setStyle("-fx-background-color: #f5f5f7;");

        VBox headerBox = createHeaderBox("MITARBEITER PORTAL", "Event Verwaltung");

        HBox mainContent = new HBox(30);
        mainContent.setAlignment(Pos.TOP_CENTER);

        // Linke Spalte: gemeinsames Formular für neue und bestehende Events.
        VBox formBox = createVBox(10, Pos.TOP_LEFT);
        formBox.setPrefWidth(350);
        formBox.setStyle(
                "-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 8; -fx-border-color: #bdc3c7; -fx-border-radius: 8;");

        Label formTitle = new Label("Neues Event anlegen");
        formTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField titleField = new TextField();
        titleField.setPromptText("Event Titel");

        TextArea descArea = new TextArea();
        descArea.setPromptText("Beschreibung (max. 600 Zeichen)");
        descArea.setPrefRowCount(3);
        descArea.setWrapText(true);
        descArea.setTextFormatter(
                new TextFormatter<String>(change -> change.getControlNewText().length() <= 600 ? change : null));

        Label descHint = new Label("Maximal 600 Zeichen erlaubt.");
        descHint.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");

        ComboBox<EventType> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(EventType.values());
        typeBox.setPromptText("Event-Typ wählen");
        typeBox.setPrefWidth(Double.MAX_VALUE);

        ComboBox<MapType> mapBox = new ComboBox<>();
        mapBox.getItems().addAll(MapType.values());
        mapBox.setPromptText("Saalplan wählen");
        mapBox.setPrefWidth(Double.MAX_VALUE);

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

        // Die Abbruchaktion ist nur während der Bearbeitung eines Events sichtbar.
        Button cancelBtn = createSecondaryButton("Bearbeiten abbrechen");
        cancelBtn.setPrefWidth(Double.MAX_VALUE);
        cancelBtn.setVisible(false);
        cancelBtn.setManaged(false);

        cancelBtn.setOnAction(e -> {
            editingEventId = null;
            app.navigateTo(ScreenManager.Screen.EMPLOYEE_EVENTS); // Neuaufbau leert das Formular.
        });

        saveBtn.setOnAction(e -> {
            try {
                String eventTitle = titleField.getText().trim();
                String desc = descArea.getText().trim();
                EventType eType = typeBox.getValue();
                MapType mType = mapBox.getValue();
                double price = Double.parseDouble(priceField.getText().trim().replace(",", "."));
                String combinedDateTime = dateField.getText().trim() + " " + timeField.getText().trim();
                LocalDateTime dateTime = LocalDateTime.parse(combinedDateTime, formatter);

                if (dateTime.isBefore(LocalDateTime.now())) {
                    app.showAlert(Alert.AlertType.WARNING, "Ungültiges Datum",
                            "Das Event darf nicht in der Vergangenheit liegen.");
                    return;
                }

                if (eventTitle.isEmpty() || eType == null || mType == null) {
                    app.showAlert(Alert.AlertType.WARNING, "Fehler", "Bitte alle Pflichtfelder ausfüllen.");
                    return;
                }

                if (editingEventId == null) {
                    // Ohne Bearbeitungs-ID wird ein neues Event angelegt.
                    Long newId = eventRepo.nextEventId();
                    Event newEvent = new Event(newId, eventTitle, desc, eType, dateTime, price, mType);
                    eventRepo.save(newEvent);
                    app.showAlert(Alert.AlertType.INFORMATION, "Erfolg", "Event erfolgreich hinzugefügt!");
                } else {
                    // Mit Bearbeitungs-ID wird der bestehende Datensatz ersetzt.
                    Event updatedEvent = new Event(editingEventId, eventTitle, desc, eType, dateTime, price, mType);
                    eventRepo.updateEvent(updatedEvent);
                    app.showAlert(Alert.AlertType.INFORMATION, "Erfolg", "Event erfolgreich aktualisiert!");
                    editingEventId = null;
                }

                app.navigateTo(ScreenManager.Screen.EMPLOYEE_EVENTS);
            } catch (Exception ex) {
                app.showAlert(Alert.AlertType.ERROR, "Eingabefehler",
                        "Bitte Eingaben prüfen.\nDatum muss sein: dd.MM.yyyy\nUhrzeit muss sein: HH:mm\nPreis muss eine Zahl sein.");
            }
        });

        formBox.getChildren().addAll(formTitle, titleField, descArea, descHint, typeBox, mapBox, dateTimeBox,
                priceField, saveBtn, cancelBtn);

        // Rechte Spalte: vorhandene Events mit Bearbeitungs- und Löschaktionen.
        VBox listBox = createVBox(10, Pos.TOP_LEFT);
        listBox.setPrefWidth(490);

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

            // Die Bearbeitungsaktion übernimmt die Eventdaten in das linke Formular.
            Button editBtn = createSelectingButton("Bearbeiten");
            editBtn.setStyle(
                    "-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 12 6 12; -fx-background-radius: 4; -fx-cursor: hand;");
            editBtn.setOnAction(e -> {
                editingEventId = ev.getId();
                formTitle.setText("Event bearbeiten (ID: " + ev.getId() + ")");
                titleField.setText(ev.getTitle());
                String desc = ev.getDescription();
                if (desc.length() > 600) {
                    desc = desc.substring(0, 600); // Entspricht der Eingabegrenze des Formulars.
                }
                descArea.setText(desc);
                typeBox.setValue(ev.getEventType());
                mapBox.setValue(ev.getMapType());
                dateField.setText(ev.getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                timeField.setText(ev.getDateTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                priceField.setText(String.valueOf(ev.getBasePrice()));

                saveBtn.setText("Änderungen speichern");
                cancelBtn.setVisible(true);
                cancelBtn.setManaged(true);
            });

            Button delBtn = createDangerButton("Löschen");
            delBtn.setStyle(
                    "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 12 6 12; -fx-background-radius: 4; -fx-cursor: hand;");
            delBtn.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                if (app.getPrimaryStage() != null) {
                    confirm.initOwner(app.getPrimaryStage());
                }
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

            eventRow.getChildren().addAll(infoBox, spacer, editBtn, delBtn);
            eventsContainer.getChildren().add(eventRow);
        }

        ScrollPane scroll = createTransparentScrollPane(eventsContainer);
        scroll.setPrefHeight(450);

        listBox.getChildren().addAll(listTitle, scroll);
        mainContent.getChildren().addAll(formBox, listBox);

        // Feste Navigation zum Verlassen des Mitarbeiterportals.
        Button backBtn = createBackButton("Abmelden / Zurück zum Login");
        backBtn.setPrefWidth(300);
        backBtn.setMinHeight(45);
        backBtn.setMaxHeight(45);
        backBtn.setOnAction(e -> app.navigateTo(ScreenManager.Screen.LOGIN));

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(backBtn);

        // Unsichtbare Standardbereiche halten die Eventverwaltung mittig ausgerichtet.
        HBox dummyHeader = createInvisibleHeader();
        VBox topBox = createRoot(20, new Insets(30, 30, 20, 30), Pos.TOP_CENTER);
        topBox.getChildren().addAll(dummyHeader, headerBox, mainContent);

        HBox dummyFooter = createInvisibleStandardFooter();
        VBox bottomBox = createRoot(10, new Insets(0, 30, 30, 30), Pos.BOTTOM_CENTER);
        bottomBox.getChildren().addAll(buttonBox, dummyFooter);

        root.setCenter(topBox);
        root.setBottom(bottomBox);

        return createDefaultScene(root);
    }
}
