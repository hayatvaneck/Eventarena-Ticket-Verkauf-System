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
import ui.App;
import ui.ScreenManager;

/**
 * Die Klasse LoginScreen stellt die Benutzeranmeldung inklusive Rücksprung in den Buchungsfluss bereit.

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

        HBox bottomBar = new HBox();
        bottomBar.setPadding(new Insets(20));
        bottomBar.setAlignment(Pos.BOTTOM_LEFT);

        javafx.scene.layout.VBox loginRoot = createVBox(15, Pos.CENTER);
        loginRoot.setPadding(new Insets(40));
        loginRoot.setStyle("-fx-background-color: transparent");

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
        loginBtn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: Hand;");
        loginBtn.setPrefWidth(250);

        Label registerLink = new Label("Noch kein Konto? Hier registrieren");
        registerLink.setStyle("-fx-text-fill: #2980b9; -fx-cursor: hand;");

        Label employeeLink = new Label("Mitarbeiter? Hier Einloggen");
        employeeLink.setStyle("-fx-text-fill: #e74c3c; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");
        employeeLink.setOnMouseClicked(e -> app.navigateTo(ScreenManager.Screen.EMPLOYEE_LOGIN));

        Button btnBackToMain = createBackButton("Zurück zum Hauptmenü");
        btnBackToMain.setPrefWidth(150);

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

        registerLink.setOnMouseClicked(e -> app.navigateTo(ScreenManager.Screen.REGISTER));

        loginRoot.getChildren().addAll(title, emailField, passwordField, loginBtn, registerLink, employeeLink);
        bottomBar.getChildren().add(btnBackToMain);

        mainRoot.setCenter(loginRoot);
        mainRoot.setBottom(bottomBar);

        Scene scene = createDefaultScene(mainRoot);
        Platform.runLater(mainRoot::requestFocus);
        return scene;
    }
}



