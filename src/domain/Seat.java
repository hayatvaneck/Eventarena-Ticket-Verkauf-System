package domain;
import exceptions.SeatAlreadyBookedException;

/**
 * Die Klasse Seat repräsentiert einen einzelnen Sitzplatz mit Position, Bereich und Buchungsstatus.

 */

public class Seat {
    /** Einsbasierte Reihennummer für Anzeige und Buchung. */
    private final int rowNumber;
    /** Einsbasierte Platznummer innerhalb der Reihe. */
    private final int seatNumber;
    /** Kennzeichnet, ob der Platz bereits verbindlich gebucht wurde. */
    private boolean isBooked;
    /** Bereich, zu dem der Platz gehört. */
    private Section section;

    /**
     * Erstellt einen zunächst freien Platz ohne Bereichszuordnung.
     *
     * @param rowNumber Reihennummer
     * @param seatNumber Platznummer
     */
    public Seat(int rowNumber, int seatNumber) {
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
        this.isBooked = false;
    }

    /**
     * Erstellt einen freien Platz und ordnet ihn direkt einem Bereich zu.
     *
     * @param rowNumber Reihennummer
     * @param seatNumber Platznummer
     * @param section zugehöriger Bereich
     */
    public Seat(int rowNumber, int seatNumber, Section section) {
        this(rowNumber, seatNumber);
        this.section = section;
    }

    /**
     * Bucht den Platz, sofern er noch frei ist.
     *
     * @throws SeatAlreadyBookedException wenn der Platz bereits gebucht ist
     */
    public void book() throws SeatAlreadyBookedException {
        if (this.isBooked) {
            throw new SeatAlreadyBookedException("Reihe " + rowNumber + ", Platz " + seatNumber + " ist bereits ausgebucht!");
        }
        this.isBooked = true;
    }

    /** Gibt den Platz nach einer Stornierung wieder frei. */
    public void release() {
        this.isBooked = false;
    }

    /** @return {@code true}, wenn der Platz bereits gebucht ist */
    public boolean isBooked() {
        return isBooked;
    }
    /** @return einsbasierte Reihennummer */
    public int getRowNumber() {
        return rowNumber;
    }
    /** @return einsbasierte Platznummer */
    public int getSeatNumber() {
        return seatNumber;
    }
    /** @return zugehöriger Bereich oder {@code null} */
    public Section getSection() {
        return section;
    }
    /**
     * Ordnet den Platz einem Bereich zu, etwa bei technischen Stehplatzhaltern.
     *
     * @param section neuer zugehöriger Bereich
     */
    public void setSection(Section section) {
        this.section = section;
    }
}



