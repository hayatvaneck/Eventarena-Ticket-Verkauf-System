package ui.screens;

import controller.SeatSelectionController;
import domain.Seat;
import domain.SeatedSection;
import domain.Section;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ui.App;
import ui.ScreenManager;

import java.util.List;

public class SeatSelectionScreen extends BaseScreen {

    private final App app;

    public SeatSelectionScreen(App app) {
        this.app = app;
    }

    @Override
    public Scene buildScene() {
        Section selectedSection = app.getCurrentSelectedSection();

        if (!(selectedSection instanceof SeatedSection)) {
            app.showAlert(Alert.AlertType.ERROR, "Fehler", "Dieser Block besitzt keine Sitzplaetze!");
            app.navigateTo(ScreenManager.Screen.GRAPHIC_SECTION_SELECTION);
            return createDefaultScene(createRoot(10, new Insets(20), Pos.CENTER));
        }

        SeatedSection seatedSection = (SeatedSection) selectedSection;

        VBox root = createRoot(15, new Insets(20), Pos.CENTER);

        Label header = new Label("Saalplan fuer: " + seatedSection.getName());
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label stageLabel = new Label("--- BUEHNE / SPIELFELD ---");
        stageLabel.setStyle("-fx-background-color: #cc0c0c77; -fx-padding: 5 50 5 50; -fx-text-fill: white;");

        GridPane seatGrid = new GridPane();
        seatGrid.setHgap(6);
        seatGrid.setVgap(6);
        seatGrid.setAlignment(Pos.CENTER);
        seatGrid.setStyle(
            "-fx-border-color: #2c3e50; " +
            "-fx-border-width: 3px; " +
            "-fx-border-radius: 8px; " +
            "-fx-background-color: #f8f9fa; " +
            "-fx-padding: 25px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);"
        );

        SeatSelectionController controller = new SeatSelectionController(seatGrid, app);
        controller.populateSeatPlan(selectedSection, app.getCartSeats());

        Label selectionStatusLabel = app.getSelectionStatusLabel();
        selectionStatusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        selectionStatusLabel.setText("Kein Platz ausgewaehlt");

        Button confirmButton = new Button("Sitzplatz bestaetigen");
        confirmButton.setStyle("-fx-background-color: #d4af37; -fx-text-fill: #2c3e50;");
        confirmButton.setOnAction(e -> {
            List<Seat> newSeats = controller.getSelectedSeats();
            if (!newSeats.isEmpty()) {
                app.getCartSeats().addAll(newSeats);
                app.navigateTo(ScreenManager.Screen.CART);
            } else if (!app.getCartSeats().isEmpty()) {
                app.navigateTo(ScreenManager.Screen.CART);
            } else {
                app.showAlert(Alert.AlertType.WARNING, "Kein Sitzplatz", "Bitte waehlen Sie einen freien Sitzplatz aus!");
            }
        });

        Button backButton = new Button("Zurueck zum Saalplan");
        backButton.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;");
        backButton.setOnAction(e -> app.navigateTo(ScreenManager.Screen.GRAPHIC_SECTION_SELECTION));

        root.getChildren().addAll(header, stageLabel, seatGrid, confirmButton, backButton, selectionStatusLabel);
        return createDefaultScene(root);
    }
}
