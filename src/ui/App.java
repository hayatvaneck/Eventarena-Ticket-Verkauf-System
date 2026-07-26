package ui;

import domain.*;
import domain.Event.EventType;
import repository.*;
import service.BookingService;
import controller.SeatSelectionController;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import javafx.scene.paint.Color;
import ui.screens.LoginScreen;
import ui.screens.MainMenuScreen;
import ui.screens.MyTicketsScreen;
import ui.screens.RegisterScreen;
import ui.screens.CartScreen;
import ui.screens.BookingConfirmationScreen;
import ui.screens.GraphicSectionSelectionScreen;
import ui.screens.SeatSelectionScreen;
import ui.screens.StandingAreaSelectionScreen;

public class App extends Application {
    
    private Stage primaryStage;
    private ScreenManager screenManager;
    private final EventRepository eventRepo = EventRepository.getInstance();
    private final BookingService bookingService = new BookingService();
    private final UserRepository userRepo = new UserRepository();
    private final ReceiptRepository receiptRepo = ReceiptRepository.getInstance();
    private User loggedInUser = null;
    private Runnable postLoginAction = null; // Merkt sich was nach dem Login passieren soll
    private List<Seat> cartSeats = new ArrayList<>();
    private Receipt lastReceipt = null;
    private List<Ticket> lastBookedTickets = new ArrayList<>();
    private String lastBookingInfoMessage = null;

    // Globale Zustände für den Buchungsprozess
    private Event currentSelectedEvent = null;
    private Section currentSelectedSection = null;
    private Label selectionStatusLabel = new Label("Kein Platz ausgewählt");
    private long customerIdCounter = 1L;

    // Methode, zum prüfen, ob jemand eingeloggt ist
    public boolean isLoggedIn() {
        return loggedInUser != null;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Arena Ticketsystem OOP");

        this.screenManager = new ScreenManager()
            .register(ScreenManager.Screen.MAIN_MENU, this::showMainMenu)
            .register(ScreenManager.Screen.BOOKING_CONFIRMATION, this::showBookingConfirmationView)
            .register(ScreenManager.Screen.MY_TICKETS, this::showMyTicketsView)
            .register(ScreenManager.Screen.LOGIN, this::showLoginView)
            .register(ScreenManager.Screen.REGISTER, this::showRegisterView)
            .register(ScreenManager.Screen.GRAPHIC_SECTION_SELECTION, this::showGraphicSectionSelection)
            .register(ScreenManager.Screen.SEAT_SELECTION, this::showSeatSelection)
            .register(ScreenManager.Screen.STANDING_AREA_SELECTION, this::showStandingAreaSelection)
            .register(ScreenManager.Screen.CART, this::showCartView);

        screenManager.navigateTo(ScreenManager.Screen.MAIN_MENU);
    }

    // --- SCREEN 1: HAUPTMENÜ ---
    private void showMainMenu() {
        MainMenuScreen mainMenuScreen = new MainMenuScreen(
            this,
            eventRepo
        );

        primaryStage.setScene(mainMenuScreen.buildScene());
        primaryStage.show();
    }

    private void showBookingConfirmationView() {
        BookingConfirmationScreen bookingConfirmationScreen = new BookingConfirmationScreen(this);
        primaryStage.setScene(bookingConfirmationScreen.buildScene());
        primaryStage.show();
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public Event getCurrentSelectedEvent() {
        return currentSelectedEvent;
    }

    public Section getCurrentSelectedSection() {
        return currentSelectedSection;
    }

    public void setCurrentSelectedSection(Section selectedSection) {
        this.currentSelectedSection = selectedSection;
    }

    public List<Seat> getCartSeats() {
        return cartSeats;
    }

    public Label getSelectionStatusLabel() {
        return selectionStatusLabel;
    }

    public void setCurrentSelectedEvent(Event selectedEvent) {
        this.currentSelectedEvent = selectedEvent;
    }

    public void logoutUser() {
        this.loggedInUser = null;
        this.lastBookingInfoMessage = null;
        this.lastReceipt = null;
        this.lastBookedTickets.clear();
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public User validateUserCredentials(String email, String password) {
        return userRepo.validateUser(email, password);
    }

    public boolean registerUser(User user) {
        return userRepo.registerUser(user);
    }

    public void runPostLoginActionOrGoMainMenu() {
        if (this.postLoginAction != null) {
            Runnable action = this.postLoginAction;
            this.postLoginAction = null;
            action.run();
        } else {
            screenManager.navigateTo(ScreenManager.Screen.MAIN_MENU);
        }
    }

    public void clearPostLoginAction() {
        this.postLoginAction = null;
    }

    public void navigateTo(ScreenManager.Screen screen) {
        screenManager.navigateTo(screen);
    }

    public void bookCurrentCart(List<String> chosenTypes) {
        ensureLoggedIn(() -> {
            if (chosenTypes.size() != cartSeats.size()) {
                showAlert(Alert.AlertType.ERROR, "Fehler", "Fehler bei der Zuordnung der Ticket-Typen.");
                return;
            }

            executeBooking(new ArrayList<>(cartSeats), chosenTypes);
            cartSeats.clear();
        });
    }

    public List<Receipt> getReceiptsForLoggedInUser() {
        if (loggedInUser == null) {
            return new ArrayList<>();
        }
        return receiptRepo.findByUserEmail(loggedInUser.getEmail());
    }

    public void openReceiptHistoryWindow() {
        List<Receipt> receipts = getReceiptsForLoggedInUser();

        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.setTitle("Gespeicherte Quittungen");

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label title = new Label("Quittungen");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ListView<String> receiptList = new ListView<>();
        receiptList.setPrefHeight(250);

        if (receipts.isEmpty()) {
            receiptList.getItems().add("Keine Quittungen gefunden.");
            receiptList.setDisable(true);
        } else {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            for (Receipt receipt : receipts) {
                receiptList.getItems().add(
                    receipt.getReceiptId() + " | " +
                    receipt.getCreatedAt().format(dtf) + " | " +
                    String.format("%.2f EUR", receipt.getTotalAmount())
                );
            }
        }

        Button openButton = new Button("Quittung oeffnen");
        openButton.setDisable(receipts.isEmpty());
        openButton.setOnAction(e -> {
            int selectedIndex = receiptList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < receipts.size()) {
                openReceiptWindow(receipts.get(selectedIndex));
            }
        });

        root.getChildren().addAll(title, receiptList, openButton);
        stage.setScene(new Scene(root, 500, 350));
        stage.show();
    }

    public void openTicketWindow(Ticket ticket) {
        if (ticket == null) {
            return;
        }

        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.setTitle("Event / Ticket");

        VBox root = new VBox(8);
        root.setPadding(new Insets(15));

        Label title = new Label("Ticket " + ticket.getTicketId());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        String eventName = ticket.getEvent() != null ? ticket.getEvent().getTitle() : "-";
        String eventDate = ticket.getEvent() != null ? ticket.getEvent().getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : "-";
        String sectionName = ticket.getSection() != null ? ticket.getSection().getName() : "-";
        String customerType = ticket.getCustomerType() != null ? ticket.getCustomerType() : "Standard";

        root.getChildren().addAll(
            title,
            new Label("Event: " + eventName),
            new Label("Datum: " + eventDate),
            new Label("Bereich: " + sectionName),
            new Label("Platz: " + ticket.getSeatInfo()),
            new Label("Typ: " + customerType),
            new Label(String.format("Preis: %.2f EUR", ticket.getFinalPrice()))
        );

        stage.setScene(new Scene(root, 420, 280));
        stage.show();
    }

    public void openReceiptWindow(Receipt receipt) {
        if (receipt == null) {
            return;
        }

        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.setTitle("Quittung " + receipt.getReceiptId());

        VBox root = new VBox(8);
        root.setPadding(new Insets(15));

        Label title = new Label("Quittung " + receipt.getReceiptId());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        root.getChildren().addAll(
            title,
            new Label("Kunde: " + receipt.getCustomerName()),
            new Label("E-Mail: " + receipt.getUserEmail()),
            new Label("Zeitpunkt: " + receipt.getCreatedAt().format(dtf)),
            new Label(String.format("Gesamtbetrag: %.2f EUR", receipt.getTotalAmount())),
            new Label("Tickets:")
        );

        for (String ticketId : receipt.getTicketIds()) {
            root.getChildren().add(new Label("- " + ticketId));
        }

        stage.setScene(new Scene(root, 460, 340));
        stage.show();
    }

    public boolean hasLastBookingInfo() {
        return lastBookingInfoMessage != null && !lastBookingInfoMessage.trim().isEmpty();
    }

    public String getLastBookingInfoMessage() {
        return lastBookingInfoMessage;
    }

    public List<Ticket> getLastBookedTickets() {
        return new ArrayList<>(lastBookedTickets);
    }

    public void clearLastBookingInfo() {
        this.lastBookingInfoMessage = null;
        this.lastReceipt = null;
        this.lastBookedTickets.clear();
    }

    public void openLastReceiptWindow() {
        if (lastReceipt != null) {
            openReceiptWindow(lastReceipt);
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Keine Quittung", "Es ist keine aktuelle Quittung vorhanden.");
        }
    }

    public void openLastBookedEventWindow() {
        if (!lastBookedTickets.isEmpty()) {
            openTicketWindow(lastBookedTickets.get(0));
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Kein Ticket", "Es ist kein aktuelles Ticket vorhanden.");
        }
    }

    /*
    // --- SCREEN 1b: BLOCKAUSWAHL ---    ERSETZT DURCH GRAFISCHE DARSTELLUNG
    private void showSectionSelection() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f6fa;");

        Label title = new Label("Blockauswahl");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label eventInfo = new Label("Event: " + currentSelectedEvent.getTitle());
        eventInfo.setStyle("-fx-font-style: italic;");

        ListView<String> sectionListView = new ListView<>();
        // Filtern der Blöcke mit Sitzplätzen
        List<SeatedSection> seatedSections = new ArrayList<>();
        for (Section section : currentSelectedEvent.getSections()) {
            if (section instanceof SeatedSection) {
                seatedSections.add((SeatedSection) section);
                sectionListView.getItems().add(section.getName() + " (Faktor: x" + section.getPriceFactor() + ")");
            }
        }

        Button nextButton = new Button("Sitzplätze anzeigen");
        nextButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px;");
        nextButton.setPrefWidth(200);

        nextButton.setOnAction(e -> {
            int selectedIndex = sectionListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                currentSelectedSection = seatedSections.get(selectedIndex);
                showSeatSelection();
            } else {
                showAlert(Alert.AlertType.WARNING, "Auswahl fehlt", "Bitte wählen Sie einen Sitzplatz-Block aus!");
            }
        });

        Button backButton = new Button("Zurück zu den Events");
        backButton.setOnAction(e -> showMainMenu());

        // Falls das Event keine Sitzplätze hat
        if (seatedSections.isEmpty()) {
            sectionListView.setPlaceholder(new Label("Keine Sitzplatz-Blöcke für dieses Event verfügbar."));
            nextButton.setDisable(true);
        }

        root.getChildren().addAll(title, eventInfo, new Label("Verfügbare Blöcke:"), sectionListView, nextButton, backButton);
        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
    }
    */


    // TICKET VIEW
    private void showMyTicketsView() {
        ensureLoggedIn(() -> {
            MyTicketsScreen myTicketsScreen = new MyTicketsScreen(this, bookingService, userRepo);
            primaryStage.setScene(myTicketsScreen.buildScene());
            primaryStage.show();
        });
    }

    // LOGIN SCREEN
    private void showLoginView() {
        LoginScreen loginScreen = new LoginScreen(this);
        primaryStage.setScene(loginScreen.buildScene());
        primaryStage.show();
    }

    // REGISTRIERUNGS SCREEN
    private void showRegisterView() {
        RegisterScreen registerScreen = new RegisterScreen(this);
        primaryStage.setScene(registerScreen.buildScene());
        primaryStage.show();
    }

    // --- SCREEN 2: SITZAUSWAHL ---
    public void showSeatSelection() {
        SeatSelectionScreen seatSelectionScreen = new SeatSelectionScreen(this);
        primaryStage.setScene(seatSelectionScreen.buildScene());
        primaryStage.show();
    }

    // --- SCREEN 2b: STEHPLATZ-ANZAHL WÄHLEN ---
    public void showStandingAreaSelection() {
        StandingAreaSelectionScreen standingAreaSelectionScreen = new StandingAreaSelectionScreen(this);
        primaryStage.setScene(standingAreaSelectionScreen.buildScene());
        primaryStage.show();
    }

    // --- SCREEN 3: WARENKORB ANSICHT ---
    private void showCartView() {
        CartScreen cartScreen = new CartScreen(this);
        primaryStage.setScene(cartScreen.buildScene());
        primaryStage.show();
    }

    private void executeBooking(List<Seat> chosenSeats, List<String> chosenTypes) {
        String firstName = loggedInUser.getFirstName();
        String lastName = loggedInUser.getLastName();
        String userEmail = loggedInUser.getEmail();

        List<Ticket> generatedTickets = new ArrayList<>();
        double totalExtendedPrice = 0.0;

        try {
            for (int i = 0; i < chosenSeats.size(); i++) {
                Seat seat = chosenSeats.get(i);
                String currentType = chosenTypes.get(i);

                Section seatSection = (seat != null && seat.getSection() != null)
                                        ? seat.getSection()
                                        : currentSelectedSection;

                Customer customer = new Customer(customerIdCounter++, firstName, lastName, currentType);

                Ticket ticket;
                if (seatSection instanceof SeatedSection) {
                    ticket = bookingService.bookSpecificTicket(currentSelectedEvent.getId(), seatSection.getName(), seat.getRowNumber(), seat.getSeatNumber(), customer, userEmail);
                } else {
                    ticket = bookingService.bookTicket(currentSelectedEvent.getId(), seatSection.getName(), customer, userEmail);
                }

                generatedTickets.add(ticket);
                totalExtendedPrice += ticket.getFinalPrice();
            }

            for (Ticket ticket : generatedTickets) {
                loggedInUser.addTicket(ticket);
            }
            userRepo.saveUsersToFile();

            lastBookedTickets = new ArrayList<>(generatedTickets);
            lastReceipt = createAndSaveReceipt(firstName, lastName, userEmail, totalExtendedPrice, generatedTickets);

            StringBuilder successMessage = new StringBuilder();
            successMessage.append(String.format("Käufer: %s %s\nGesamtpreis: %.2f EUR\n\nGekaufte Tickets:\n", firstName, lastName, totalExtendedPrice));

            for (Ticket t : generatedTickets) {
                successMessage.append(String.format("- %s | (%s) - %.2f EUR\n",
                    t.getSection() != null ? t.getSection().getName() : "Bereich",
                    //t.getSeatInfo(),
                    t.getCustomer().getCustomerType(),
                    t.getFinalPrice()
                ));
            }
                
                /* 
                if (currentSelectedSection instanceof StandingSection) {
                    successMessage.append(String.format("- Stehplatz %s (%s) - %.2f EUR\n", 
                    t.getTicketId(), 
                    t.getCustomer().getCustomerType(), 
                    t.getFinalPrice()
                ));
            } else {
                successMessage.append(String.format("- Sitzplatz %s (%s) - %.2f EUR\n",
            t.getTicketId(),
            t.getCustomer().getCustomerType(),
            t.getFinalPrice()
        ));
    }
}
*/

            lastBookingInfoMessage = successMessage.toString();

            cartSeats.clear();
            screenManager.navigateTo(ScreenManager.Screen.BOOKING_CONFIRMATION);
    } catch (Exception ex) {
        showAlert(Alert.AlertType.ERROR, "Fehler bei der Buchung", ex.getMessage());
    }
}

    private Receipt createAndSaveReceipt(String firstName, String lastName, String userEmail, double totalAmount, List<Ticket> generatedTickets) {
        String receiptId = receiptRepo.nextReceiptId();
        String customerName = firstName + " " + lastName;

        List<String> ticketIds = new ArrayList<>();
        for (Ticket ticket : generatedTickets) {
            ticketIds.add(ticket.getTicketId());
        }

        Receipt receipt = new Receipt(
            receiptId,
            userEmail,
            customerName,
            LocalDateTime.now(),
            totalAmount,
            ticketIds
        );

        receiptRepo.save(receipt);
        return receipt;
    }

    public void updateSelectionLabel(String text) {
        selectionStatusLabel.setText(text);
    }

    public void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void showGraphicSectionSelection() {
        GraphicSectionSelectionScreen graphicSectionSelectionScreen = new GraphicSectionSelectionScreen(this);
        primaryStage.setScene(graphicSectionSelectionScreen.buildScene());
        primaryStage.show();
    }


    public Section findSectionByName(String name) {
        if (currentSelectedEvent == null || currentSelectedEvent.getSections() == null) {
            return null;
        }
        for (Section section : currentSelectedEvent.getSections()) {
            if (section.getName().equalsIgnoreCase(name)) {
                return section;
            }
        }
        return null;
    }

    private void ensureLoggedIn(Runnable onLoggedInAction) {
        if (this.loggedInUser != null) {
            onLoggedInAction.run();
        } else {
            this.postLoginAction = onLoggedInAction;

            showAlert(Alert.AlertType.INFORMATION, "Anmeldung erforderlich", "Bitte logge dich ein oder erstelle ein Konto, um die Buchung abzuschließen.");
        
            screenManager.navigateTo(ScreenManager.Screen.LOGIN);
        }
    }

    private int[] parseRowAndSeat(String seatInfoStr) {
        int[] result = new int[]{0,0};
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

        }
        return result;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
