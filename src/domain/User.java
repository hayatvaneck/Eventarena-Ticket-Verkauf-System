package domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse User repräsentiert ein Benutzerkonto mit Login-Daten und gekauften Tickets.

 */


public class User {
    /** Vorname des registrierten Benutzers. */
    private String firstName;
    /** Nachname des registrierten Benutzers. */
    private String lastName;
    /** Eindeutige E-Mail-Adresse für Anmeldung und Ticketzuordnung. */
    private String email;
    /** Mit BCrypt erzeugter Hash des Benutzerpassworts. */
    private String passwordHash;
    /** Derzeit aktive, dem Konto zugeordnete Tickets. */
    private List<Ticket> purchasedTickets = new ArrayList<>();

    /**
     * Erstellt ein Benutzerkonto mit bereits gehashtem Passwort.
     *
     * @param firstName Vorname
     * @param lastName Nachname
     * @param email nicht leere Anmelde-E-Mail
     * @param passwordHash gespeicherter Passwort-Hash
     * @throws IllegalArgumentException wenn die E-Mail fehlt
     */
    public User(String firstName, String lastName, String email, String passwordHash) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("E-Mail darf nicht leer sein.");
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    /** @return gespeicherter Passwort-Hash */
    public String getPasswordHash() {
        return passwordHash;
    }

    /** @return Vorname des Benutzers */
    public String getFirstName() {
        return firstName;
    }
    /** @return Nachname des Benutzers */
    public String getLastName() {
        return lastName;
    
    }
    /** @return E-Mail-Adresse des Kontos */
    public String getEmail() {
        return email;
    }
    /**
     * Liefert die verwaltete Ticketliste. Das Repository verwendet sie beim
     * Wiederherstellen der Benutzer-Ticket-Zuordnung.
     *
     * @return aktive Tickets des Benutzers
     */
    public List<Ticket> getPurchasedTickets() {
        return purchasedTickets;
    }

    /**
     * Ordnet dem Benutzer ein neu gebuchtes Ticket zu.
     *
     * @param ticket hinzuzufügendes Ticket
     */
    public void addTicket(Ticket ticket) {
        this.purchasedTickets.add(ticket);
    }

    /**
     * Entfernt bei einer Stornierung das Ticket mit derselben Ticket-ID.
     *
     * @param ticket zu entfernendes Ticket
     */
    public void removeTicket(Ticket ticket) {
        if (this.purchasedTickets != null && ticket != null) {
            this.purchasedTickets.removeIf(t -> t.getTicketId().equals(ticket.getTicketId()));
        }
    }

    
    
    
}



