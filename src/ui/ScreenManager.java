package ui;

import java.util.EnumMap;
import java.util.Map;

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

    private final Map<Screen, Runnable> routes = new EnumMap<>(Screen.class);

    public ScreenManager register(Screen screen, Runnable action) {
        if (screen == null) {
            throw new IllegalArgumentException("screen darf nicht null sein");
        }
        if (action == null) {
            throw new IllegalArgumentException("action darf nicht null sein");
        }

        routes.put(screen, action);
        return this;
    }

    public void navigateTo(Screen screen) {
        Runnable action = routes.get(screen);
        if (action == null) {
            throw new IllegalStateException("Kein Screen registriert fuer: " + screen);
        }

        action.run();
    }
}
