package ui.screens;

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

public class EmployeeLoginScreen extends BaseScreen {
    private final App app;

    public EmployeeLoginScreen(App app) {
        this.app = app;
    }

    @Override
    public Scene buildScene() {
        BorderPane mainRoot = new BorderPane();
        mainRoot.setStyle("-fx-background-color: #f5f5f7");

        HBox bottomBar = new HBox();
        bottomBar.setPadding(new Insets(20, 20, 50, 100));
        bottomBar.setAlignment(Pos.CENTER_LEFT);

        VBox loginRoot = createVBox(15, Pos.CENTER);
        loginRoot.setPadding(new Insets(40));

        Label title = new Label("MITARBEITER LOGIN");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;"); // Rot, um es abzuheben

        TextField userField = new TextField();
        userField.setPromptText("Benutzername");
        userField.setPrefWidth(250);
        userField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Passwort");
        passwordField.setPrefWidth(250);
        passwordField.setMaxWidth(250);

        Button loginBtn = new Button("Einloggen");
        loginBtn.setDefaultButton(true);
        loginBtn.setStyle(
                "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: Hand;");
        loginBtn.setPrefWidth(250);

        Button btnBackToMain = createBackButton("Zurück zum Kunden-Login");
        btnBackToMain.setPrefWidth(250);

        loginBtn.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = passwordField.getText().trim();

            if (app.validateEmployeeCredentials(username, password)) {
                app.navigateTo(ScreenManager.Screen.EMPLOYEE_EVENTS);
            } else {
                app.showAlert(Alert.AlertType.ERROR, "Fehler", "Falscher Benutzername oder Passwort.");
                passwordField.clear();
            }
        });

        btnBackToMain.setOnAction(e -> app.navigateTo(ScreenManager.Screen.LOGIN));

        loginRoot.getChildren().addAll(title, userField, passwordField, loginBtn);
        bottomBar.getChildren().add(btnBackToMain);

        mainRoot.setCenter(loginRoot);
        mainRoot.setBottom(bottomBar);

        Scene scene = createDefaultScene(mainRoot);
        Platform.runLater(mainRoot::requestFocus);
        return scene;
    }
}