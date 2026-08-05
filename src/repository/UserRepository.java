package repository;

import domain.Ticket;
import domain.User;
import service.PasswordService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Die Klasse UserRepository verwaltet Benutzerdaten, Registrierung und Validierung von Logins.
 */
public class UserRepository {
    /** Im Arbeitsspeicher verwaltete Benutzerkonten. */
    private final List<User> users;
    /** Relativer Pfad zur Benutzerdatei. */
    private static final String FILE_PATH = "data/users.csv";

    /** Lädt Benutzerkonten und legt bei Bedarf das vorgesehene Demokonto an. */
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

    /**
     * Registriert ein Konto, sofern seine E-Mail noch nicht vergeben ist.
     *
     * @param newUser bereits validiertes und mit Passwort-Hash versehenes Konto
     * @return {@code true}, wenn das Konto gespeichert wurde
     */
    public boolean registerUser(User newUser) {
        if (findUserByEmail(newUser.getEmail()) != null) {
            return false;
        }
        users.add(newUser);
        saveUsersToFile();
        return true;
    }

    /**
     * Authentifiziert einen Benutzer und stellt anschließend seine Ticketliste wieder her.
     *
     * @param email eingegebene E-Mail-Adresse
     * @param password eingegebenes Klartextpasswort
     * @return angemeldeter Benutzer oder {@code null} bei ungültigen Daten
     */
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

    /**
     * Sucht ein Konto ohne Beachtung der Groß- und Kleinschreibung.
     *
     * @param email gesuchte E-Mail-Adresse
     * @return gefundenes Konto oder {@code null}
     */
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

    /** @return defensive Kopie aller registrierten Benutzer */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    /** Schreibt alle Kontostammdaten und Passwort-Hashes in die CSV-Datei. */
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

    /** Liest alle gültigen und noch nicht vorhandenen Konten aus der CSV-Datei. */
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



