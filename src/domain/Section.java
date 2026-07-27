package domain;

import exceptions.SeatAlreadyBookedException;

/**
 * Die abstrakte Klasse Section definiert die gemeinsame Basis für alle buchbaren und nicht buchbaren Bereiche.

 */

public abstract class Section {
    private final String name;
    private final double priceFactor;

    public Section(String name, double priceFactor) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name darf nicht leer sein.");
        }
        if (priceFactor < 0) {
            throw new IllegalArgumentException("Preisfaktor darf nicht negativ sein.");
        }

        this.name = name;
        this.priceFactor = priceFactor;
    }

    public abstract boolean bookNextAvailableTicket() throws SeatAlreadyBookedException;
    public abstract int getAvailableSeats();
    public abstract void printLayout();

    public String getName() {
        return name;
    }

    public double getPriceFactor() {
        return priceFactor;
    }
}


