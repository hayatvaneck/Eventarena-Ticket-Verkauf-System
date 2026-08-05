package service;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordService {

    private static final int WORK_FACTOR = 10;

    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Passwort darf nicht leer sein.");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    public static boolean verifyPassword(String plainPassword, String passwordHash) {
        if (plainPassword == null || passwordHash == null || passwordHash.isBlank()) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, passwordHash);
    }
}