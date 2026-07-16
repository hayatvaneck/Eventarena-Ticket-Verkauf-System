package repository;

import domain.User;
import java.util.ArrayList;
import java.util.List;

import java.io.*;

public class UserRepository {
    private final List<User> users;
    private static final String FILE_PATH = "users.csv";

    public UserRepository() {
        this.users = new ArrayList<>();

        // Bereits registrierte Nutzer laden
        loadUsersFromFile();

        // Test-User anlegen
        if (findUserByEmail("max@mustermann.de") == null) {
            User testUser = new User("Max", "Mustermann", "max@mustermann.de", "passwort");
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
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
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

    // Gibt eine Kopie der aktuellen Benutzerliste zurück
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    // Persistenz Methoden
    public void saveUsersToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (User user : users) {
                writer.println(user.getFirstName() + ";" +
                               user.getLastName() + ";" +
                               user.getEmail() + ";" +
                               user.getPassword());
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
                    String password = parts[3];

                    User user = new User(firstName, lastName, email, password);

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
