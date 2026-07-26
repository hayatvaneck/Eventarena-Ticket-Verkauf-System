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

public class BookingConfirmationScreen extends BaseScreen {

    private final App app;

    public BookingConfirmationScreen(App app) {
        this.app = app;
    }

    @Override
    public Scene buildScene() {
        VBox root = createRoot(18, new Insets(25), Pos.TOP_CENTER);

        Label title = createTitle("BUCHUNG ERFOLGREICH");

        Label subtitle = createSubtitle("Ihre Buchung wurde erfolgreich abgeschlossen.");

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
            "-fx-padding: 10px;"
        );

        VBox ticketList = createVBox(10, Pos.TOP_CENTER);
        ticketList.setPadding(new Insets(4, 2, 4, 2));

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        if (bookedTickets.isEmpty()) {
            Label noTicketLabel = new Label("Keine Tickets zur aktuellen Buchung gefunden.");
            noTicketLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #7f8c8d; -fx-font-size: 13px;");
            ticketList.getChildren().add(noTicketLabel);
        } else {
            for (Ticket ticket : bookedTickets) {
                ticketList.getChildren().add(createTicketCard(ticket, dateFormat));
            }
        }

        ScrollPane detailsScroll = new ScrollPane(ticketList);
        detailsScroll.setFitToWidth(true);
        detailsScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        detailsScroll.setPrefHeight(240);

        HBox actionButtons = createHBox(10, Pos.CENTER);

        Button openReceiptButton = createPrimaryButton("Quittung oeffnen");
        openReceiptButton.setOnAction(e -> app.openLastReceiptWindow());

        actionButtons.getChildren().add(openReceiptButton);

        HBox navButtons = createHBox(10, Pos.CENTER);

        Button toTicketsButton = createSecondaryButton("Meine Tickets");
        toTicketsButton.setOnAction(e -> {
            app.clearLastBookingInfo();
            app.navigateTo(ScreenManager.Screen.MY_TICKETS);
        });

        Button toMainButton = createSecondaryButton("Zum Hauptmenue");
        toMainButton.setOnAction(e -> {
            app.clearLastBookingInfo();
            app.navigateTo(ScreenManager.Screen.MAIN_MENU);
        });

        navButtons.getChildren().addAll(toTicketsButton, toMainButton);

        root.getChildren().addAll(title, subtitle, summary, detailsScroll, actionButtons, navButtons);
        return createDefaultScene(root);
    }

    private HBox createTicketCard(Ticket ticket, DateTimeFormatter dateFormat) {
        HBox card = createHBox(12, Pos.CENTER_LEFT);
        card.setPadding(new Insets(10));
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #2ecc71;" +
            "-fx-border-width: 1px 1px 1px 4px;" +
            "-fx-border-radius: 4px;" +
            "-fx-background-radius: 4px;"
        );

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

        Label line3 = new Label("Platz: " + seatText + " | Typ: " + customerType + " | Preis: " + String.format("%.2f EUR", ticket.getFinalPrice()));
        line3.setStyle("-fx-text-fill: #34495e; -fx-font-size: 12px;");

        details.getChildren().addAll(line1, line2, line3);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button openTicketButton = createPrimaryButton("Ticket oeffnen");
        openTicketButton.setOnAction(e -> app.openTicketWindow(ticket));

        card.getChildren().addAll(details, spacer, openTicketButton);
        return card;
    }
}
