package ui;

import java.util.EnumMap;
import java.util.Map;

/**
 * Die Klasse ScreenManager verwaltet die Zuordnung von Screens zu Aktionen und steuert die Navigation.
 */

public class ScreenManager {

    /**
     * Eindeutige Bezeichner aller Ansichten, zwischen denen die Anwendung
     * navigieren kann.
     */
    public enum Screen {
        /** Hauptmenü mit der Eventauswahl. */
        MAIN_MENU,
        /** Zusammenfassung einer abgeschlossenen Buchung. */
        BOOKING_CONFIRMATION,
        /** Übersicht der Tickets des angemeldeten Benutzers. */
        MY_TICKETS,
        /** Anmeldemaske für Kunden. */
        LOGIN,
        /** Registrierungsmaske für neue Kunden. */
        REGISTER,
        /** Grafische Auswahl eines Hallenbereichs. */
        GRAPHIC_SECTION_SELECTION,
        /** Sitzplatzauswahl innerhalb eines Sitzbereichs. */
        SEAT_SELECTION,
        /** Ticketauswahl für einen Stehbereich. */
        STANDING_AREA_SELECTION,
        /** Warenkorb mit den vorgemerkten Tickets. */
        CART,
        /** Verwaltungsansicht für Events. */
        EMPLOYEE_EVENTS,
        /** Anmeldemaske für Mitarbeitende. */
        EMPLOYEE_LOGIN
    }

    /** Ordnet jedem Screen die Aktion zum Erzeugen und Anzeigen seiner Szene zu. */
    private final Map<Screen, Runnable> routes = new EnumMap<>(Screen.class);

    /**
     * Registriert die Aktion, die beim Aufruf eines Screens ausgeführt werden
     * soll.
     *
     * @param screen eindeutiger Bezeichner des Screens
     * @param action auszuführende Navigationsaktion
     * @return diese Instanz für verkettete Registrierungsaufrufe
     * @throws IllegalArgumentException wenn Screen oder Aktion fehlen
     */
    public ScreenManager register(Screen screen, Runnable action) {
        if (screen == null) {
            throw new IllegalArgumentException("Screeneingabe fehlt");
        }
        if (action == null) {
            throw new IllegalArgumentException("Aktionseingabe fehlt");
        }

        routes.put(screen, action);
        return this;
    }

    /**
     * Navigiert zum angegebenen Screen, indem dessen registrierte Aktion
     * ausgeführt wird.
     *
     * @param screen Ziel der Navigation
     * @throws IllegalStateException wenn für das Ziel keine Aktion registriert ist
     */
    public void navigateTo(Screen screen) {
        Runnable action = routes.get(screen);
        if (action == null) {
            throw new IllegalStateException("Kein Screen registriert für: " + screen);
        }

        action.run();
    }
}


