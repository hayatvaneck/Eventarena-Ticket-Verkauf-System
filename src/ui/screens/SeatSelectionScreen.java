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
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ui.App;
import ui.ScreenManager;
import domain.CartItem; // Falls CartItem woanders liegt, diesen Import anpassen

import java.util.List;

/**
 * Die Klasse SeatSelectionScreen zeigt den Sitzplan eines Blocks
 * und übernimmt ausgewählte Sitze in den Warenkorb.
 */
public class SeatSelectionScreen extends BaseScreen {

    private final App app;

    public SeatSelectionScreen(App app) {
        this.app = app;
    }

    @Override
    public Scene buildScene() {
        Section selectedSection = app.getCurrentSelectedSection();

        if (!(selectedSection instanceof SeatedSection)) {
            app.showAlert(Alert.AlertType.ERROR, "Fehler", "Dieser Block besitzt keine Sitzplätze!");
            app.navigateTo(ScreenManager.Screen.GRAPHIC_SECTION_SELECTION);
            return createDefaultScene(createRoot(10, new Insets(20), Pos.CENTER));
        }

        SeatedSection seatedSection = (SeatedSection) selectedSection;

        VBox root = createRoot(15, new Insets(20), Pos.CENTER);

        Label title = createTitle("Sitzplätze " + seatedSection.getName());
        Label instruction = createSubtitle("Wählen Sie einen Sitzplatz aus:");

        Label stageLabel = new Label("--- BÜHNE / SPIELFELD ---");
        stageLabel.setStyle("-fx-background-color: #7f8c8d; -fx-padding: 5 50 5 50; -fx-text-fill: white;");

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
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        SeatSelectionController controller = new SeatSelectionController(seatGrid, app::updateSelectionLabel);
        controller.populateSeatPlan(selectedSection, app.getCartItems());

        VBox gridWrapper = new VBox(seatGrid);
        gridWrapper.setAlignment(Pos.CENTER);

        ScrollPane seatGridScrollPane = createTransparentScrollPane(seatGrid);
        seatGridScrollPane.setPannable(true);
        seatGridScrollPane.setFitToHeight(false);
        seatGridScrollPane.setFitToWidth(true);
        seatGridScrollPane.setPrefViewportHeight(420);
        seatGridScrollPane.setPrefViewportWidth(740);

        Label selectionStatusLabel = app.getSelectionStatusLabel();
        selectionStatusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        selectionStatusLabel.setText("Kein Platz ausgewählt");

        Button confirmButton = createConfirmButton("Sitzplatz bestätigen");
        confirmButton.setOnAction(e -> {
            List<Seat> newSeats = controller.getSelectedSeats();

            if (!newSeats.isEmpty()) {
                for (Seat seat : newSeats) {
                    app.getCartItems().add(
                            new CartItem(app.getCurrentSelectedEvent(), selectedSection, seat));
                }
                app.navigateTo(ScreenManager.Screen.CART);
            } else if (!app.getCartItems().isEmpty()) {
                app.navigateTo(ScreenManager.Screen.CART);
            } else {
                app.showAlert(Alert.AlertType.WARNING, "Kein Sitzplatz",
                        "Bitte wählen Sie einen freien Sitzplatz aus!");
            }
        });

        Button backButton = createBackButton("Zurück zum Saalplan");
        backButton.setOnAction(e -> app.navigateTo(ScreenManager.Screen.GRAPHIC_SECTION_SELECTION));

        root.getChildren().addAll(title, instruction, stageLabel, seatGridScrollPane, selectionStatusLabel, confirmButton, backButton);

        return createDefaultScene(root);
    }
}