package repository;

import domain.Employee;
import service.PasswordService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Verwaltet Mitarbeiterkonten als In-Memory-Liste und persistiert sie in einer
 * lokalen CSV-Datei.
 */
public class EmployeeRepository {
    /** Einzige Repository-Instanz innerhalb der Anwendung. */
    private static EmployeeRepository instance;
    /** Im Arbeitsspeicher verfügbare Mitarbeiterkonten. */
    private final List<Employee> employees;
    /** Relativer Speicherort der Mitarbeiterdaten. */
    private static final String FILE_PATH = "data/employees.csv";

    /** Lädt die Mitarbeiterdaten und legt bei Bedarf das Demo-Administratorkonto an. */
    private EmployeeRepository() {
        this.employees = new ArrayList<>();
        loadEmployeesFromFile();
        
        // Beim Erststart wird ein administrativer Demo-Zugang angelegt.
        if (findByUsername("admin") == null) {
            String passwordHash = PasswordService.hashPassword("admin");
            employees.add(new Employee("admin", passwordHash));
            saveEmployeesToFile();
        }
    }

    /**
     * Liefert die zentrale Repository-Instanz und erzeugt sie beim ersten Zugriff.
     *
     * @return Singleton-Instanz des Mitarbeiter-Repositories
     */
    public static synchronized EmployeeRepository getInstance() {
        if (instance == null) {
            instance = new EmployeeRepository();
        }
        return instance;
    }

    /**
     * Sucht einen Mitarbeiter ohne Beachtung der Groß- und Kleinschreibung.
     *
     * @param username gesuchter Benutzername
     * @return gefundenes Konto oder {@code null}
     */
    public Employee findByUsername(String username) {
        if (username == null) return null;
        for (Employee emp : employees) {
            if (emp.getUsername().equalsIgnoreCase(username.trim())) {
                return emp;
            }
        }
        return null;
    }

    /**
     * Prüft die eingegebenen Anmeldedaten gegen das gespeicherte Konto.
     *
     * @param username Benutzername
     * @param password Klartextpasswort aus dem Loginformular
     * @return {@code true} bei erfolgreicher Passwortprüfung
     */
    public boolean validateEmployee(String username, String password) {
        Employee emp = findByUsername(username);
        if (emp == null || !PasswordService.verifyPassword(password, emp.getPasswordHash())) {
            return false;
        }
        return true;
    }

    /** Liest alle gültigen Mitarbeiterzeilen aus der CSV-Datei ein. */
    private void loadEmployeesFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    employees.add(new Employee(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Laden der Mitarbeiter: " + e.getMessage());
        }
    }

    /** Schreibt den vollständigen Mitarbeiterbestand in die CSV-Datei. */
    private void saveEmployeesToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Employee emp : employees) {
                writer.println(emp.getUsername() + ";" + emp.getPasswordHash());
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern der Mitarbeiter: " + e.getMessage());
        }
    }
}
