package exceptions;

/**
 * Die Klasse SeatAlreadyBookedException signalisiert, dass ein Platz bereits belegt oder nicht mehr buchbar ist.

 */

public class SeatAlreadyBookedException extends Exception {
    /**
     * Erstellt eine fachliche Exception mit einer verständlichen Beschreibung.
     *
     * @param message Beschreibung der fehlgeschlagenen Platzbuchung
     */
    public SeatAlreadyBookedException(String message) {
        super(message);
    }
    
}



