package domain;
import exceptions.SeatAlreadyBookedException;

public class Seat {
    private final int rowNumber;
    private final int seatNumber;
    private boolean isBooked;

    public Seat(int rowNumber, int seatNumber) {
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
        this.isBooked = false;
    }

    public void book() throws SeatAlreadyBookedException {
        if (this.isBooked) {
            throw new SeatAlreadyBookedException("Reihe " + rowNumber + ", Platz " + seatNumber + " ist bereits ausgebucht!");
        }
        this.isBooked = true;
    }

    public void release() {
        this.isBooked = false;
    }

    // ----Getter-----
    public boolean isBooked() {
        return isBooked;
    }
    public int getRowNumber() {
        return rowNumber;
    }
    public int getSeatNumber() {
        return seatNumber;
    }
}
