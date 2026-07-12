package domain;

public abstract class Section {
    private final String name;
    private final double priceFactor;

    public Section(String name, double priceFactor) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name darf nicht leer sein.");
        }
        if (priceFactor < 0) {
            throw new IllegalArgumentException("Preisfaktor muss größer als 0 sein.");
        }

        this.name = name;
        this.priceFactor = priceFactor;
    }

    public abstract int getAvailableSeats();
    public abstract void printLayout();

    public String getName() {
        return name;
    }

    public double getPriceFactor() {
        return priceFactor;
    }
}