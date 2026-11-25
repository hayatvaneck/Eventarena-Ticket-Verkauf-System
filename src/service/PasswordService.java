package service;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Kapselt die sichere Erzeugung und Prüfung von BCrypt-Passwort-Hashes.
 * Der Service besitzt keinen veränderlichen Zustand.
 */
public class PasswordService {

    /** BCrypt-Kostenfaktor und damit Rechenaufwand einer Hash-Operation. */
    private static final int WORK_FACTOR = 10;

    /**
     * Erzeugt aus einem Klartextpasswort einen gesalzenen BCrypt-Hash.
     *
     * @param plainPassword zu hashendes Klartextpasswort
     * @return speicherbarer BCrypt-Hash
     * @throws IllegalArgumentException wenn das Passwort leer ist
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Passwort darf nicht leer sein.");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    /**
     * Vergleicht ein Klartextpasswort mit einem gespeicherten BCrypt-Hash.
     *
     * @param plainPassword eingegebenes Klartextpasswort
     * @param passwordHash gespeicherter Hash
     * @return {@code true}, wenn beide Werte zusammengehören
     */
    public static boolean verifyPassword(String plainPassword, String passwordHash) {
        if (plainPassword == null || passwordHash == null || passwordHash.isBlank()) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, passwordHash);
    }
}
