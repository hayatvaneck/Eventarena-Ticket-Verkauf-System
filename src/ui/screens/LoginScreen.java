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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ui.App;
import ui.ScreenManager;

/**
 * Die Klasse LoginScreen stellt die Benutzeranmeldung inklusive Rücksprung in
 * den Buchungsfluss bereit.
 * 
 */

public class LoginScreen extends BaseScreen {

    private final App app;

    public LoginScreen(App app) {
        this.app = app;
    }

    @Override
    public Scene buildScene() {
        BorderPane mainRoot = new BorderPane();
        mainRoot.setStyle("-fx-background-color: #f5f5f7");

        // --- UNTERE LEISTE FÜR DEN ZURÜCK-BUTTON ---
        HBox bottomBar = new HBox();
        bottomBar.setPadding(new Insets(20, 20, 50, 100));
        bottomBar.setAlignment(Pos.CENTER_LEFT);

        // --- ZENTRALER BEREICH FÜR DAS FORMULAR ---
        javafx.scene.layout.VBox loginRoot = createVBox(15, Pos.CENTER);
        loginRoot.setStyle("-fx-background-color: transparent");

        // Die neue weiße Box mit Schatten
        VBox formBox = createVBox(15, Pos.CENTER);
        formBox.setPadding(new Insets(40, 50, 40, 50));
        formBox.setMaxWidth(400);
        formBox.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 12; " +
                        "-fx-border-color: #cbd5e1; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 12; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 10, 0, 0, 4);");

        Label title = new Label("KUNDEN LOGIN");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        TextField emailField = new TextField();
        emailField.setPromptText("Mail-Adresse");
        emailField.setPrefWidth(250);
        emailField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Passwort");
        passwordField.setPrefWidth(250);
        passwordField.setMaxWidth(250);

        Button loginBtn = new Button("Einloggen");
        loginBtn.setDefaultButton(true);
        loginBtn.setStyle(
                "-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: Hand;");
        loginBtn.setPrefWidth(250);

        Label registerLink = new Label("Noch kein Konto? Hier registrieren");
        registerLink.setStyle("-fx-text-fill: #2980b9; -fx-cursor: hand;");

        Label employeeLink = new Label("Mitarbeiter? Hier Einloggen");
        employeeLink
                .setStyle("-fx-text-fill: #e74c3c; -fx-cursor: hand; -fx-font-weight: bold;");

        // --- DER ZURÜCK BUTTON ---
        Button btnBackToMain = createBackButton("Zurück zum Hauptmenü");
        btnBackToMain.setPrefWidth(250);

        // --- AKTIONEN (KLICKS) ---
        loginBtn.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            User user = app.validateUserCredentials(email, password);
            if (user != null) {
                app.setLoggedInUser(user);
                app.runPostLoginActionOrGoMainMenu();
            } else {
                app.showAlert(Alert.AlertType.ERROR, "Fehler", "Falscher Benutzername oder Passwort.");
            }
        });

        btnBackToMain.setOnAction(e -> {
            app.clearPostLoginAction();
            app.navigateTo(ScreenManager.Screen.MAIN_MENU);
        });

        employeeLink.setOnMouseClicked(e -> app.navigateTo(ScreenManager.Screen.EMPLOYEE_LOGIN));
        registerLink.setOnMouseClicked(e -> app.navigateTo(ScreenManager.Screen.REGISTER));

        // --- ZUSAMMENBAU ---
        // 1. Felder in die weiße Box packen
        formBox.getChildren().addAll(title, emailField, passwordField, loginBtn, registerLink, employeeLink);

        // 2. Weiße Box in den mittleren Bereich packen
        loginRoot.getChildren().add(formBox);

        // 3. Zurück-Button in die untere Leiste packen
        bottomBar.getChildren().add(btnBackToMain);

        // 4. Alles dem Haupt-Layout übergeben
        mainRoot.setCenter(loginRoot);
        mainRoot.setBottom(bottomBar);

        Scene scene = createDefaultScene(mainRoot);
        Platform.runLater(mainRoot::requestFocus);
        return scene;
    }
}
