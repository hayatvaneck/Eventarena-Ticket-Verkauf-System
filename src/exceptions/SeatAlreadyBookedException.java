package exceptions;

/**
 * Die Klasse SeatAlreadyBookedException signalisiert, dass ein Platz bereits belegt oder nicht mehr buchbar ist.

 */

public class SeatAlreadyBookedException extends Exception {
    
    public SeatAlreadyBookedException(String message) {
        super(message);
    }
    
}



