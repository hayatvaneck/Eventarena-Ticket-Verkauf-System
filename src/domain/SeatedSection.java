package domain;

import exceptions.SeatAlreadyBookedException;

public class SeatedSection extends Section {
    private final Seat[][] seats;

    public SeatedSection(String name, double priceFactor, int rows, int seatsPerRow) {
        super(name, priceFactor);

        if (rows <= 0) {
            throw new IllegalArgumentException("Anzahl der Reihen muss größer als 0 sein.");
        }
        if (seatsPerRow <= 0) {
            throw new IllegalArgumentException("Anzahl der Sitzplätze pro Reihe muss größer als 0 sein.");
        }

        this.seats = new Seat[rows][seatsPerRow];

        for (int row = 0; row < rows; row++) {
            for (int seat = 0; seat < seatsPerRow; seat++) {
                this.seats[row][seat] = new Seat(row + 1, seat + 1);
            }
        }
    }

    @Override
    public int getAvailableSeats() {
        int availableSeats = 0;

        for (int row = 0; row < seats.length; row++) {
            for (int seat = 0; seat < seats[row].length; seat++) {
                if (!seats[row][seat].isBooked()) {
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

        for (int row = 0; row < seats.length; row++) {
            System.out.printf("Reihe %02d: ", row + 1);

            for (int seat = 0; seat < seats[row].length; seat++) {
                if (seats[row][seat].isBooked()) {
                    System.out.print("[X] ");
                } else {
                    System.out.print("[O] ");
                }
            }

            System.out.println();
        }
    }

    public Seat getSeat(int rowNumber, int seatNumber) {
        if (rowNumber <= 0 || rowNumber > seats.length) {
            throw new IllegalArgumentException("Ungültige Reihennummer: " + rowNumber);
        }
        if (seatNumber <= 0 || seatNumber > seats[rowNumber - 1].length) {
            throw new IllegalArgumentException("Ungültige Sitznummer: " + seatNumber);
        }

        return seats[rowNumber - 1][seatNumber - 1];
    }

    public void bookSeat(int rowNumber, int seatNumber) throws SeatAlreadyBookedException {
        Seat seat = getSeat(rowNumber, seatNumber);
        seat.book();
    }
}