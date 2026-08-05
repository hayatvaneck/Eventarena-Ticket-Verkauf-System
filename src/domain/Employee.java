package domain;

public class Employee {
    private String username;
    private String passwordHash;

    public Employee(String username, String passwordHash) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Benutzername darf nicht leer sein.");
        }
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}