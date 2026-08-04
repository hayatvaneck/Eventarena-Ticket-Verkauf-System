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
        VBox headerBox = createHeaderBox("MEINE BESTELLUNGEN & TICKETS",
                "Ihre gebuchten Tickets und Quittungen im Überblick:");

        VBox mainContainer = createVBox(25, Pos.TOP_CENTER);
        mainContainer.setPadding(new Insets(10, 20, 20, 20));
        ScrollPane scrollPane = createTransparentScrollPane(mainContainer);

        User loggedInUser = app.getLoggedInUser();
        List<Ticket> myActiveTickets = loggedInUser != null ? loggedInUser.getPurchasedTickets() : new ArrayList<>();

        // 1. Alle Quittungen des Nutzers laden (Das sind unsere "Gruppen")
        List<Receipt> myReceipts = app.getReceiptsForLoggedInUser();

        // Sortieren: Neueste Bestellungen ganz nach oben
        myReceipts.sort((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()));

        if (myReceipts.isEmpty() && myActiveTickets.isEmpty()) {
            Label noTicketsLabel = createMutedInfoLabel("Sie haben bisher noch keine Tickets gebucht.");
            mainContainer.getChildren().add(noTicketsLabel);
        } else {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm 'Uhr'");

            // 2. Wir iterieren über jede Quittung und bauen einen Bestell-Kasten
            for (Receipt receipt : myReceipts) {

                // Herausfinden, welche aktiven Tickets zu dieser Quittung gehören
                List<Ticket> ticketsForThisOrder = new ArrayList<>();
                for (Ticket t : myActiveTickets) {
                    if (receipt.getTicketIds().contains(t.getTicketId())) {
                        ticketsForThisOrder.add(t);
                    }
                }

                // Wenn alle Tickets dieser Bestellung storniert wurden, ignorieren wir sie
                // (oder man könnte sie als storniert anzeigen)
                if (ticketsForThisOrder.isEmpty()) {
                    continue;
                }

                // --- BESTELL-KASTEN (UI) ---
                VBox orderCard = new VBox(15);
                orderCard.setStyle(
                        "-fx-background-color: #ffffff; " +
                                "-fx-border-color: #bdc3c7; " +
                                "-fx-border-width: 2px; " +
                                "-fx-border-radius: 8px; " +
                                "-fx-background-radius: 8px; " +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);");
                orderCard.setPadding(new Insets(0, 0, 15, 0)); // Unten etwas Platz

                // --- HEADER DER BESTELLUNG ---
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

                // --- NEUE EXPORT BUTTONS FÜR DIE GRUPPE ---
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

                // --- EINZELNE TICKETS IN DIE KARTE EINFÜGEN ---
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

        // --- ZUSAMMENBAU ---
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

    // Erstellt die visuelle Einzelkarte eines Tickets innerhalb der Bestellung
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

    // 3. GENERIERUNG DES QUITTUNGS-PNGS (Inkl. Steuern)
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
                new Label(" ") // Spacer
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

    // 4. GENERIERUNG DES TICKET-GRUPPEN-PNGS
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

    // Helfer für Labels im Export
    private Label createExportLabel(String text, boolean bold) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 16px; -fx-text-fill: #34495e;" + (bold ? " -fx-font-weight: bold;" : ""));
        return lbl;
    }

    // Helfer für den physischen Datei-Export
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

    // --- BESTEHENDE LOGIK ZUM STORNIEREN UND PARSEN (Unverändert) ---
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