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
import domain.CartItem;

import java.util.List;

/**
 * Die Klasse SeatSelectionScreen zeigt den Sitzplan eines Blocks
 * und übernimmt ausgewählte Sitze in den Warenkorb.
 */
public class SeatSelectionScreen extends BaseScreen {

    /** Anwendungskontext für Auswahlzustand, Warenkorb und Navigation. */
    private final App app;

    /**
     * Erstellt die Sitzplatzauswahl für den zuvor gewählten Hallenbereich.
     *
     * @param app zentraler Anwendungskontext
     */
    public SeatSelectionScreen(App app) {
        this.app = app;
    }

    /**
     * Baut den interaktiven Sitzplan auf, validiert das Ticketlimit und übernimmt
     * bestätigte Sitzplätze in den Warenkorb.
     *
     * @return vollständige Sitzplatzauswahlszene
     */
    @Override
    public Scene buildScene() {
        Section selectedSection = app.getCurrentSelectedSection();

        if (!(selectedSection instanceof SeatedSection)) {
            app.showAlert(Alert.AlertType.ERROR, "Fehler", "Dieser Block besitzt keine Sitzplätze!");
            app.navigateTo(ScreenManager.Screen.GRAPHIC_SECTION_SELECTION);
            return createDefaultScene(createRoot(10, new Insets(20), Pos.CENTER));
        }

        SeatedSection seatedSection = (SeatedSection) selectedSection;

        Label title = createTitle(seatedSection.getName());
        Label instruction = createSubtitle("Wählen Sie einen Sitzplatz aus:");

        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setMaxWidth(400);
        headerBox.setStyle(
                "-fx-background-color: white; " +
                        "-fx-padding: 15 30 15 30; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 5, 0, 0, 2);");
        headerBox.getChildren().addAll(title, instruction);

        // Die Orientierungshilfe passt ihre Beschriftung an den Hallenplantyp an.
        String stageText = "B Ü H N E";

        if (app.getCurrentSelectedEvent().getMapType() == domain.Event.MapType.ARENA) {
            stageText = "S P I E L F E L D";
        }

        Label stageLabel = new Label(stageText);
        stageLabel.setStyle(ui.UIStyles.STAGE_LABEL_STYLE);

        // Ein separater Container hält die Bühnenanzeige über dem Sitzraster mittig.
        HBox stageContainer = new HBox();
        stageContainer.setMaxWidth(680);

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

        // Das Raster bleibt auf seine tatsächlich benötigte Größe begrenzt.
        seatGrid.setMaxWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        seatGrid.setMaxHeight(javafx.scene.layout.Region.USE_PREF_SIZE);

        SeatSelectionController controller = new SeatSelectionController(seatGrid, app::updateSelectionLabel);
        controller.populateSeatPlan(selectedSection, app.getCartItems());

        VBox gridWrapper = new VBox(20);
        gridWrapper.setAlignment(Pos.CENTER);

        // Die volle Viewportbreite hält Bühne und Sitzraster gemeinsam zentriert.
        gridWrapper.setPrefWidth(800);

        gridWrapper.getChildren().addAll(stageContainer, seatGrid);

        ScrollPane seatGridScrollPane = createTransparentScrollPane(gridWrapper);
        seatGridScrollPane.setPannable(true);
        seatGridScrollPane.setFitToHeight(false);
        seatGridScrollPane.setFitToWidth(true);
        seatGridScrollPane.setPrefViewportHeight(550);
        seatGridScrollPane.setPrefViewportWidth(800);

        Label selectionStatusLabel = app.getSelectionStatusLabel();
        selectionStatusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        selectionStatusLabel.setText("Kein Platz ausgewählt");

        // Bestätigung und Rückweg verwenden dieselben Abmessungen.
        Button confirmButton = createSelectingButton("Sitzplatz bestätigen");
        confirmButton.setPrefWidth(300);
        confirmButton.setMinHeight(45);
        confirmButton.setMaxHeight(45);
        confirmButton.setOnAction(e -> {
            List<Seat> newSeats = controller.getSelectedSeats();

            if (!newSeats.isEmpty()) {
                // Das gemeinsame Warenkorblimit gilt auch für Mehrfachauswahlen.
                if (app.getCartItems().size() + newSeats.size() > 10) {
                    app.showAlert(Alert.AlertType.WARNING, "Limit erreicht",
                            "Sie können maximal 10 Tickets gleichzeitig kaufen. Sie haben bereits " +
                                    app.getCartItems().size() + " Ticket(s) im Warenkorb.");
                    return;
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

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(backButton, confirmButton);

        // Sitzplan und feste Aktionsleiste werden in einem BorderPane zusammengeführt.
        javafx.scene.layout.BorderPane root = new javafx.scene.layout.BorderPane();
        root.setStyle("-fx-background-color: #f5f5f7;");

        // Scrollbarer Auswahlbereich mit aktuellem Status.
        VBox topBox = createRoot(15, new Insets(30, 30, 20, 30), Pos.TOP_CENTER);
        topBox.getChildren().addAll(headerBox, seatGridScrollPane, selectionStatusLabel);

        // Feste Navigation mit unsichtbarem Footer als Ausrichtungsplatzhalter.
        VBox bottomBox = createRoot(10, new Insets(0, 30, 30, 30), Pos.BOTTOM_CENTER);
        HBox dummyFooter = createInvisibleStandardFooter();
        bottomBox.getChildren().addAll(buttonBox, dummyFooter);

        root.setCenter(topBox);
        root.setBottom(bottomBox);

        return createDefaultScene(root);
    }
}
