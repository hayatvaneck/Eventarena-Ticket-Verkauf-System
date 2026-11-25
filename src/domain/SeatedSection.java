package domain;
import exceptions.SeatAlreadyBookedException;

/**
 * Die Klasse SeatedSection verwaltet einen Bereich mit festen Sitzreihen und konkreten Sitzplätzen.

 */

public class SeatedSection extends Section {
    /** Zweidimensionale, nach Reihe und Platznummer geordnete Sitzmatrix. */
    private final Seat[][] seats;

    /**
     * Erstellt einen regelmäßig aufgebauten Sitzbereich und alle zugehörigen Sitze.
     *
     * @param name Name des Bereichs
     * @param priceFactor Preisfaktor relativ zum Event
     * @param rows Anzahl der Sitzreihen
     * @param seatsPerRow Anzahl der Plätze je Reihe
     */
    public SeatedSection(String name, double priceFactor, int rows, int seatsPerRow) {
        super(name, priceFactor);
        this.seats = new Seat[rows][seatsPerRow];
        for (int r = 0; r < rows; r++) {
            for (int s = 0; s < seatsPerRow; s++) {
                this.seats[r][s] = new Seat(r+1, s+1, this);
            }
        }
    }

    /**
     * Durchsucht die Matrix zeilenweise und bucht den ersten freien Sitz.
     *
     * @return {@code true}, wenn ein freier Sitz gefunden wurde
     * @throws SeatAlreadyBookedException wenn sich der Zustand während der Buchung ändert
     */
    @Override
    public boolean bookNextAvailableTicket() throws SeatAlreadyBookedException {
        for (int r = 0; r < seats.length; r++) {
            for (int s = 0; s < seats[r].length; s++) {
                Seat currentSeat = seats[r][s];
                if (!currentSeat.isBooked()) {
                    currentSeat.book();
                    return true;
                }
            }
        }
        return false; // Keine verfügbaren Plätze mehr
    }

    /** @return Anzahl aller noch freien Sitze in der Matrix */
    @Override
    public int getAvailableSeats() {
        int availableSeats = 0;
        for (int r = 0; r < seats.length; r++) {
            for (int s = 0; s < seats[r].length; s++) {
                if (!seats[r][s].isBooked()) {
                    availableSeats++;
                }
            }
        }
        return availableSeats;
    }

    /** Gibt die Sitzmatrix mit Frei-/Belegt-Kennzeichnung auf der Konsole aus. */
    @Override
    public void printLayout() {
        System.out.println("\nSitzplan für Block: " + getName());
        System.out.println("[O] = frei, [X] = besetzt\n");
        for(int r = 0; r < seats.length; r++) {
            System.out.printf("Reihe %02d: ", r + 1);
            for(int s = 0; s < seats[r].length; s++) {
                if (seats[r][s].isBooked()) {
                    System.out.print("[X] ");
                } else {
                    System.out.print("[O] ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Gibt einen konkreten Sitz frei, sofern die Position existiert.
     *
     * @param rowNumber einsbasierte Reihennummer
     * @param seatNumber einsbasierte Platznummer
     */
    public void releaseSeat (int rowNumber, int seatNumber) {
        Seat seat = getSeat(rowNumber, seatNumber);
        if (seat != null) {
            seat.release();
        }
    }

    /**
     * Löst eine externe einsbasierte Position in den passenden Sitz auf.
     *
     * @param row einsbasierte Reihennummer
     * @param seatNumber einsbasierte Platznummer
     * @return Sitz an der Position oder {@code null} bei ungültigen Werten
     */
    public Seat getSeat(int row, int seatNumber) {
        if (row > 0 && row <= seats.length && seatNumber > 0 && seatNumber <= seats[0].length) {
            return seats[row - 1][seatNumber - 1];
        }
        return null;
    }

    /** @return Anzahl der Sitzreihen */
    public int getRowCount() {
        return this.seats.length;
    }

    /** @return Anzahl der Plätze je Reihe oder {@code 0} bei leerer Matrix */
    public int getSeatsPerRow() {
        if (this.seats.length > 0) {
            return this.seats[0].length;
        }
        return 0;
    }
    
}



