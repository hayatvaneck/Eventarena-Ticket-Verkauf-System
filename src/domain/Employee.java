package domain;

/** Repräsentiert ein Mitarbeiterkonto für den geschützten Verwaltungsbereich. */
public class Employee {
    /** Eindeutiger Benutzername für die Mitarbeiteranmeldung. */
    private String username;
    /** Mit BCrypt erzeugter Hash des Mitarbeiterpassworts. */
    private String passwordHash;

    /**
     * Erstellt ein Mitarbeiterkonto mit bereits gehashtem Passwort.
     *
     * @param username nicht leerer Anmeldename
     * @param passwordHash gespeicherter Passwort-Hash
     * @throws IllegalArgumentException wenn der Benutzername fehlt
     */
    public Employee(String username, String passwordHash) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Benutzername darf nicht leer sein.");
        }
        this.username = username;
        this.passwordHash = passwordHash;
    }

    /** @return Benutzername des Mitarbeiterkontos */
    public String getUsername() {
        return username;
    }

    /** @return gespeicherter Passwort-Hash */
    public String getPasswordHash() {
        return passwordHash;
    }
}
