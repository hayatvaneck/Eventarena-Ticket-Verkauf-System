package ui;

import domain.*;
import repository.*;
import service.BookingService;
import ui.dialogs.ReceiptDialog;
import ui.dialogs.ReceiptHistoryDialog;
import ui.dialogs.TicketDialog;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import ui.screens.LoginScreen;
import ui.screens.MainMenuScreen;
import ui.screens.MyTicketsScreen;
import ui.screens.RegisterScreen;
import ui.screens.CartScreen;
import ui.screens.BookingConfirmationScreen;
import ui.screens.GraphicSectionSelectionScreen;
import ui.screens.SeatSelectionScreen;
import ui.screens.StandingAreaSelectionScreen;

/**
 * Die Klasse App startet die JavaFX-Anwendung, verwaltet die Navigation und
 * hält den globalen Buchungszustand.
 */

public class App extends Application {

    private Stage primaryStage;
    private ScreenManager screenManager;
    private final EventRepository eventRepo = EventRepository.getInstance();
    private final repository.EmployeeRepository employeeRepo = repository.EmployeeRepository.getInstance();
    private final BookingService bookingService = new BookingService();
    private final UserRepository userRepo = new UserRepository();
    private final ReceiptRepository receiptRepo = ReceiptRepository.getInstance();
    private User loggedInUser = null;
    private Runnable postLoginAction = null; // Merkt sich was nach dem Login passieren soll
    private List<CartItem> cartItems = new ArrayList<>();
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

        this.primaryStage.setMaximized(true);

        this.screenManager = new ScreenManager()
                .register(ScreenManager.Screen.MAIN_MENU, this::showMainMenu)
                .register(ScreenManager.Screen.BOOKING_CONFIRMATION, this::showBookingConfirmationView)
                .register(ScreenManager.Screen.MY_TICKETS, this::showMyTicketsView)
                .register(ScreenManager.Screen.LOGIN, this::showLoginView)
                .register(ScreenManager.Screen.REGISTER, this::showRegisterView)
                .register(ScreenManager.Screen.GRAPHIC_SECTION_SELECTION, this::showGraphicSectionSelection)
                .register(ScreenManager.Screen.SEAT_SELECTION, this::showSeatSelection)
                .register(ScreenManager.Screen.STANDING_AREA_SELECTION, this::showStandingAreaSelection)
                .register(ScreenManager.Screen.CART, this::showCartView)
                .register(ScreenManager.Screen.EMPLOYEE_EVENTS, this::showEmployeeEventsView)
                .register(ScreenManager.Screen.EMPLOYEE_LOGIN, this::showEmployeeLoginView);

        screenManager.navigateTo(ScreenManager.Screen.MAIN_MENU);
    }

    // --- SCREEN 1: HAUPTMENÜ ---
    private void showMainMenu() {
        MainMenuScreen mainMenuScreen = new MainMenuScreen(this, eventRepo);
        switchScene(mainMenuScreen.buildScene());
    }

    private void showBookingConfirmationView() {
        BookingConfirmationScreen bookingConfirmationScreen = new BookingConfirmationScreen(this);
        switchScene(bookingConfirmationScreen.buildScene());
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

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public BookingService getBookingService() {
        return bookingService;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
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
            if (chosenTypes.size() != cartItems.size()) {
                showAlert(Alert.AlertType.ERROR, "Fehler", "Fehler bei der Zuordnung der Ticket-Typen.");
                return;
            }

            executeBooking(new ArrayList<>(cartItems), chosenTypes);
            cartItems.clear();
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
        ReceiptHistoryDialog.show(primaryStage, receipts, this::openReceiptWindow);
    }

    public void openTicketWindow(Ticket ticket) {
        TicketDialog.show(primaryStage, ticket);
    }

    public void openReceiptWindow(Receipt receipt) {
        ReceiptDialog.show(primaryStage, receipt);
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

    // TICKET VIEW
    private void showMyTicketsView() {
        ensureLoggedIn(() -> {
            MyTicketsScreen myTicketsScreen = new MyTicketsScreen(this, bookingService, userRepo);
            switchScene(myTicketsScreen.buildScene());
        });
    }

    // LOGIN SCREEN
    private void showLoginView() {
        LoginScreen loginScreen = new LoginScreen(this);
        switchScene(loginScreen.buildScene());
    }

    // REGISTRIERUNGS SCREEN
    private void showRegisterView() {
        RegisterScreen registerScreen = new RegisterScreen(this);
        switchScene(registerScreen.buildScene());
    }

    // --- SCREEN 2: SITZAUSWAHL ---
    public void showSeatSelection() {
        SeatSelectionScreen seatSelectionScreen = new SeatSelectionScreen(this);
        switchScene(seatSelectionScreen.buildScene());
    }

    // --- SCREEN 2b: STEHPLATZ-ANZAHL WÄHLEN ---
    public void showStandingAreaSelection() {
        StandingAreaSelectionScreen standingAreaSelectionScreen = new StandingAreaSelectionScreen(this);
        switchScene(standingAreaSelectionScreen.buildScene());
    }

    // --- SCREEN 3: WARENKORB ANSICHT ---
    private void showCartView() {
        CartScreen cartScreen = new CartScreen(this);
        switchScene(cartScreen.buildScene());
    }

    // --- SCREEN FÜR MITARBEITER ---
    private void showEmployeeEventsView() {
        ui.screens.EmployeeEventScreen employeeScreen = new ui.screens.EmployeeEventScreen(this, eventRepo);
        switchScene(employeeScreen.buildScene());
    }

    private void showEmployeeLoginView() {
        ui.screens.EmployeeLoginScreen employeeLoginScreen = new ui.screens.EmployeeLoginScreen(this);
        switchScene(employeeLoginScreen.buildScene());
    }

    public boolean validateEmployeeCredentials(String username, String password) {
        return employeeRepo.validateEmployee(username, password);
    }

    private void executeBooking(List<CartItem> chosenItems, List<String> chosenTypes) {
        String firstName = loggedInUser.getFirstName();
        String lastName = loggedInUser.getLastName();
        String userEmail = loggedInUser.getEmail();

        List<Ticket> generatedTickets = new ArrayList<>();
        double totalExtendedPrice = 0.0;

        try {
            for (int i = 0; i < chosenItems.size(); i++) {
                CartItem item = chosenItems.get(i);
                Seat seat = item.getSeat();
                Event event = item.getEvent();
                Section seatSection = item.getSection();

                String currentType = chosenTypes.get(i);
                CustomerType customerType = mapCustomerType(currentType);

                Customer customer = new Customer(customerIdCounter++, firstName, lastName, customerType);

                Ticket ticket;
                if (seatSection instanceof SeatedSection) {
                    ticket = bookingService.bookSpecificTicket(
                            event.getId(),
                            seatSection.getName(),
                            seat.getRowNumber(),
                            seat.getSeatNumber(),
                            customer,
                            userEmail);
                } else {
                    ticket = bookingService.bookTicket(
                            event.getId(),
                            seatSection.getName(),
                            customer,
                            userEmail);
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
            successMessage.append(String.format("Käufer: %s %s\nGesamtpreis: %.2f EUR\n\nGekaufte Tickets:\n",
                    firstName, lastName, totalExtendedPrice));

            for (Ticket t : generatedTickets) {
                successMessage.append(String.format("- %s | (%s) - %.2f EUR\n",
                        t.getSection() != null ? t.getSection().getName() : "Bereich",
                        t.getCustomer().getCustomerType(),
                        t.getFinalPrice()));
            }

            lastBookingInfoMessage = successMessage.toString();

            cartItems.clear();
            screenManager.navigateTo(ScreenManager.Screen.BOOKING_CONFIRMATION);
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Fehler bei der Buchung", ex.getMessage());
        }
    }

    private Receipt createAndSaveReceipt(String firstName, String lastName, String userEmail, double totalAmount,
            List<Ticket> generatedTickets) {
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
                ticketIds);

        receiptRepo.save(receipt);
        return receipt;
    }

    public void updateSelectionLabel(String text) {
        selectionStatusLabel.setText(text);
    }

    public void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        if (primaryStage != null) {
            alert.initOwner(primaryStage);
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void showGraphicSectionSelection() {
        GraphicSectionSelectionScreen graphicSectionSelectionScreen = new GraphicSectionSelectionScreen(this);
        switchScene(graphicSectionSelectionScreen.buildScene());
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

            showAlert(Alert.AlertType.INFORMATION, "Fast geschafft!",
                    "Damit wir deine Tickets sicher für dich hinterlegen können, logge dich bitte kurz ein.\n\n" +
                            "Du hast noch kein Konto? Kein Problem, die Registrierung dauert nur wenige Sekunden!");

            screenManager.navigateTo(ScreenManager.Screen.LOGIN);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public CustomerType mapCustomerType(String typeLabel) {
        if (typeLabel == null) {
            return CustomerType.STANDARD;
        }
        String normalized = typeLabel.trim().toLowerCase();

        if (normalized.startsWith("student")) {
            return CustomerType.STUDENT;
        } else if (normalized.startsWith("rentner") || normalized.startsWith("senior")) {
            return CustomerType.SENIOR;
        } else if (normalized.startsWith("vip")) {
            return CustomerType.VIP;
        } else if (normalized.startsWith("kind")) {
            return CustomerType.KIND;
        } else {
            return CustomerType.STANDARD;
        }
    }

    private void switchScene(Scene newScene) {
        if (primaryStage.getScene() == null) {
            // Beim allerersten Start gibt es noch keine Szene
            primaryStage.setScene(newScene);
        } else {
            // 1. Wir holen uns den Inhalt (Root) der neuen Szene
            javafx.scene.Parent newRoot = newScene.getRoot();

            // 2. TRICK: Wir geben der neuen Szene einen leeren Platzhalter,
            // damit unser eigentlicher Inhalt "befreit" wird und keinen Besitzer mehr hat!
            newScene.setRoot(new javafx.scene.layout.Region());

            // 3. Jetzt können wir den befreiten Inhalt fehlerfrei in unser Fenster laden!
            primaryStage.getScene().setRoot(newRoot);
        }
        primaryStage.show();
    }
}
