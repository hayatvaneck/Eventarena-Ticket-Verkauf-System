package domain;

/**
 * Die Klasse Customer repräsentiert eine Ticketinhaberin oder einen Ticketinhaber mit Typ und Stammdaten.
 */

public class Customer {
    /** Eindeutige fachliche Kennung des Ticketinhabers. */
    private final long id;
    /** Vorname des Ticketinhabers. */
    private final String firstName;
    /** Nachname des Ticketinhabers. */
    private final String lastName;
    /** Kundengruppe, aus der sich ein möglicher Rabatt ableitet. */
    private final CustomerType customerType;

    /**
     * Erstellt einen unveränderlichen Ticketinhaber und prüft die Stammdaten.
     * Ein nicht angegebener Kundentyp wird als {@link CustomerType#STANDARD}
     * behandelt.
     *
     * @param id positive Kunden-ID
     * @param firstName nicht leerer Vorname
     * @param lastName nicht leerer Nachname
     * @param customerType Kundengruppe oder {@code null} für Standard
     * @throws IllegalArgumentException wenn ID oder Name ungültig sind
     */
    public Customer(long id, String firstName, String lastName, CustomerType customerType) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID muss größer als 0 sein.");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("Vorname darf nicht leer sein.");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nachname darf nicht leer sein.");
        }

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.customerType = (customerType != null) ? customerType : CustomerType.STANDARD;
    }

    /** @return eindeutige Kunden-ID */
    public long getId() {
        return id;
    }

    /** @return Vorname des Ticketinhabers */
    public String getFirstName() {
        return firstName;
    }

    /** @return Nachname des Ticketinhabers */
    public String getLastName() {
        return lastName;
    }

    /** @return zugeordnete Kundengruppe */
    public CustomerType getCustomerType() {
        return customerType;
    }

    /**
     * Setzt Vor- und Nachname zu einer lesbaren Anzeige zusammen.
     *
     * @return vollständiger Name des Ticketinhabers
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}


