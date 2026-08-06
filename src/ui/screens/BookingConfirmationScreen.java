package ui.screens;

import domain.Ticket;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ui.App;
import ui.ScreenManager;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Die Klasse BookingConfirmationScreen zeigt die erfolgreiche Buchung inklusive
 * aller neu erhaltenen Tickets.
 * 
 */

public class BookingConfirmationScreen extends BaseScreen {

    /** Anwendungskontext für Buchungsdaten, Dialoge und Navigation. */
    private final App app;

    /**
     * Erstellt den Screen für die zuletzt abgeschlossene Buchung.
     *
     * @param app zentraler Anwendungskontext
     */
    public BookingConfirmationScreen(App app) {
        this.app = app;
    }

    /**
     * Baut die Bestätigungsansicht mit Ticketkarten und weiterführenden Aktionen
     * auf.
     *
     * @return vollständige Buchungsbestätigungsszene
     */
    @Override
    public Scene buildScene() {
        // Grundlayout mit getrenntem Inhalts- und Aktionsbereich.
        javafx.scene.layout.BorderPane root = new javafx.scene.layout.BorderPane();
        root.setStyle("-fx-background-color: #f5f5f7;");

        // Kopfbereich und Zusammenfassung der abgeschlossenen Buchung.
        VBox headerBox = createHeaderBox("BUCHUNG ERFOLGREICH", "Ihre Buchung wurde erfolgreich abgeschlossen.");

        List<Ticket> bookedTickets = app.getLastBookedTickets();

        Label summary = new Label("Erhaltene Tickets: " + bookedTickets.size());
        summary.setStyle(
                "-fx-text-fill: #1e8449;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-color: #eafaf1;" +
                        "-fx-border-color: #2ecc71;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 6px;" +
                        "-fx-background-radius: 6px;" +
                        "-fx-padding: 10px;");

        VBox ticketList = createVBox(10, Pos.TOP_CENTER);
        ticketList.setPadding(new Insets(4, 2, 4, 2));
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        if (bookedTickets.isEmpty()) {
            Label noTicketLabel = createMutedInfoLabel("Keine Tickets zur aktuellen Buchung gefunden.");
            ticketList.getChildren().add(noTicketLabel);
        } else {
            for (Ticket ticket : bookedTickets) {
                ticketList.getChildren().add(createTicketCard(ticket, dateFormat));
            }
        }

        ScrollPane detailsScroll = createTransparentScrollPane(ticketList);
        detailsScroll.setPrefHeight(300);

        // Gleich breite Folgeaktionen sorgen für eine ruhige horizontale Anordnung.
        Button toMainButton = createBackButton("Zum Hauptmenü");
        toMainButton.setPrefWidth(250);
        toMainButton.setMinHeight(45);
        toMainButton.setMaxHeight(45);
        toMainButton.setOnAction(e -> {
            app.clearLastBookingInfo();
            app.navigateTo(ScreenManager.Screen.MAIN_MENU);
        });

        Button toTicketsButton = createSelectingButton("Meine Tickets");
        toTicketsButton.setPrefWidth(250);
        toTicketsButton.setMinHeight(45);
        toTicketsButton.setMaxHeight(45);
        toTicketsButton.setOnAction(e -> {
            app.clearLastBookingInfo();
            app.navigateTo(ScreenManager.Screen.MY_TICKETS);
        });

        Button openReceiptButton = createConfirmButton("Quittung öffnen");
        openReceiptButton.setPrefWidth(250);
        openReceiptButton.setMinHeight(45);
        openReceiptButton.setMaxHeight(45);
        openReceiptButton.setOnAction(e -> app.openLastReceiptWindow());

        // Navigation zu den wichtigsten Folgeschritten.
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(toMainButton, toTicketsButton, openReceiptButton);


        VBox topBox = createRoot(20, new Insets(30, 30, 20, 30), Pos.TOP_CENTER);
        topBox.getChildren().addAll(headerBox, summary, detailsScroll);

        HBox dummyFooter = createInvisibleStandardFooter();
        VBox bottomBox = createRoot(10, new Insets(0, 30, 30, 30), Pos.BOTTOM_CENTER);
        bottomBox.getChildren().addAll(buttonBox, dummyFooter);

        root.setCenter(topBox);
        root.setBottom(bottomBox);

        return createDefaultScene(root);
    }

    /**
     * Erstellt eine kompakte Vorschaukarte für ein neu gebuchtes Ticket.
     *
     * @param ticket darzustellendes Ticket
     * @param dateFormat Formatierung für den Veranstaltungszeitpunkt
     * @return formatierte Ticketkarte
     */
    private HBox createTicketCard(Ticket ticket, DateTimeFormatter dateFormat) {
        HBox card = createHBox(12, Pos.CENTER_LEFT);
        card.setPadding(new Insets(10));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #2ecc71;" +
                        "-fx-border-width: 1px 1px 1px 4px;" +
                        "-fx-border-radius: 4px;" +
                        "-fx-background-radius: 4px;");

        VBox details = createVBox(3, Pos.CENTER_LEFT);

        String eventTitle = (ticket.getEvent() != null) ? ticket.getEvent().getTitle() : "Event";
        String dateText = (ticket.getEvent() != null)
                ? ticket.getEvent().getDateTime().format(dateFormat)
                : "-";
        String sectionText = (ticket.getSection() != null) ? ticket.getSection().getName() : "-";
        String seatText = ticket.getSeatInfo() != null ? ticket.getSeatInfo() : "-";
        String customerType = ticket.getCustomerType() != null ? ticket.getCustomerType() : "Standard";

        Label line1 = new Label(ticket.getTicketId() + " | " + eventTitle);
        line1.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 13px;");

        Label line2 = new Label("Datum: " + dateText + " | Bereich: " + sectionText);
        line2.setStyle("-fx-text-fill: #34495e; -fx-font-size: 12px;");

        Label line3 = new Label("Platz: " + seatText + " | Typ: " + customerType + " | Preis: "
                + String.format("%.2f EUR", ticket.getFinalPrice()));
        line3.setStyle("-fx-text-fill: #34495e; -fx-font-size: 12px;");

        details.getChildren().addAll(line1, line2, line3);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button openTicketButton = createConfirmButton("Ticket öffnen");
        openTicketButton.setOnAction(e -> app.openTicketWindow(ticket));

        card.getChildren().addAll(details, spacer, openTicketButton);
        return card;
    }
}
