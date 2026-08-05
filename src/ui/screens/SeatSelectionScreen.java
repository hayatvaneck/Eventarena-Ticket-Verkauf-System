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
import ui.screens.BaseScreen;

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

        // VBox root = createRoot(15, new Insets(20), Pos.CENTER);

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
        stageLabel.setStyle(
                "-fx-background-color: #2c3e50; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 12 0 12 0; " +
                        "-fx-alignment: center; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-color: #1a252f; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 5, 0, 0, 2);");

        // 2. Der Wrapper, um den Kasten nach links oder rechts zu schieben
        HBox stageContainer = new HBox();
        stageContainer.setMaxWidth(680); // Passt sich der Breite deines Sitzplans an

        // 3. Dynamische Anpassung je nach Block!
        stageContainer.setAlignment(Pos.CENTER);
        stageLabel.setPrefWidth(650);
        stageLabel.setMaxWidth(650);

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

        // Das zwingt die Box, nicht über die ganze Bildschirmbreite zu wachsen,
        // sondern nur so breit/hoch zu sein wie die Sitze darin.
        seatGrid.setMaxWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        seatGrid.setMaxHeight(javafx.scene.layout.Region.USE_PREF_SIZE);

        SeatSelectionController controller = new SeatSelectionController(seatGrid, app::updateSelectionLabel);
        controller.populateSeatPlan(selectedSection, app.getCartItems());

        VBox gridWrapper = new VBox(20); // 20 Pixel Abstand zwischen Bühne und Sitzen
        gridWrapper.setAlignment(Pos.CENTER);

        // Dies stellt sicher, dass der Container die volle Breite des ScrollPanes
        // ausnutzt
        // und den Inhalt (Bühne + Sitzbox) dadurch perfekt in der Mitte hält.
        gridWrapper.setPrefWidth(800);

        gridWrapper.getChildren().addAll(stageContainer, seatGrid);

        ScrollPane seatGridScrollPane = createTransparentScrollPane(gridWrapper);
        seatGridScrollPane.setPannable(true);
        seatGridScrollPane.setFitToHeight(false);
        seatGridScrollPane.setFitToWidth(true);
        seatGridScrollPane.setPrefViewportHeight(550); // Vorher 420 -> Jetzt deutlich höher!
        seatGridScrollPane.setPrefViewportWidth(800); // Vorher 740 -> Etwas breiter

        Label selectionStatusLabel = app.getSelectionStatusLabel();
        selectionStatusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        selectionStatusLabel.setText("Kein Platz ausgewählt");

        // --- 2. BUTTONS EXAKT WIE IN ANDEREN SCREENS MACHEN (300x45) ---
        Button confirmButton = createSelectingButton("Sitzplatz bestätigen");
        confirmButton.setPrefWidth(300);
        confirmButton.setMinHeight(45);
        confirmButton.setMaxHeight(45);
        confirmButton.setOnAction(e -> {
            List<Seat> newSeats = controller.getSelectedSeats();

            if (!newSeats.isEmpty()) {
                // PRÜFUNG AUF MAXIMAL 10 TICKETS
                if (app.getCartItems().size() + newSeats.size() > 10) {
                    app.showAlert(Alert.AlertType.WARNING, "Limit erreicht",
                            "Sie können maximal 10 Tickets gleichzeitig kaufen. Sie haben bereits " +
                                    app.getCartItems().size() + " Ticket(s) im Warenkorb.");
                    return; // Bricht hier ab und speichert die Sitze nicht
                }

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
        backButton.setPrefWidth(300);
        backButton.setMinHeight(45);
        backButton.setMaxHeight(45);
        backButton.setOnAction(e -> app.navigateTo(ScreenManager.Screen.GRAPHIC_SECTION_SELECTION));

        // --- 3. BUTTONS NEBENEINANDER STELLEN ---
        HBox buttonBox = new HBox(20); // 20 Pixel Abstand zwischen den beiden Buttons
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(backButton, confirmButton); // Zurück links, Bestätigen rechts

        // --- 4. PERFEKTES LAYOUT ZUSAMMENBAUEN ---
        javafx.scene.layout.BorderPane root = new javafx.scene.layout.BorderPane();
        root.setStyle("-fx-background-color: #f5f5f7;");

        // Obere Box mit Platzhalter, Titel, Saalplan und Status
        VBox topBox = createRoot(15, new Insets(30, 30, 20, 30), Pos.TOP_CENTER);
        topBox.getChildren().addAll(headerBox, seatGridScrollPane, selectionStatusLabel);

        // Untere Box mit den Buttons und dem Platzhalter-Footer
        VBox bottomBox = createRoot(10, new Insets(0, 30, 30, 30), Pos.BOTTOM_CENTER);
        HBox dummyFooter = createInvisibleStandardFooter();
        bottomBox.getChildren().addAll(buttonBox, dummyFooter);

        root.setCenter(topBox);
        root.setBottom(bottomBox);

        return createDefaultScene(root);
    }
}