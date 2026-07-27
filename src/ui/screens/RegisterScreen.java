package ui.screens;

import domain.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import ui.App;
import ui.ScreenManager;

/**
 * Die Klasse RegisterScreen erfasst neue Benutzerkonten und führt zur Anmeldung zurück.

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

        Button registerBtn = new Button("Registrieren");
        registerBtn.setDefaultButton(true);
        registerBtn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: Hand;");
        registerBtn.setPrefWidth(250);

        Label backToLoginLink = new Label("Bereits ein Konto? Zum Login");
        backToLoginLink.setStyle("-fx-text-fill: #2980b9; -fx-cursor: hand;");

        registerBtn.setOnAction(e -> {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                app.showAlert(Alert.AlertType.WARNING, "Fehler", "Bitte füllen Sie alle Felder aus.");
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                app.showAlert(Alert.AlertType.WARNING, "Fehler", "Bitte geben Sie eine gültige E-Mail-Adresse ein.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                app.showAlert(Alert.AlertType.ERROR, "Fehler", "Die eingegebenen Passwörter stimmen nicht überein!");
                passwordField.clear();
                confirmPasswordField.clear();
                return;
            }

            User newUser = new User(firstName, lastName, email, password);
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
            registerBtn,
            backToLoginLink
        );

        return createDefaultScene(registerRoot);
    }
}



