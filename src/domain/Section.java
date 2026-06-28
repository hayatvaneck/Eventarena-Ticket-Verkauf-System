package domain;
import exceptions.SeatAlreadyBookedException;

public abstract class Section {
    private String name;
    private double priceFactor;

    public Section(String name, double priceFactor) {
        this.name = name;
        this.priceFactor = priceFactor;
    }

    public abstract boolean bookNextAvailableTicket() throws SeatAlreadyBookedException;
    public abstract int getAvailableSeats();
    public abstract void printLayout();

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getPriceFactor() {
        return priceFactor;
    }
    public void setPriceFactor(double priceFactor) {
        this.priceFactor = priceFactor;
    }
    
}
