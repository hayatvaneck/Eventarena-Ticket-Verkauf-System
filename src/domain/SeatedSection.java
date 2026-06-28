package domain;
import exceptions.SeatAlreadyBookedException;

public class SeatedSection extends Section {
    private final Seat[][] seats;

    public SeatedSection(String name, double priceFactor, int rows, int seatsPerRow) {
        super(name, priceFactor);
        this.seats = new Seat[rows][seatsPerRow];
        for (int r = 0; r < rows; r++) {
            for (int s = 0; s < seatsPerRow; s++) {
                this.seats[r][s] = new Seat(r+1, s+1);
            }
        }
    }

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

    public Seat getSeat(int row, int seatNumber) {
        if (row > 0 && row <= seats.length && seatNumber > 0 && seatNumber <= seats[0].length) {
            return seats[row - 1][seatNumber - 1];
        }
        return null;
    }

    public int getRowCount() {
        return this.seats.length;
    }

    public int getSeatsPerRow() {
        if (this.seats.length > 0) {
            return this.seats[0].length;
        }
        return 0;
    }
    
}
