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

        // --- UNTERE LEISTE FÜR DEN ZURÜCK-BUTTON ---
        HBox bottomBar = new HBox();
        bottomBar.setPadding(new Insets(20, 20, 50, 100));
        bottomBar.setAlignment(Pos.CENTER_LEFT);

        // --- ZENTRALER BEREICH FÜR DAS FORMULAR ---
        javafx.scene.layout.VBox loginRoot = createVBox(15, Pos.CENTER);
        loginRoot.setStyle("-fx-background-color: transparent");

        // Die weiße Box mit Schatten
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

        Label title = new Label("MITARBEITER LOGIN");
        // Rote Akzentfarbe, um es vom Kundenbereich optisch abzugrenzen
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Benutzername");
        usernameField.setPrefWidth(250);
        usernameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Passwort");
        passwordField.setPrefWidth(250);
        passwordField.setMaxWidth(250);

        Button loginBtn = new Button("Einloggen");
        loginBtn.setDefaultButton(true);
        // Auch der Button bekommt die rote Farbe
        loginBtn.setStyle(
                "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: Hand;");
        loginBtn.setPrefWidth(250);

        // --- DER ZURÜCK BUTTON ---
        Button btnBackToMain = createBackButton("Zurück zum Kunden-Login");
        btnBackToMain.setPrefWidth(250);

        // --- AKTIONEN (KLICKS) ---
        btnBackToMain.setOnAction(e -> app.navigateTo(ScreenManager.Screen.LOGIN));

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // WICHTIG: Füge hier deine bisherige Logik ein,
            // die überprüft, ob die Mitarbeiter-Daten richtig sind!
            // Beispiel:
            if (username.equals("admin") && password.equals("admin")) {
                app.navigateTo(ScreenManager.Screen.EMPLOYEE_EVENTS);
            } else {
                app.showAlert(Alert.AlertType.ERROR, "Fehler", "Falsche Zugangsdaten.");
            }
        });

        // --- ZUSAMMENBAU ---
        formBox.getChildren().addAll(title, usernameField, passwordField, loginBtn);
        loginRoot.getChildren().add(formBox);

        bottomBar.getChildren().add(btnBackToMain);

        mainRoot.setCenter(loginRoot);
        mainRoot.setBottom(bottomBar);

        Scene scene = createDefaultScene(mainRoot);
        Platform.runLater(mainRoot::requestFocus);
        return scene;
    }
}