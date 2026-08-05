package ui.screens;

import domain.Receipt;
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
import javafx.scene.control.DialogPane;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.scene.image.WritableImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import javafx.scene.SnapshotParameters;
import javafx.scene.paint.Color;

/**
 * Zeigt die Bestellungen des angemeldeten Kunden gruppiert nach Quittungen an
 * und ermöglicht Ticketstornierungen sowie PNG-Exporte.
 */
public class MyTicketsScreen extends BaseScreen {

    /** Anwendungskontext für Kundendaten, Dialoge und Navigation. */
    private final App app;

    /** Service zur fachlich korrekten Stornierung von Tickets. */
    private final BookingService bookingService;

    /** Repository zum dauerhaften Speichern veränderter Benutzertickets. */
    private final UserRepository userRepo;

    /**
     * Erstellt die persönliche Ticket- und Bestellübersicht.
     *
     * @param app zentraler Anwendungskontext
     * @param bookingService Service für Ticketstornierungen
     * @param userRepo Repository für Benutzerdaten
     */
    public MyTicketsScreen(App app, BookingService bookingService, UserRepository userRepo) {
        this.app = app;
        this.bookingService = bookingService;
        this.userRepo = userRepo;
    }

    /**
     * Baut die nach Quittung gruppierte Bestellübersicht mit Export- und
     * Stornierungsaktionen auf.
     *
     * @return vollständige Szene der eigenen Tickets
     */
    @Override
    public Scene buildScene() {
        VBox headerBox = createHeaderBox("MEINE BESTELLUNGEN & TICKETS",
                "Ihre gebuchten Tickets und Quittungen im Überblick:");

        VBox mainContainer = createVBox(25, Pos.TOP_CENTER);
        mainContainer.setPadding(new Insets(10, 20, 20, 20));
        ScrollPane scrollPane = createTransparentScrollPane(mainContainer);

        User loggedInUser = app.getLoggedInUser();
        List<Ticket> myActiveTickets = loggedInUser != null ? loggedInUser.getPurchasedTickets() : new ArrayList<>();

        // Quittungen bilden die fachlichen Gruppen der Bestellübersicht.
        List<Receipt> myReceipts = app.getReceiptsForLoggedInUser();

        // Neueste Bestellungen werden zuerst angezeigt.
        myReceipts.sort((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()));

        if (myReceipts.isEmpty() && myActiveTickets.isEmpty()) {
            Label noTicketsLabel = createMutedInfoLabel("Sie haben bisher noch keine Tickets gebucht.");
            mainContainer.getChildren().add(noTicketsLabel);
        } else {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm 'Uhr'");

            // Für jede Quittung wird eine eigenständige Bestellkarte aufgebaut.
            for (Receipt receipt : myReceipts) {

                // Nur noch aktive Tickets der jeweiligen Quittung werden zugeordnet.
                List<Ticket> ticketsForThisOrder = new ArrayList<>();
                for (Ticket t : myActiveTickets) {
                    if (receipt.getTicketIds().contains(t.getTicketId())) {
                        ticketsForThisOrder.add(t);
                    }
                }

                // Vollständig stornierte Bestellungen enthalten keine anzeigbaren Tickets.
                if (ticketsForThisOrder.isEmpty()) {
                    continue;
                }

                // Visuelle Gruppierung einer Bestellung.
                VBox orderCard = new VBox(15);
                orderCard.setStyle(
                        "-fx-background-color: #ffffff; " +
                                "-fx-border-color: #bdc3c7; " +
                                "-fx-border-width: 2px; " +
                                "-fx-border-radius: 8px; " +
                                "-fx-background-radius: 8px; " +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);");
                orderCard.setPadding(new Insets(0, 0, 15, 0));

                // Kopfbereich mit Quittungsnummer, Datum und Gesamtbetrag.
                HBox orderHeader = new HBox(15);
                orderHeader.setAlignment(Pos.CENTER_LEFT);
                orderHeader.setPadding(new Insets(15));
                orderHeader.setStyle(
                        "-fx-background-color: #ecf0f1; -fx-background-radius: 6px 6px 0 0; -fx-border-color: #bdc3c7; -fx-border-width: 0 0 1px 0;");

                VBox headerInfo = new VBox(3);
                Label lblOrderTitle = new Label("Bestellung vom " + receipt.getCreatedAt().format(dtf));
                lblOrderTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #2c3e50;");
                Label lblOrderId = new Label("Quittungs-Nr: " + receipt.getReceiptId() + " | Gesamtbetrag: "
                        + String.format("%.2f EUR", receipt.getTotalAmount()));
                lblOrderId.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
                headerInfo.getChildren().addAll(lblOrderTitle, lblOrderId);

                Region headerSpacer = new Region();
                HBox.setHgrow(headerSpacer, Priority.ALWAYS);

                // Exportaktionen gelten jeweils für die gesamte Bestellung.
                Button btnDownloadReceipt = createConfirmButton("Quittung als PNG");
                btnDownloadReceipt.setStyle(
                        "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand;");
                btnDownloadReceipt.setOnAction(e -> saveReceiptAsImage(receipt));

                Button btnDownloadTickets = createConfirmButton("Alle Tickets dieser Bestellung (PNG)");
                btnDownloadTickets.setStyle(
                        "-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand;");
                btnDownloadTickets.setOnAction(e -> saveTicketGroupAsImage(receipt, ticketsForThisOrder));

                orderHeader.getChildren().addAll(headerInfo, headerSpacer, btnDownloadReceipt, btnDownloadTickets);
                orderCard.getChildren().add(orderHeader);

                // Einzelkarten der aktiven Tickets.
                VBox ticketsContainer = new VBox(10);
                ticketsContainer.setPadding(new Insets(0, 15, 0, 15));
                for (Ticket ticket : ticketsForThisOrder) {
                    ticketsContainer.getChildren().add(createTicketCard(ticket, dtf, loggedInUser));
                }

                orderCard.getChildren().add(ticketsContainer);
                mainContainer.getChildren().add(orderCard);
            }
        }

        Button backButton = createBackButton("Zurück zum Hauptmenü");
        backButton.setPrefWidth(300);
        backButton.setMinHeight(45);
        backButton.setMaxHeight(45);
        backButton.setOnAction(e -> app.navigateTo(ScreenManager.Screen.MAIN_MENU));

        HBox actionButtons = createHBox(20, Pos.CENTER);
        actionButtons.getChildren().addAll(backButton);

        // Scrollbarer Inhalt und feste Navigation werden getrennt angeordnet.
        javafx.scene.layout.BorderPane root = new javafx.scene.layout.BorderPane();
        root.setStyle("-fx-background-color: #f5f5f7;");

        VBox topBox = createRoot(20, new Insets(30, 30, 20, 30), Pos.TOP_CENTER);
        topBox.getChildren().addAll(headerBox, scrollPane);

        HBox dummyFooter = createInvisibleStandardFooter();
        VBox bottomBox = createRoot(10, new Insets(0, 30, 30, 30), Pos.BOTTOM_CENTER);
        bottomBox.getChildren().addAll(actionButtons, dummyFooter);

        root.setCenter(topBox);
        root.setBottom(bottomBox);

        return createDefaultScene(root);
    }

    /**
     * Erstellt die visuelle Einzelkarte eines Tickets innerhalb einer Bestellung.
     *
     * @param ticket darzustellendes Ticket
     * @param dtf Formatierung des Eventzeitpunkts
     * @param loggedInUser Besitzer des Tickets
     * @return formatierte Ticketkarte
     */
    private HBox createTicketCard(Ticket ticket, DateTimeFormatter dtf, User loggedInUser) {
        HBox ticketCard = createHBox(20, Pos.CENTER_LEFT);
        ticketCard.setPadding(new Insets(15));
        ticketCard.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #ecf0f1; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 4px; " +
                        "-fx-background-radius: 4px;");

        VBox details = new VBox(5);
        String eventTitle = ticket.getEvent() != null ? ticket.getEvent().getTitle() : "Event-Ticket";

        Label lblEvent = new Label(eventTitle);
        lblEvent.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");

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

        String customerType = (ticket.getCustomerType() != null) ? ticket.getCustomerType() : "Standard";
        Label lblType = new Label("Typ: " + customerType + " | ID: " + ticket.getTicketId());
        lblType.setStyle("-fx-font-style: italic; -fx-text-fill: #7f8c8d; -fx-font-size: 11px;");

        details.getChildren().addAll(lblEvent, lblSeat, lblType);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblPrice = new Label(String.format("%.2f EUR", ticket.getFinalPrice()));
        lblPrice.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Button btnCancel = createDangerButton("Stornieren");
        btnCancel.setStyle(
                "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 4px; -fx-padding: 6px 12px; -fx-cursor: Hand;");
        btnCancel.setOnAction(e -> cancelTicket(ticket, eventTitle, loggedInUser));

        VBox actionBox = new VBox(8);
        actionBox.setAlignment(Pos.CENTER_RIGHT);
        actionBox.getChildren().add(btnCancel);

        ticketCard.getChildren().addAll(details, spacer, lblPrice, actionBox);
        return ticketCard;
    }

    /**
     * Erstellt aus den Quittungsdaten eine druckbare Ansicht inklusive
     * Steueraufteilung und öffnet den PNG-Speicherdialog.
     *
     * @param receipt zu exportierende Quittung
     */
    private void saveReceiptAsImage(Receipt receipt) {
        VBox exportLayout = new VBox(10);
        exportLayout.setPadding(new Insets(40));
        exportLayout.setStyle("-fx-background-color: white; -fx-border-color: #2c3e50; -fx-border-width: 4px;");
        exportLayout.setAlignment(Pos.TOP_LEFT);

        Label headerLbl = new Label("KAUFBELEG / RECHNUNG");
        headerLbl
                .setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 0 0 20 0;");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        exportLayout.getChildren().addAll(
                createExportLabel("Rechnungsnummer: " + receipt.getReceiptId(), true),
                createExportLabel("Kunde: " + receipt.getCustomerName(), false),
                createExportLabel("Datum: " + receipt.getCreatedAt().format(dtf), false),
                new Label(" ") // Optischer Abstand vor der Betragsaufstellung.
        );

        double brutto = receipt.getTotalAmount();
        double netto = brutto / 1.19;
        double steuer = brutto - netto;

        exportLayout.getChildren().addAll(
                createExportLabel("========================================", false),
                createExportLabel(String.format("Netto (exkl. MwSt.): %.2f EUR", netto), false),
                createExportLabel(String.format("zzgl. 19%% MwSt.: %.2f EUR", steuer), false),
                createExportLabel("----------------------------------------", false),
                createExportLabel(String.format("GESAMTBETRAG: %.2f EUR", brutto), true),
                createExportLabel("========================================", false));

        new Scene(exportLayout);
        takeSnapshotAndSave(exportLayout, "Quittung_" + receipt.getReceiptId() + ".png", "Quittung speichern unter...");
    }

    /**
     * Erstellt eine gemeinsame PNG-Ansicht aller aktiven Tickets einer Bestellung.
     *
     * @param receipt Quittung zur Benennung und Zuordnung des Exports
     * @param tickets zu exportierende Tickets der Bestellung
     */
    private void saveTicketGroupAsImage(Receipt receipt, List<Ticket> tickets) {
        VBox exportLayout = new VBox(20);
        exportLayout.setPadding(new Insets(30));
        exportLayout.setStyle("-fx-background-color: white;");
        exportLayout.setAlignment(Pos.TOP_CENTER);

        Label mainHeader = new Label("TICKETS - BESTELLUNG " + receipt.getReceiptId());
        mainHeader
                .setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 0 0 10 0;");
        exportLayout.getChildren().add(mainHeader);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm 'Uhr'");

        for (Ticket ticket : tickets) {
            VBox ticketBox = new VBox(8);
            ticketBox.setPadding(new Insets(20));
            ticketBox.setStyle(
                    "-fx-border-color: #2ecc71; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-color: #f9fbf9; -fx-background-radius: 8px; -fx-pref-width: 500px;");

            String eventTitle = ticket.getEvent() != null ? ticket.getEvent().getTitle() : "Event";
            String eventDate = ticket.getEvent() != null ? ticket.getEvent().getDateTime().format(dtf) : "";

            Label eventLbl = new Label(eventTitle);
            eventLbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

            String seatInfo = ticket.getSeatInfo() != null ? ticket.getSeatInfo() : "Keine Platzinfo";
            Label seatLbl = new Label("Bereich: " + ticket.getSection().getName() + " | " + seatInfo);
            seatLbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

            Label idLbl = new Label("Ticket-ID: " + ticket.getTicketId() + " | Typ: " + ticket.getCustomerType());
            idLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");

            ticketBox.getChildren().addAll(eventLbl, new Label(eventDate), seatLbl, idLbl);
            exportLayout.getChildren().add(ticketBox);
        }

        new Scene(exportLayout);
        takeSnapshotAndSave(exportLayout, "Tickets_" + receipt.getReceiptId() + ".png", "Tickets speichern unter...");
    }

    /**
     * Erstellt eine einheitlich formatierte Textzeile für PNG-Exporte.
     *
     * @param text anzuzeigender Text
     * @param bold {@code true} für hervorgehobene Schrift
     * @return formatierte Exportbeschriftung
     */
    private Label createExportLabel(String text, boolean bold) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 16px; -fx-text-fill: #34495e;" + (bold ? " -fx-font-weight: bold;" : ""));
        return lbl;
    }

    /**
     * Erstellt einen Schnappschuss des vorbereiteten Layouts und speichert ihn nach
     * Benutzerauswahl als PNG-Datei.
     *
     * @param layout zu rendernder JavaFX-Container
     * @param defaultName vorgeschlagener Dateiname
     * @param windowTitle Titel des Speicherdialogs
     */
    private void takeSnapshotAndSave(VBox layout, String defaultName, String windowTitle) {
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.WHITE);
        WritableImage image = layout.snapshot(params, null);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(windowTitle);
        fileChooser.setInitialFileName(defaultName);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Bild", "*.png"));

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                app.showAlert(Alert.AlertType.INFORMATION, "Erfolg", "Erfolgreich gespeichert!");
            } catch (IOException ex) {
                app.showAlert(Alert.AlertType.ERROR, "Fehler", "Speichern fehlgeschlagen: " + ex.getMessage());
            }
        }
    }

    /**
     * Fragt die Stornierung eines Tickets ab, führt sie über den Service aus und
     * speichert den geänderten Benutzerzustand.
     *
     * @param ticket zu stornierendes Ticket
     * @param eventTitle Titel des zugehörigen Events
     * @param loggedInUser Besitzer des Tickets
     */
    private void cancelTicket(Ticket ticket, String eventTitle, User loggedInUser) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        if (app.getPrimaryStage() != null) {
            confirmAlert.initOwner(app.getPrimaryStage());
        }
        confirmAlert.setTitle("Ticket stornieren");
        confirmAlert.setHeaderText("Möchten Sie dieses Ticket wirklich stornieren?");
        confirmAlert.setContentText("Das Ticket wird dauerhaft storniert.");

        DialogPane dialogPane = confirmAlert.getDialogPane();
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setText("Ja, stornieren");
        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
        cancelButton.setText("Abbrechen");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = bookingService.cancelTicket(ticket, loggedInUser);
            if (success) {
                userRepo.saveUsersToFile();
                app.showAlert(Alert.AlertType.INFORMATION, "Storniert", "Das Ticket wurde erfolgreich storniert.");
                app.navigateTo(ScreenManager.Screen.MY_TICKETS);
            } else {
                app.showAlert(Alert.AlertType.ERROR, "Fehler", "Fehler beim Stornieren.");
            }
        }
    }

    /**
     * Extrahiert Reihen- und Sitznummer aus der textuellen Platzbeschreibung eines
     * Tickets.
     *
     * @param seatInfoStr Platzbeschreibung des Tickets
     * @return Array mit Reihe an Index 0 und Sitz an Index 1; jeweils 0 bei Fehlern
     */
    private int[] parseRowAndSeat(String seatInfoStr) {
        int[] result = new int[] { 0, 0 };
        if (seatInfoStr == null)
            return result;
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
}
