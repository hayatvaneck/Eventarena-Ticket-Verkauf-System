package ui.screens;

import domain.PasswordService;
import domain.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ui.App;
import ui.ScreenManager;

/**
 * Die Klasse RegisterScreen erfasst neue Benutzerkonten und führt zur Anmeldung
 * zurück.
 * 
 */

public class RegisterScreen extends BaseScreen {

    private final App app;

    public RegisterScreen(App app) {
        this.app = app;
    }

    @Override
    public Scene buildScene() {
        VBox registerRoot = createVBox(15, Pos.CENTER);
        registerRoot.setPadding(new Insets(40));
        registerRoot.setStyle("-fx-background-color: #f5f5f7;");

        Label title = new Label("NEUES KONTO ERSTELLEN");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Vorname");
        firstNameField.setPrefWidth(250);
        firstNameField.setMaxWidth(250);

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Nachname");
        lastNameField.setPrefWidth(250);
        lastNameField.setMaxWidth(250);

        TextField emailField = new TextField();
        emailField.setPromptText("E-Mail-Adresse");
        emailField.setPrefWidth(250);
        emailField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Passwort");
        passwordField.setPrefWidth(250);
        passwordField.setMaxWidth(250);

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Passwort bestätigen");
        confirmPasswordField.setPrefWidth(250);
        confirmPasswordField.setMaxWidth(250);

        VBox hintBox = new VBox(5);
        hintBox.setPadding(new Insets(12));
        hintBox.setStyle(
                "-fx-background-color: #eaf2f8; " +
                        "-fx-border-color: #3498db; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 6px; " +
                        "-fx-background-radius: 6px;");

        Label hintTitle = new Label("Richtlinien für ein sicheres Passwort:");
        hintTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2980b9; -fx-font-size: 12px;");

        String ruleStyle = "-fx-text-fill: #34495e; -fx-font-size: 11px;";
        Label rule1 = new Label("• Mindestens 8 Zeichen");
        Label rule2 = new Label("• Groß- und Kleinbuchstaben");
        Label rule3 = new Label("• Mindestens eine Zahl (0-9)");
        Label rule4 = new Label("• Ein Sonderzeichen (z.B. !@#$)");
        rule1.setStyle(ruleStyle);
        rule2.setStyle(ruleStyle);
        rule3.setStyle(ruleStyle);
        rule4.setStyle(ruleStyle);

        hintBox.getChildren().addAll(hintTitle, rule1, rule2, rule3, rule4);

        GridPane formGrid = new GridPane();
        // formGrid.setVgap(15); // Vertikaler Abstand zwischen den Feldern
        // formGrid.setHgap(20); // Horizontaler Abstand zwischen Textfeld und Info-Box
        formGrid.setAlignment(Pos.CENTER);

        formGrid.add(firstNameField, 0, 0);
        formGrid.add(lastNameField, 0, 1);
        formGrid.add(emailField, 0, 2);
        formGrid.add(passwordField, 0, 3);
        formGrid.add(confirmPasswordField, 0, 4);

        formGrid.add(hintBox, 1, 3, 1, 2);

        Button registerBtn = new Button("Registrieren");
        registerBtn.setDefaultButton(true);
        registerBtn.setStyle(
                "-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: Hand;");
        registerBtn.setPrefWidth(250);

        Label backToLoginLink = new Label("Bereits ein Konto? Zum Login");
        backToLoginLink.setStyle("-fx-text-fill: #2980b9; -fx-cursor: hand;");

        registerBtn.setOnAction(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()
                    || confirmPassword.isEmpty()) {
                app.showAlert(Alert.AlertType.WARNING, "Fehler", "Bitte füllen Sie alle Felder aus.");
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                app.showAlert(Alert.AlertType.WARNING, "Fehler", "Bitte geben Sie eine gültige E-Mail-Adresse ein.");
                return;
            }

            String passwordError = validatePassword(password);
            if (passwordError != null) {
                app.showAlert(Alert.AlertType.WARNING, "Schwaches Passwort", passwordError);
                passwordField.clear();
                confirmPasswordField.clear();
                return;
            }

            if (!password.equals(confirmPassword)) {
                app.showAlert(Alert.AlertType.ERROR, "Fehler", "Die eingegebenen Passwörter stimmen nicht überein!");
                passwordField.clear();
                confirmPasswordField.clear();
                return;
            }

            String passwordHash = PasswordService.hashPassword(password);
            User newUser = new User(firstName, lastName, email, passwordHash);
            boolean success = app.registerUser(newUser);

            if (success) {
                app.showAlert(Alert.AlertType.INFORMATION, "Erfolg", "Registrierung erfolgreich!");
                app.navigateTo(ScreenManager.Screen.LOGIN);
            } else {
                app.showAlert(Alert.AlertType.ERROR, "Fehler", "Diese E-Mail-Adresse ist bereits registriert.");
            }
        });

        backToLoginLink.setOnMouseClicked(e -> app.navigateTo(ScreenManager.Screen.LOGIN));

        registerRoot.getChildren().addAll(
                title,

                firstNameField,
                lastNameField,
                emailField,
                passwordField,
                confirmPasswordField,
                formGrid,
                registerBtn,
                backToLoginLink);

        return createDefaultScene(registerRoot);
    }

    private String validatePassword(String password) {
        if (password.length() < 8) {
            return "Das Passwort muss mindestens 8 Zeichen lang sein.";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Das Passwort muss mindestens einen Großbuchstaben enthalten.";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Das Passwort muss mindestens einen Kleinbuchstaben enthalten.";
        }
        if (!password.matches(".*\\d.*")) {
            return "Das Passwort muss mindestens eine Zahl (0-9) enthalten.";
        }
        // Prüft auf mindestens ein Sonderzeichen
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return "Das Passwort muss mindestens ein Sonderzeichen enthalten.";
        }

        return null; // Passwort ist stark genug
    }
}
