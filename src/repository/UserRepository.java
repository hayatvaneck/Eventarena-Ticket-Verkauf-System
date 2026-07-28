package repository;

import domain.Ticket;
import domain.User;
import domain.PasswordService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse UserRepository verwaltet Benutzerdaten, Registrierung und Validierung von Logins.
 */
public class UserRepository {
    private final List<User> users;
    private static final String FILE_PATH = "data/users.csv";

    public UserRepository() {
        this.users = new ArrayList<>();

        loadUsersFromFile();

        if (findUserByEmail("max@mustermann.de") == null) {
            String passwordHash = PasswordService.hashPassword("passwort");
            User testUser = new User("Max", "Mustermann", "max@mustermann.de", passwordHash);
            users.add(testUser);
            saveUsersToFile();
        }
    }

    public boolean registerUser(User newUser) {
        if (findUserByEmail(newUser.getEmail()) != null) {
            return false;
        }
        users.add(newUser);
        saveUsersToFile();
        return true;
    }

    public User validateUser(String email, String password) {
        User user = findUserByEmail(email);

        if (user == null || !PasswordService.verifyPassword(password, user.getPasswordHash())) {
            return null;
        }

        user.getPurchasedTickets().clear();

        List<Ticket> allTickets = TicketRepository.getInstance().findAll();

        for (Ticket ticket : allTickets) {
            if (ticket.getUserEmail() != null && ticket.getUserEmail().equalsIgnoreCase(user.getEmail())) {
                user.addTicket(ticket);
            }
        }

        return user;
    }

    public User findUserByEmail(String email) {
        if (email == null) {
            return null;
        }

        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email.trim())) {
                return user;
            }
        }
        return null;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public void saveUsersToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (User user : users) {
                writer.println(user.getFirstName() + ";" +
                               user.getLastName() + ";" +
                               user.getEmail() + ";" +
                               user.getPasswordHash());
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern der Benutzerdaten: " + e.getMessage());
        }
    }

    public void loadUsersFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(";");
                if (parts.length == 4) {
                    String firstName = parts[0];
                    String lastName = parts[1];
                    String email = parts[2];
                    String passwordHash = parts[3];

                    User user = new User(firstName, lastName, email, passwordHash);

                    if (findUserByEmail(user.getEmail()) == null) {
                        users.add(user);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Benutzerdaten: " + e.getMessage());
        }
    }
}



