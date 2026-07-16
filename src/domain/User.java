package domain;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String password; // hier nicht verschlüsselt
    private List<Ticket> purchasedTickets = new ArrayList<>(); // Liste der gekauften Tickets

    public User (String firstName, String lastName, String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("E-Mail darf nicht leer sein.");
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.purchasedTickets = new ArrayList<>();
    }

    // Getter Methoden
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }
    public List<Ticket> getPurchasedTickets() {
        return purchasedTickets;
    }

    public void addTicket(Ticket ticket) {
        this.purchasedTickets.add(ticket);
    }

    
    
    
}
