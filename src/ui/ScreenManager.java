package ui;

import java.util.EnumMap;
import java.util.Map;

/**
 * Die Klasse ScreenManager verwaltet die Zuordnung von Screens zu Aktionen und steuert die Navigation.
 */

public class ScreenManager {

    public enum Screen {
        MAIN_MENU,
        BOOKING_CONFIRMATION,
        MY_TICKETS,
        LOGIN,
        REGISTER,
        GRAPHIC_SECTION_SELECTION,
        SEAT_SELECTION,
        STANDING_AREA_SELECTION,
        CART
    }

    //Map-Array, das jeden Screen mit einer zugehörigen Aktion verknüpft.
    private final Map<Screen, Runnable> routes = new EnumMap<>(Screen.class);

    //Methode zum mappen. Jeder Screen erhält eine zugehörige Aktion, die ausgeführt wird, wenn zu diesem Screen navigiert wird.
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

    //Navigiert zu einem registrierten Screen und führt die zugehörige Aktion aus.
    public void navigateTo(Screen screen) {
        Runnable action = routes.get(screen);
        if (action == null) {
            throw new IllegalStateException("Kein Screen registriert für: " + screen);
        }

        action.run();
    }
}


