package ui.screens;

import domain.User;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import service.PasswordService;
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
        BorderPane mainRoot = new BorderPane();
        mainRoot.setStyle("-fx-background-color: #f5f5f7");

        // --- UNTERE LEISTE FÜR DEN ZURÜCK-BUTTON (Exakt wie im LoginScreen) ---
        HBox bottomBar = new HBox();
        bottomBar.setPadding(new Insets(20, 20, 50, 100));
        bottomBar.setAlignment(Pos.CENTER_LEFT);

        // --- ZENTRALER BEREICH FÜR DAS FORMULAR ---
        javafx.scene.layout.VBox registerRoot = createVBox(15, Pos.CENTER);
        registerRoot.setStyle("-fx-background-color: transparent");

        // Die weiße Box mit Schatten
        VBox formBox = createVBox(15, Pos.CENTER);
        formBox.setPadding(new Insets(30, 40, 30, 40));
        formBox.setMaxWidth(400);
        formBox.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 12; " +
                        "-fx-border-color: #cbd5e1; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 12; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 10, 0, 0, 4);");

        Label title = new Label("NEUES KONTO ERSTELLEN");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // --- FELDER MIT STRIKTEM 30-ZEICHEN LIMIT ---
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Vorname");
        firstNameField.setPrefWidth(250);
        firstNameField.setMaxWidth(250);
        firstNameField.setTextFormatter(
                new javafx.scene.control.TextFormatter<String>(c -> c.getControlNewText().length() <= 30 ? c : null));

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Nachname");
        lastNameField.setPrefWidth(250);
        lastNameField.setMaxWidth(250);
        lastNameField.setTextFormatter(
                new javafx.scene.control.TextFormatter<String>(c -> c.getControlNewText().length() <= 30 ? c : null));

        TextField emailField = new TextField();
        emailField.setPromptText("E-Mail-Adresse");
        emailField.setPrefWidth(250);
        emailField.setMaxWidth(250);
        emailField.setTextFormatter(
                new javafx.scene.control.TextFormatter<String>(c -> c.getControlNewText().length() <= 30 ? c : null));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Passwort");
        passwordField.setPrefWidth(250);
        passwordField.setMaxWidth(250);
        passwordField.setTextFormatter(
                new javafx.scene.control.TextFormatter<String>(c -> c.getControlNewText().length() <= 30 ? c : null));

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Passwort bestätigen");
        confirmPasswordField.setPrefWidth(250);
        confirmPasswordField.setMaxWidth(250);
        confirmPasswordField.setTextFormatter(
                new javafx.scene.control.TextFormatter<String>(c -> c.getControlNewText().length() <= 30 ? c : null));

        // --- HINWEIS-BOX FÜR PASSWORT ---
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

        Button registerBtn = new Button("Registrieren");
        registerBtn.setDefaultButton(true);
        registerBtn.setStyle(
                "-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: Hand;");
        registerBtn.setPrefWidth(250);

        // --- DER ZURÜCK BUTTON (Jetzt wie im LoginScreen!) ---
        Button btnBackToLogin = createBackButton("Zurück zum Login");
        btnBackToLogin.setPrefWidth(250);

        // --- AKTIONEN (KLICKS) ---
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

            // E-Mail-Prüfung (darf nicht fehlerhaft sein)
            String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[a-zA-Z]{2,6}$";
            if (!email.matches(emailRegex)) {
                app.showAlert(Alert.AlertType.WARNING, "Fehler",
                        "Bitte geben Sie eine gültige E-Mail-Adresse ein (z.B. max@beispiel.de).");
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

            String passwordHash = service.PasswordService.hashPassword(password);
            User newUser = new User(firstName, lastName, email, passwordHash);
            boolean success = app.registerUser(newUser);

            if (success) {
                app.showAlert(Alert.AlertType.INFORMATION, "Erfolg", "Registrierung erfolgreich!");
                app.navigateTo(ScreenManager.Screen.CART);
            } else {
                app.showAlert(Alert.AlertType.ERROR, "Fehler", "Diese E-Mail-Adresse ist bereits registriert.");
            }
        });

        btnBackToLogin.setOnAction(e -> app.navigateTo(ScreenManager.Screen.LOGIN));

        // --- ZUSAMMENBAU ---
        // 1. Felder in die weiße Box packen
        formBox.getChildren().addAll(
                title,
                firstNameField,
                lastNameField,
                emailField,
                passwordField,
                confirmPasswordField,
                hintBox,
                registerBtn);

        // 2. Weiße Box in den mittleren Bereich packen
        registerRoot.getChildren().add(formBox);

        // 3. Zurück-Button in die untere Leiste packen
        bottomBar.getChildren().add(btnBackToLogin);

        // 4. Alles dem Haupt-Layout übergeben
        mainRoot.setCenter(registerRoot);
        mainRoot.setBottom(bottomBar);

        Scene scene = createDefaultScene(mainRoot);
        scene.setOnMouseClicked(e -> mainRoot.requestFocus());
        Platform.runLater(mainRoot::requestFocus);
        return scene;
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
