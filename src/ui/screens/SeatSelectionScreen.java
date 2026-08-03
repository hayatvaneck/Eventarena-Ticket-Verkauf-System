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
import javafx.scene.layout.HBox;
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

        Label title = createTitle(seatedSection.getName());
        Label instruction = createSubtitle("Wählen Sie einen Sitzplatz aus:");

        VBox headerBox = new VBox(5); // 5 Pixel Abstand zwischen Titel und Untertitel
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setMaxWidth(400); // Breite des Kastens (kannst du beliebig anpassen)
        headerBox.setStyle(
                "-fx-background-color: white; " +
                        "-fx-padding: 15 30 15 30; " + // Innenabstand, damit der Text Luft hat
                        "-fx-background-radius: 10; " +
                        // "-fx-border-color: #81b9ed; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 5, 0, 0, 2);");
        headerBox.getChildren().addAll(title, instruction);

        // 1. Der neue, schicke Kasten für die Bühne
        String stageText = "B Ü H N E";

        // Wenn der Saalplan eine Arena ist, ändern wir den Text[cite: 1]
        if (app.getCurrentSelectedEvent().getMapType() == domain.Event.MapType.ARENA) {
            stageText = "S P I E L F E L D";
        }

        Label stageLabel = new Label(stageText);
        stageLabel.setStyle(ui.UIStyles.STAGE_LABEL_STYLE);

        // 2. Der Wrapper, um den Kasten nach links oder rechts zu schieben
        HBox stageContainer = new HBox();
        stageContainer.setMaxWidth(680); // Passt sich der Breite deines Sitzplans an

        // 3. Dynamische Anpassung je nach Block!
        String blockName = seatedSection.getName().toLowerCase();

        if (blockName.contains("block 1") || blockName.contains("block 3")) {
            // Wenn man rechts sitzt, ist die Bühne/Spielfeld links von einem
            stageContainer.setAlignment(Pos.CENTER_RIGHT);
            stageLabel.setPrefWidth(500);
            stageLabel.setMaxWidth(500);

        } else if (blockName.contains("block 2") || blockName.contains("block 4")) {
            // Wenn man links sitzt, ist die Bühne/Spielfeld rechts von einem
            stageContainer.setAlignment(Pos.CENTER_LEFT);
            stageLabel.setPrefWidth(500);
            stageLabel.setMaxWidth(500);
        } else {
            // Für alle anderen (z.B. Innenraum, Block 5, Block 6) bleibt es mittig
            stageContainer.setAlignment(Pos.CENTER);
            stageLabel.setPrefWidth(650);
            stageLabel.setMaxWidth(650);
        }

        stageContainer.getChildren().add(stageLabel);

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

        VBox gridWrapper = new VBox(20); // 20 Pixel Abstand zwischen Bühne und Sitzen
        gridWrapper.setAlignment(Pos.CENTER);
        gridWrapper.getChildren().addAll(stageContainer, seatGrid);

        ScrollPane seatGridScrollPane = createTransparentScrollPane(gridWrapper);
        seatGridScrollPane.setPannable(true);
        seatGridScrollPane.setFitToHeight(false);
        seatGridScrollPane.setFitToWidth(true);
        seatGridScrollPane.setPrefViewportHeight(420);
        seatGridScrollPane.setPrefViewportWidth(740);

        Label selectionStatusLabel = app.getSelectionStatusLabel();
        selectionStatusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        selectionStatusLabel.setText("Kein Platz ausgewählt");

        Button confirmButton = createSelectingButton("Sitzplatz bestätigen");
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

        root.getChildren().addAll(headerBox, seatGridScrollPane, selectionStatusLabel, confirmButton,
                backButton);

        return createDefaultScene(root);
    }
}