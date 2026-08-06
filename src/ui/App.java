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

    /** Hauptfenster, in dem alle Anwendungsszenen angezeigt werden. */
    private Stage primaryStage;

    /** Zentrale Zuordnung und Ausführung der Navigation zwischen Screens. */
    private ScreenManager screenManager;

    /** Repository für die im System angebotenen Veranstaltungen. */
    private final EventRepository eventRepo = EventRepository.getInstance();

    /** Repository zur Prüfung der Zugangsdaten von Mitarbeitenden. */
    private final repository.EmployeeRepository employeeRepo = repository.EmployeeRepository.getInstance();

    /** Service zur Durchführung und Stornierung von Ticketbuchungen. */
    private final BookingService bookingService = new BookingService();

    /** Repository für Registrierung, Anmeldung und Speicherung von Kunden. */
    private final UserRepository userRepo = new UserRepository();

    /** Repository für erzeugte Buchungsquittungen. */
    private final ReceiptRepository receiptRepo = ReceiptRepository.getInstance();

    /** Aktuell angemeldeter Kunde oder {@code null} ohne aktive Anmeldung. */
    private User loggedInUser = null;

    /** Aktion, die nach einer erforderlichen Anmeldung fortgesetzt werden soll. */
    private Runnable postLoginAction = null;

    /** Während der aktuellen Buchung vorgemerkte Tickets. */
    private List<CartItem> cartItems = new ArrayList<>();

    /** Zuletzt erzeugte Quittung für die Buchungsbestätigung. */
    private Receipt lastReceipt = null;

    /** Zuletzt gebuchte Tickets für Vorschau und Export. */
    private List<Ticket> lastBookedTickets = new ArrayList<>();

    /** Zusammenfassung der zuletzt erfolgreich abgeschlossenen Buchung. */
    private String lastBookingInfoMessage = null;

    /** Im aktuellen Buchungsvorgang ausgewählte Veranstaltung. */
    private Event currentSelectedEvent = null;

    /** Im aktuellen Buchungsvorgang ausgewählter Hallenbereich. */
    private Section currentSelectedSection = null;

    /** Sichtbarer Statustext zur aktuellen Platz- oder Bereichsauswahl. */
    private Label selectionStatusLabel = new Label("Kein Platz ausgewählt");

    /** Laufende Kennung für innerhalb dieser Sitzung erzeugte Kundenobjekte. */
    private long customerIdCounter = 1L;

    /**
     * Prüft, ob aktuell ein Kunde angemeldet ist.
     *
     * @return {@code true}, wenn eine aktive Kundenanmeldung besteht
     */
    public boolean isLoggedIn() {
        return loggedInUser != null;
    }

    /**
     * Initialisiert das Hauptfenster, registriert alle Navigationsziele und zeigt
     * das Hauptmenü an.
     *
     * @param primaryStage von JavaFX bereitgestelltes Hauptfenster
     */
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

    /** Erzeugt das Hauptmenü und zeigt es im Hauptfenster an. */
    private void showMainMenu() {
        MainMenuScreen mainMenuScreen = new MainMenuScreen(this, eventRepo);
        switchScene(mainMenuScreen.buildScene());
    }

    /** Zeigt nach einer erfolgreichen Buchung die Buchungsbestätigung an. */
    private void showBookingConfirmationView() {
        BookingConfirmationScreen bookingConfirmationScreen = new BookingConfirmationScreen(this);
        switchScene(bookingConfirmationScreen.buildScene());
    }

    /**
     * Liefert den aktuell angemeldeten Kunden.
     *
     * @return angemeldeter Kunde oder {@code null}
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Liefert die für den Buchungsvorgang ausgewählte Veranstaltung.
     *
     * @return ausgewählte Veranstaltung oder {@code null}
     */
    public Event getCurrentSelectedEvent() {
        return currentSelectedEvent;
    }

    /**
     * Liefert den aktuell ausgewählten Hallenbereich.
     *
     * @return ausgewählter Bereich oder {@code null}
     */
    public Section getCurrentSelectedSection() {
        return currentSelectedSection;
    }

    /**
     * Setzt den Hallenbereich, in dem anschließend Tickets ausgewählt werden.
     *
     * @param selectedSection ausgewählter Sitz- oder Stehbereich
     */
    public void setCurrentSelectedSection(Section selectedSection) {
        this.currentSelectedSection = selectedSection;
    }

    /**
     * Liefert die aktuell im Warenkorb vorgemerkten Positionen.
     *
     * @return veränderbare Warenkorbliste des laufenden Buchungsvorgangs
     */
    public List<CartItem> getCartItems() {
        return cartItems;
    }

    /**
     * Liefert den von der Oberfläche verwendeten Buchungsservice.
     *
     * @return zentraler Buchungsservice
     */
    public BookingService getBookingService() {
        return bookingService;
    }

    /**
     * Liefert das Hauptfenster als Besitzer für Dialoge und Dateiauswahldialoge.
     *
     * @return JavaFX-Hauptfenster
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Liefert die gemeinsam genutzte Beschriftung des aktuellen Auswahlstatus.
     *
     * @return Status-Label des Buchungsvorgangs
     */
    public Label getSelectionStatusLabel() {
        return selectionStatusLabel;
    }

    /**
     * Setzt die Veranstaltung, für die der Kunde Tickets auswählen möchte.
     *
     * @param selectedEvent ausgewählte Veranstaltung
     */
    public void setCurrentSelectedEvent(Event selectedEvent) {
        this.currentSelectedEvent = selectedEvent;
    }

    /**
     * Beendet die Kundenanmeldung und entfernt sitzungsbezogene Buchungsdaten.
     */
    public void logoutUser() {
        this.loggedInUser = null;
        this.lastBookingInfoMessage = null;
        this.lastReceipt = null;
        this.lastBookedTickets.clear();
    }

    /**
     * Hinterlegt einen erfolgreich angemeldeten oder registrierten Kunden.
     *
     * @param user anzumeldender Kunde
     */
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    /**
     * Lässt die eingegebenen Kundenzugangsdaten vom Repository prüfen.
     *
     * @param email eingegebene E-Mail-Adresse
     * @param password eingegebenes Klartextpasswort
     * @return gefundener Kunde oder {@code null} bei ungültigen Zugangsdaten
     */
    public User validateUserCredentials(String email, String password) {
        return userRepo.validateUser(email, password);
    }

    /**
     * Registriert einen neuen Kunden über das Benutzer-Repository.
     *
     * @param user vollständig angelegtes Benutzerobjekt
     * @return {@code true}, wenn die Registrierung gespeichert werden konnte
     */
    public boolean registerUser(User user) {
        return userRepo.registerUser(user);
    }

    /**
     * Setzt nach erfolgreicher Anmeldung den unterbrochenen Benutzerfluss fort
     * oder navigiert ohne vorgemerkte Aktion zum Hauptmenü.
     */
    public void runPostLoginActionOrGoMainMenu() {
        if (this.postLoginAction != null) {
            Runnable action = this.postLoginAction;
            this.postLoginAction = null;
            action.run();
        } else {
            screenManager.navigateTo(ScreenManager.Screen.MAIN_MENU);
        }
    }

    /** Verwirft eine nach der Anmeldung vorgemerkte Fortsetzungsaktion. */
    public void clearPostLoginAction() {
        this.postLoginAction = null;
    }

    /**
     * Leitet eine Navigationsanforderung an den zentralen ScreenManager weiter.
     *
     * @param screen anzuzeigendes Navigationsziel
     */
    public void navigateTo(ScreenManager.Screen screen) {
        screenManager.navigateTo(screen);
    }

    /**
     * Validiert die Kundentypen des Warenkorbs und startet dessen verbindliche
     * Buchung nach erfolgter Anmeldung.
     *
     * @param chosenTypes pro Warenkorbposition ausgewählte Kundentypen
     */
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

    /**
     * Ermittelt alle Quittungen des aktuell angemeldeten Kunden.
     *
     * @return Quittungen des Kunden oder eine leere Liste ohne Anmeldung
     */
    public List<Receipt> getReceiptsForLoggedInUser() {
        if (loggedInUser == null) {
            return new ArrayList<>();
        }
        return receiptRepo.findByUserEmail(loggedInUser.getEmail());
    }

    /** Öffnet die auswählbare Historie der eigenen Buchungsquittungen. */
    public void openReceiptHistoryWindow() {
        List<Receipt> receipts = getReceiptsForLoggedInUser();
        ReceiptHistoryDialog.show(primaryStage, receipts, this::openReceiptWindow);
    }

    /**
     * Öffnet die Detailansicht eines Tickets in einem Dialogfenster.
     *
     * @param ticket anzuzeigendes Ticket
     */
    public void openTicketWindow(Ticket ticket) {
        TicketDialog.show(primaryStage, ticket);
    }

    /**
     * Öffnet die Detailansicht einer Quittung in einem Dialogfenster.
     *
     * @param receipt anzuzeigende Quittung
     */
    public void openReceiptWindow(Receipt receipt) {
        ReceiptDialog.show(primaryStage, receipt);
    }

    /**
     * Prüft, ob eine Zusammenfassung der letzten Buchung vorliegt.
     *
     * @return {@code true}, wenn Buchungsinformationen angezeigt werden können
     */
    public boolean hasLastBookingInfo() {
        return lastBookingInfoMessage != null && !lastBookingInfoMessage.trim().isEmpty();
    }

    /**
     * Liefert die textuelle Zusammenfassung der letzten Buchung.
     *
     * @return Buchungszusammenfassung oder {@code null}
     */
    public String getLastBookingInfoMessage() {
        return lastBookingInfoMessage;
    }

    /**
     * Liefert eine Kopie der zuletzt gebuchten Tickets.
     *
     * @return unabhängige Liste der zuletzt gebuchten Tickets
     */
    public List<Ticket> getLastBookedTickets() {
        return new ArrayList<>(lastBookedTickets);
    }

    /** Entfernt die temporären Informationen der letzten Buchung. */
    public void clearLastBookingInfo() {
        this.lastBookingInfoMessage = null;
        this.lastReceipt = null;
        this.lastBookedTickets.clear();
    }

    /** Öffnet die zuletzt erzeugte Quittung oder informiert über deren Fehlen. */
    public void openLastReceiptWindow() {
        if (lastReceipt != null) {
            openReceiptWindow(lastReceipt);
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Keine Quittung", "Es ist keine aktuelle Quittung vorhanden.");
        }
    }

    /** Öffnet das erste zuletzt gebuchte Ticket oder informiert über dessen Fehlen. */
    public void openLastBookedEventWindow() {
        if (!lastBookedTickets.isEmpty()) {
            openTicketWindow(lastBookedTickets.get(0));
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Kein Ticket", "Es ist kein aktuelles Ticket vorhanden.");
        }
    }

    /** Zeigt nach einer Anmeldeprüfung die persönliche Ticketübersicht an. */
    private void showMyTicketsView() {
        ensureLoggedIn(() -> {
            MyTicketsScreen myTicketsScreen = new MyTicketsScreen(this, bookingService, userRepo);
            switchScene(myTicketsScreen.buildScene());
        });
    }

    /** Zeigt die Kundenanmeldung an. */
    private void showLoginView() {
        LoginScreen loginScreen = new LoginScreen(this);
        switchScene(loginScreen.buildScene());
    }

    /** Zeigt die Registrierung für neue Kunden an. */
    private void showRegisterView() {
        RegisterScreen registerScreen = new RegisterScreen(this);
        switchScene(registerScreen.buildScene());
    }

    /** Zeigt die Sitzplatzauswahl für den zuvor gewählten Sitzbereich an. */
    public void showSeatSelection() {
        SeatSelectionScreen seatSelectionScreen = new SeatSelectionScreen(this);
        switchScene(seatSelectionScreen.buildScene());
    }

    /** Zeigt die Mengenauswahl für den zuvor gewählten Stehbereich an. */
    public void showStandingAreaSelection() {
        StandingAreaSelectionScreen standingAreaSelectionScreen = new StandingAreaSelectionScreen(this);
        switchScene(standingAreaSelectionScreen.buildScene());
    }

    /** Zeigt den Warenkorb des laufenden Buchungsvorgangs an. */
    private void showCartView() {
        CartScreen cartScreen = new CartScreen(this);
        switchScene(cartScreen.buildScene());
    }

    /** Zeigt die Eventverwaltung für angemeldete Mitarbeitende an. */
    private void showEmployeeEventsView() {
        ui.screens.EmployeeEventScreen employeeScreen = new ui.screens.EmployeeEventScreen(this, eventRepo);
        switchScene(employeeScreen.buildScene());
    }

    /** Zeigt die separate Anmeldung für Mitarbeitende an. */
    private void showEmployeeLoginView() {
        ui.screens.EmployeeLoginScreen employeeLoginScreen = new ui.screens.EmployeeLoginScreen(this);
        switchScene(employeeLoginScreen.buildScene());
    }

    /**
     * Prüft die Zugangsdaten eines Mitarbeitenden.
     *
     * @param username eingegebener Benutzername
     * @param password eingegebenes Passwort
     * @return {@code true}, wenn die Zugangsdaten gültig sind
     */
    public boolean validateEmployeeCredentials(String username, String password) {
        return employeeRepo.validateEmployee(username, password);
    }

    /**
     * Bucht alle ausgewählten Warenkorbpositionen, ordnet die erzeugten Tickets
     * dem angemeldeten Kunden zu und erstellt die zugehörige Quittung.
     *
     * @param chosenItems zu buchende Warenkorbpositionen
     * @param chosenTypes Kundentyp je Position für die Preisberechnung
     */
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
                successMessage.append(String.format("- %s | %s | (%s) - %.2f EUR\n",
                        t.getSection() != null ? t.getSection().getName() : "Bereich",
                        t.getSeatInfo(),
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

    /**
     * Erstellt und speichert die Quittung einer erfolgreich abgeschlossenen
     * Buchung.
     *
     * @param firstName Vorname des Käufers
     * @param lastName Nachname des Käufers
     * @param userEmail E-Mail-Adresse zur späteren Zuordnung
     * @param totalAmount Gesamtbetrag der Buchung
     * @param generatedTickets erzeugte Tickets
     * @return gespeicherte Quittung
     */
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

    /**
     * Aktualisiert den sichtbaren Text zur aktuellen Platz- oder Bereichsauswahl.
     *
     * @param text neuer Statustext
     */
    public void updateSelectionLabel(String text) {
        selectionStatusLabel.setText(text);
    }

    /**
     * Zeigt einen an das Hauptfenster gebundenen Standarddialog an.
     *
     * @param type Art des Hinweises
     * @param title Fenstertitel
     * @param content Nachricht für den Benutzer
     */
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

    /** Zeigt den grafischen Hallenplan zur Auswahl eines Bereichs an. */
    public void showGraphicSectionSelection() {
        GraphicSectionSelectionScreen graphicSectionSelectionScreen = new GraphicSectionSelectionScreen(this);
        switchScene(graphicSectionSelectionScreen.buildScene());
    }

    /**
     * Sucht innerhalb der aktuell ausgewählten Veranstaltung einen Bereich nach
     * seinem Namen.
     *
     * @param name gesuchter Bereichsname
     * @return gefundener Bereich oder {@code null}
     */
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

    /**
     * Führt eine geschützte Aktion sofort aus oder merkt sie vor und leitet einen
     * nicht angemeldeten Benutzer zur Anmeldung weiter.
     *
     * @param onLoggedInAction nach erfolgreicher Anmeldung auszuführende Aktion
     */
    public void ensureLoggedIn(Runnable onLoggedInAction) {
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

    /**
     * Startpunkt beim direkten Ausführen der Anwendung.
     *
     * @param args optionale Kommandozeilenargumente für JavaFX
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Übersetzt die in der GUI verwendete Bezeichnung in den fachlichen
     * Kundentyp.
     *
     * @param typeLabel Beschriftung aus der Kundentyp-Auswahl
     * @return passender Kundentyp, standardmäßig {@link CustomerType#STANDARD}
     */
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

    /**
     * Tauscht den Inhalt des Hauptfensters aus. Nach dem ersten Szenenwechsel wird
     * nur noch der Wurzelknoten ersetzt, damit Fenstergröße und Zustand erhalten
     * bleiben.
     *
     * @param newScene neu erzeugte Szene des Ziel-Screens
     */
    private void switchScene(Scene newScene) {
        if (primaryStage.getScene() == null) {
            // Beim ersten Aufruf muss zunächst eine Szene am Fenster gesetzt werden.
            primaryStage.setScene(newScene);
        } else {
            // Bei späteren Wechseln bleibt die bestehende Szene mitsamt Fensterzustand erhalten.
            javafx.scene.Parent newRoot = newScene.getRoot();

            // Der Platzhalter löst den neuen Wurzelknoten von seiner bisherigen Szene.
            newScene.setRoot(new javafx.scene.layout.Region());

            // Anschließend kann der gelöste Wurzelknoten in die aktive Szene wechseln.
            primaryStage.getScene().setRoot(newRoot);
        }
        primaryStage.show();
    }
}
