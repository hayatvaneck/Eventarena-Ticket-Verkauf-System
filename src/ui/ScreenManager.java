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

    //Map-Array, das jeden Screen mit einer zugehÃ¶rigen Aktion verknÃ¼pft.
    private final Map<Screen, Runnable> routes = new EnumMap<>(Screen.class);

    //Methode zum mappen. Jeder Screen erhÃ¤lt eine zugehÃ¶rige Aktion, die ausgefÃ¼hrt wird, wenn zu diesem Screen navigiert wird.
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

    //Navigiert zu einem registrierten Screen und fÃ¼hrt die zugehÃ¶rige Aktion aus.
    public void navigateTo(Screen screen) {
        Runnable action = routes.get(screen);
        if (action == null) {
            throw new IllegalStateException("Kein Screen registriert fÃ¼r: " + screen);
        }

        action.run();
    }
}


