package ui.screens;

import domain.SeatedSection;
import domain.StandingSection;
import domain.Ticket;
import domain.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import repository.UserRepository;
import service.BookingService;
import ui.App;
import ui.ScreenManager;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javafx.scene.image.WritableImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javafx.scene.SnapshotParameters;
import javafx.scene.paint.Color;


public class MyTicketsScreen extends BaseScreen {

    private final App app;
    private final BookingService bookingService;
    private final UserRepository userRepo;

    public MyTicketsScreen(App app, BookingService bookingService, UserRepository userRepo) {
        this.app = app;
        this.bookingService = bookingService;
        this.userRepo = userRepo;
    }

    @Override
    public Scene buildScene() {
        VBox root = createRoot(20, new Insets(30), Pos.TOP_CENTER);

        Label title = createTitle("MEINE TICKETS");

        VBox ticketContainer = createVBox(15, Pos.TOP_CENTER);
        ticketContainer.setPadding(new Insets(10));

        ScrollPane scrollPane = createTransparentScrollPane(ticketContainer);

        User loggedInUser = app.getLoggedInUser();
        List<Ticket> myTickets = loggedInUser != null ? loggedInUser.getPurchasedTickets() : null;

        if (myTickets == null || myTickets.isEmpty()) {
            Label noTicketsLabel = createMutedInfoLabel("Sie haben bisher noch keine Tickets gebucht.");
            ticketContainer.getChildren().add(noTicketsLabel);
        } else {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm 'Uhr'");

            for (Ticket ticket : myTickets) {
                HBox ticketCard = createTicketCard(ticket, dtf, loggedInUser);
                ticketContainer.getChildren().add(ticketCard);
            }
        }

        scrollPane.setContent(ticketContainer);

        // --- NEUER BUTTON ---
        Button downloadAllButton = createConfirmButton("Alle Tickets als PNG speichern");
        downloadAllButton.setDisable(myTickets == null || myTickets.isEmpty());
        downloadAllButton.setStyle(
                "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20 10 20; -fx-background-radius: 6px; -fx-cursor: hand;");

        downloadAllButton.setOnAction(e -> saveAllTicketsAsImage(myTickets));

        Button backButton = createBackButton("Zurück zum Hauptmenü");
        backButton.setOnAction(e -> app.navigateTo(ScreenManager.Screen.MAIN_MENU));

        HBox actionButtons = createHBox(10, Pos.CENTER);
        // downloadAllButton statt receiptsButton hinzufügen
        actionButtons.getChildren().addAll(downloadAllButton, backButton);

        root.getChildren().addAll(title, scrollPane, actionButtons);

        return createDefaultScene(root);
    }

    private HBox createTicketCard(Ticket ticket, DateTimeFormatter dtf, User loggedInUser) {
        HBox ticketCard = createHBox(20, Pos.CENTER_LEFT);
        ticketCard.setPadding(new Insets(15));
        ticketCard.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #2ecc71; " +
                        "-fx-border-width: 1px 1px 1px 5px; " +
                        "-fx-border-radius: 4px; " +
                        "-fx-background-radius: 4px; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);");

        VBox details = new VBox(5);

        String eventTitle = ticket.getEvent() != null ? ticket.getEvent().getTitle() : "Event-Ticket";
        String eventDate = ticket.getEvent() != null ? ticket.getEvent().getDateTime().format(dtf) : "";

        Label lblEvent = new Label(eventTitle);
        lblEvent.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");

        Label lblDate = new Label(eventDate);
        lblDate.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

        String eventDescription = (ticket.getEvent() != null && ticket.getEvent().getDescription() != null)
                ? ticket.getEvent().getDescription().trim()
                : "";
        if (eventDescription.isEmpty()) {
            eventDescription = "Keine Eventbeschreibung vorhanden.";
        }
        Label lblDescription = new Label(eventDescription);
        lblDescription.setWrapText(true);
        lblDescription.setMaxWidth(380);
        lblDescription.setStyle("-fx-text-fill: #566573; -fx-font-size: 12px;");

        String seatInfo;
        if (ticket.getSection() instanceof StandingSection) {
            seatInfo = ticket.getSection().getName();
        } else if (ticket.getSection() instanceof SeatedSection) {
            int[] rowAndSeat = parseRowAndSeat(ticket.getSeatInfo());
            if (rowAndSeat[0] > 0 && rowAndSeat[1] > 0) {
                seatInfo = String.format("%s | Reihe %d | Sitz %d", ticket.getSection().getName(), rowAndSeat[0],
                        rowAndSeat[1]);
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

        details.getChildren().addAll(lblEvent, lblDate, lblDescription, lblSeat, lblType);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblPrice = new Label(String.format("%.2f €", ticket.getFinalPrice()));
        lblPrice.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Button btnCancel = createDangerButton("Stornieren");
        btnCancel.setStyle(
                "-fx-background-color: #e74c3c;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-background-radius: 4px;" +
                        "-fx-padding: 6px 12px;" +
                        "-fx-cursor: Hand;");

        btnCancel.setOnAction(e -> cancelTicket(ticket, eventTitle, loggedInUser));

        Button btnDownload = createConfirmButton("Ticket speichern");
        btnDownload.setStyle(
                "-fx-background-color: #2ecc71;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-background-radius: 4px;" +
                        "-fx-padding: 6px 12px;" +
                        "-fx-cursor: Hand;");

        btnDownload.setOnAction(e -> saveTicketAsImage(ticket));

        Button btnOpenEvent = createConfirmButton("Ticket anzeigen");
        btnOpenEvent.setStyle(
                "-fx-background-color: #2c3e50;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-background-radius: 4px;" +
                        "-fx-padding: 6px 12px;" +
                        "-fx-cursor: Hand;");
        btnOpenEvent.setOnAction(e -> app.openTicketWindow(ticket));

        VBox actionBox = new VBox(8);
        actionBox.setAlignment(Pos.CENTER_RIGHT);
        actionBox.getChildren().addAll(btnOpenEvent, btnDownload, btnCancel);

        ticketCard.getChildren().addAll(details, spacer, lblPrice, actionBox);
        return ticketCard;
    }

    private void cancelTicket(Ticket ticket, String eventTitle, User loggedInUser) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        if (app.getPrimaryStage() != null) {
            confirmAlert.initOwner(app.getPrimaryStage());
        }

        confirmAlert.setTitle("Ticket stornieren");
        confirmAlert.setHeaderText("Möchten Sie dieses Ticket wirklich stornieren?");

        String placeText = (ticket.getSection() instanceof StandingSection)
                ? "Bereich: " + ticket.getSection().getName()
                : "Platz: " + ticket.getSeatInfo();

        confirmAlert.setContentText(
                "Event: " + eventTitle + "\n" +
                        placeText + "\n" +
                        "Preis: " + String.format("%.2f €", ticket.getFinalPrice()));

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = bookingService.cancelTicket(ticket, loggedInUser);
            if (success) {
                userRepo.saveUsersToFile();
                app.showAlert(Alert.AlertType.INFORMATION, "Storniert", "Das Ticket wurde erfolgreich storniert.");
                app.navigateTo(ScreenManager.Screen.MY_TICKETS);
            } else {
                app.showAlert(Alert.AlertType.ERROR, "Fehler", "Das Ticket konnte leider nicht storniert werden.");
            }
        }
    }

    private int[] parseRowAndSeat(String seatInfoStr) {
        int[] result = new int[] { 0, 0 };
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
            return result;
        }
        return result;
    }

    private void saveTicketAsImage(Ticket ticket) {
        // 1. Ein sauberes Layout nur für den Export erstellen (ohne Buttons)
        VBox exportLayout = new VBox(10);
        exportLayout.setPadding(new Insets(20));
        exportLayout.setStyle(
                "-fx-background-color: white; -fx-border-color: #2c3e50; -fx-border-width: 3px; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        exportLayout.setAlignment(Pos.CENTER_LEFT);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm 'Uhr'");
        String eventTitle = ticket.getEvent() != null ? ticket.getEvent().getTitle() : "Event";
        String eventDate = ticket.getEvent() != null ? ticket.getEvent().getDateTime().format(dtf) : "";

        Label headerLbl = new Label("E-TICKET");
        headerLbl.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label eventLbl = new Label(eventTitle);
        eventLbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        Label dateLbl = new Label("Datum: " + eventDate);
        dateLbl.setStyle("-fx-font-size: 16px; -fx-text-fill: #34495e;");

        String seatInfo = ticket.getSeatInfo() != null ? ticket.getSeatInfo() : "Keine Platzinfo";
        Label seatLbl = new Label("Bereich: " + ticket.getSection().getName() + " | " + seatInfo);
        seatLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        String customerName = ticket.getCustomer() != null ? ticket.getCustomer().getFullName() : "Unbekannt";
        Label customerLbl = new Label("Käufer: " + customerName + " (" + ticket.getCustomerType() + ")");
        customerLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        Label idLbl = new Label("Ticket-ID: " + ticket.getTicketId());
        idLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #bdc3c7;");

        exportLayout.getChildren().addAll(headerLbl, eventLbl, dateLbl, seatLbl, customerLbl, idLbl);

        // Szene kurz generieren, damit JavaFX das CSS und Layout für den Screenshot
        // berechnet
        new Scene(exportLayout);

        // 2. Screenshot (Snapshot) von diesem Layout machen
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage image = exportLayout.snapshot(params, null);

        // 3. FileChooser öffnen, damit der Benutzer den Speicherort wählen kann
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ticket speichern unter...");
        fileChooser.setInitialFileName(ticket.getTicketId() + ".png"); // Bsp: T-1001.png
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Bild", "*.png"));

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                // Bild auf die Festplatte schreiben
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                app.showAlert(Alert.AlertType.INFORMATION, "Erfolg", "Das Ticket wurde erfolgreich gespeichert!");
            } catch (IOException ex) {
                app.showAlert(Alert.AlertType.ERROR, "Fehler",
                        "Das Ticket konnte nicht gespeichert werden: " + ex.getMessage());
            }
        }
    }

    private void saveAllTicketsAsImage(List<Ticket> tickets) {
        if (tickets == null || tickets.isEmpty()) {
            app.showAlert(Alert.AlertType.WARNING, "Keine Tickets", "Es gibt keine Tickets zum Speichern.");
            return;
        }

        // 1. Ein Layout erstellen, das alle Tickets aufnimmt
        VBox exportLayout = new VBox(20); // Abstand zwischen den Tickets
        exportLayout.setPadding(new Insets(30));
        exportLayout.setStyle("-fx-background-color: white;");
        exportLayout.setAlignment(Pos.TOP_CENTER);

        // Eine Überschrift für das Dokument
        Label mainHeader = new Label("MEINE TICKETS");
        mainHeader
                .setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 0 0 20 0;");
        exportLayout.getChildren().add(mainHeader);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm 'Uhr'");

        // 2. Für jedes Ticket einen visuell abgetrennten Bereich erstellen
        for (Ticket ticket : tickets) {
            VBox ticketBox = new VBox(10);
            ticketBox.setPadding(new Insets(20));
            ticketBox.setStyle(
                    "-fx-border-color: #3498db; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-color: #f8f9fa; -fx-background-radius: 8px; -fx-pref-width: 600px;");
            ticketBox.setAlignment(Pos.CENTER_LEFT);

            String eventTitle = ticket.getEvent() != null ? ticket.getEvent().getTitle() : "Event";
            String eventDate = ticket.getEvent() != null ? ticket.getEvent().getDateTime().format(dtf) : "";

            Label eventLbl = new Label(eventTitle);
            eventLbl.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

            Label dateLbl = new Label("Datum: " + eventDate);
            dateLbl.setStyle("-fx-font-size: 16px; -fx-text-fill: #34495e;");

            String seatInfo = ticket.getSeatInfo() != null ? ticket.getSeatInfo() : "Keine Platzinfo";
            Label seatLbl = new Label("Bereich: " + ticket.getSection().getName() + " | " + seatInfo);
            seatLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

            String customerName = ticket.getCustomer() != null ? ticket.getCustomer().getFullName() : "Unbekannt";
            Label customerLbl = new Label("Käufer: " + customerName + " (" + ticket.getCustomerType() + ")");
            customerLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

            Label idLbl = new Label("Ticket-ID: " + ticket.getTicketId() + "  |  Preis: "
                    + String.format("%.2f EUR", ticket.getFinalPrice()));
            idLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");

            ticketBox.getChildren().addAll(eventLbl, dateLbl, seatLbl, customerLbl, idLbl);
            exportLayout.getChildren().add(ticketBox);
        }

        // Szene kurz generieren, damit JavaFX das CSS und Layout für den Screenshot
        // berechnet
        new Scene(exportLayout);

        // 3. Screenshot (Snapshot) von diesem Gesamtlayout machen
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.WHITE); // Weißer Hintergrund, falls das Bild Ränder hat
        WritableImage image = exportLayout.snapshot(params, null);

        // 4. FileChooser öffnen
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Alle Tickets speichern unter...");

        // Einen sinnvollen Standardnamen vorschlagen
        String userName = app.getLoggedInUser() != null ? app.getLoggedInUser().getLastName() : "Kunde";
        fileChooser.setInitialFileName("Tickets_" + userName + ".png");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Bild", "*.png"));

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                // Bild auf die Festplatte schreiben
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                app.showAlert(Alert.AlertType.INFORMATION, "Erfolg",
                        "Alle Tickets wurden erfolgreich in einer Datei gespeichert!");
            } catch (IOException ex) {
                app.showAlert(Alert.AlertType.ERROR, "Fehler",
                        "Die Tickets konnten nicht gespeichert werden: " + ex.getMessage());
            }
        }
    }
}
