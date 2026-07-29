package repository;

import domain.Employee;
import domain.PasswordService;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {
    private static EmployeeRepository instance;
    private final List<Employee> employees;
    private static final String FILE_PATH = "data/employees.csv";

    private EmployeeRepository() {
        this.employees = new ArrayList<>();
        loadEmployeesFromFile();
        
        // Erstelle Standard-Admin, falls nicht vorhanden
        if (findByUsername("admin") == null) {
            String passwordHash = PasswordService.hashPassword("admin");
            employees.add(new Employee("admin", passwordHash));
            saveEmployeesToFile();
        }
    }

    public static synchronized EmployeeRepository getInstance() {
        if (instance == null) {
            instance = new EmployeeRepository();
        }
        return instance;
    }

    public Employee findByUsername(String username) {
        if (username == null) return null;
        for (Employee emp : employees) {
            if (emp.getUsername().equalsIgnoreCase(username.trim())) {
                return emp;
            }
        }
        return null;
    }

    public boolean validateEmployee(String username, String password) {
        Employee emp = findByUsername(username);
        if (emp == null || !PasswordService.verifyPassword(password, emp.getPasswordHash())) {
            return false;
        }
        return true;
    }

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