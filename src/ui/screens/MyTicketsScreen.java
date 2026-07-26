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

/**
 * Die Klasse MyTicketsScreen zeigt gebuchte Tickets, ermoeglicht Oeffnen und Stornieren sowie Quittungszugriff.

 */

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

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        VBox ticketContainer = createVBox(15, Pos.TOP_CENTER);
        ticketContainer.setPadding(new Insets(10));

        User loggedInUser = app.getLoggedInUser();
        List<Ticket> myTickets = loggedInUser != null ? loggedInUser.getPurchasedTickets() : null;

        if (myTickets == null || myTickets.isEmpty()) {
            Label noTicketsLabel = new Label("Sie haben bisher noch keine Tickets gebucht.");
            noTicketsLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
            ticketContainer.getChildren().add(noTicketsLabel);
        } else {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm 'Uhr'");

            for (Ticket ticket : myTickets) {
                HBox ticketCard = createTicketCard(ticket, dtf, loggedInUser);
                ticketContainer.getChildren().add(ticketCard);
            }
        }

        scrollPane.setContent(ticketContainer);

        Button receiptsButton = createSecondaryButton("Quittungen anzeigen");
        receiptsButton.setOnAction(e -> app.openReceiptHistoryWindow());

        Button backButton = createPrimaryButton("Zurueck zum Hauptmenue");
        backButton.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 6px; -fx-padding: 8px 15px; -fx-cursor: Hand;");
        backButton.setOnAction(e -> app.navigateTo(ScreenManager.Screen.MAIN_MENU));

        HBox actionButtons = createHBox(10, Pos.CENTER);
        actionButtons.getChildren().addAll(receiptsButton, backButton);

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
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);"
        );

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
                seatInfo = String.format("%s | Reihe %d | Sitz %d", ticket.getSection().getName(), rowAndSeat[0], rowAndSeat[1]);
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

        Label lblPrice = new Label(String.format("%.2f â‚¬", ticket.getFinalPrice()));
        lblPrice.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Button btnCancel = createDangerButton("Stornieren");
        btnCancel.setStyle(
            "-fx-background-color: #e74c3c;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 12px;" +
            "-fx-background-radius: 4px;" +
            "-fx-padding: 6px 12px;" +
            "-fx-cursor: Hand;"
        );

        btnCancel.setOnAction(e -> cancelTicket(ticket, eventTitle, loggedInUser));

        Button btnOpenEvent = createPrimaryButton("Event oeffnen");
        btnOpenEvent.setStyle(
            "-fx-background-color: #2c3e50;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 12px;" +
            "-fx-background-radius: 4px;" +
            "-fx-padding: 6px 12px;" +
            "-fx-cursor: Hand;"
        );
        btnOpenEvent.setOnAction(e -> app.openTicketWindow(ticket));

        VBox actionBox = new VBox(8);
        actionBox.setAlignment(Pos.CENTER_RIGHT);
        actionBox.getChildren().addAll(btnOpenEvent, btnCancel);

        ticketCard.getChildren().addAll(details, spacer, lblPrice, actionBox);
        return ticketCard;
    }

    private void cancelTicket(Ticket ticket, String eventTitle, User loggedInUser) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Ticket stornieren");
        confirmAlert.setHeaderText("Moechten Sie dieses Ticket wirklich stornieren?");

        String placeText = (ticket.getSection() instanceof StandingSection)
            ? "Bereich: " + ticket.getSection().getName()
            : "Platz: " + ticket.getSeatInfo();

        confirmAlert.setContentText(
            "Event: " + eventTitle + "\n" +
            placeText + "\n" +
            "Preis: " + String.format("%.2f â‚¬", ticket.getFinalPrice())
        );

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
        int[] result = new int[]{0, 0};
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
}



