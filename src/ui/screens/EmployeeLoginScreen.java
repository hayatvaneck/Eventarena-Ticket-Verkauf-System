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

/**
 * Stellt die getrennte Anmeldung für Mitarbeitende bereit und leitet bei
 * erfolgreicher Prüfung zur Eventverwaltung weiter.
 */
public class EmployeeLoginScreen extends BaseScreen {

    /** Anwendungskontext für Zugangsprüfung, Hinweise und Navigation. */
    private final App app;

    /**
     * Erstellt die Mitarbeiteranmeldung.
     *
     * @param app zentraler Anwendungskontext
     */
    public EmployeeLoginScreen(App app) {
        this.app = app;
    }

    /**
     * Baut das Mitarbeiter-Anmeldeformular mit Rückweg zur Kundenanmeldung auf.
     *
     * @return vollständige Mitarbeiter-Anmeldeszene
     */
    @Override
    public Scene buildScene() {
        BorderPane mainRoot = new BorderPane();
        mainRoot.setStyle("-fx-background-color: #f5f5f7");

        // Separater Fußbereich für den Rückweg zur Kundenanmeldung.
        HBox bottomBar = new HBox();
        bottomBar.setPadding(new Insets(20, 20, 50, 100));
        bottomBar.setAlignment(Pos.CENTER_LEFT);

        // Zentral ausgerichteter Formularbereich.
        javafx.scene.layout.VBox loginRoot = createVBox(15, Pos.CENTER);
        loginRoot.setStyle("-fx-background-color: transparent");

        // Eine abgesetzte Karte grenzt die Mitarbeiteranmeldung optisch ab.
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
        // Die rote Akzentfarbe kennzeichnet den internen Mitarbeiterbereich.
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
        // Die primäre Aktion übernimmt die Akzentfarbe des Mitarbeiterbereichs.
        loginBtn.setStyle(
                "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: Hand;");
        loginBtn.setPrefWidth(250);

        // Navigation zurück zur Kundenanmeldung.
        Button btnBackToMain = createBackButton("Zurück zum Kunden-Login");
        btnBackToMain.setPrefWidth(250);

        // Prüfung der aktuell als Demo-Zugang hinterlegten Anmeldedaten.
        btnBackToMain.setOnAction(e -> app.navigateTo(ScreenManager.Screen.LOGIN));

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.equals("admin") && password.equals("admin")) {
                app.navigateTo(ScreenManager.Screen.EMPLOYEE_EVENTS);
            } else {
                app.showAlert(Alert.AlertType.ERROR, "Fehler", "Falsche Zugangsdaten.");
            }
        });

        // Formular, Inhaltsbereich und Fußleiste zusammensetzen.
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
