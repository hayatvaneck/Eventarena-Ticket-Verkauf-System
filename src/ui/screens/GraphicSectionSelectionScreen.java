package ui.screens;

import domain.Event.EventType;
import domain.Section;
import domain.StandingSection;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import ui.App;
import ui.ScreenManager;

/**
 * Die Klasse GraphicSectionSelectionScreen zeigt den grafischen Saalplan und ermoeglicht die Bereichsauswahl.

 */

public class GraphicSectionSelectionScreen extends BaseScreen {

    private final App app;

    public GraphicSectionSelectionScreen(App app) {
        this.app = app;
    }

    @Override
    public Scene buildScene() {
        if (app.getCurrentSelectedEvent() == null) {
            VBox emptyRoot = createRoot(10, new Insets(20), Pos.CENTER);
            emptyRoot.getChildren().add(new Label("Kein Event ausgewaehlt."));
            return createDefaultScene(emptyRoot);
        }

        VBox root = createRoot(15, new Insets(20), Pos.CENTER);
        root.setStyle("-fx-background-color: #ebe4e4;");

        Label title = new Label("Blockauswahl fuer: " + app.getCurrentSelectedEvent().getTitle());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        StackPane mapContainer;
        if (app.getCurrentSelectedEvent().getEventType() == EventType.BASKETBALL) {
            mapContainer = createBasketballLayout();
        } else if (app.getCurrentSelectedEvent().getEventType() == EventType.CONCERT) {
            mapContainer = createConcertLayout();
        } else {
            mapContainer = createGalaLayout();
        }

        if (mapContainer == null) {
            mapContainer = new StackPane(new Label("Fehler: Saalplan-Layout ist null!"));
            mapContainer.setStyle("-fx-background-color: #ffcccc; -fx-border-color: red;");
            mapContainer.setPrefSize(600, 400);
        }

        Button backButton = new Button("Zurueck zu den Events");
        backButton.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white;");
        backButton.setOnAction(e -> app.navigateTo(ScreenManager.Screen.MAIN_MENU));

        root.getChildren().addAll(title, mapContainer, backButton);
        return createDefaultScene(root);
    }

    private StackPane createConcertLayout() {
        StackPane mapContainer = new StackPane();
        Pane clickLayer = new Pane();
        mapContainer.setStyle("-fx-border-color: rgba(0,0,0,0.1);");

        ImageView imageView = new ImageView();
        try {
            Image arenaMapImage = new Image(getClass().getResourceAsStream("/ui/resources/images/saalplan_stehplaetze_innenraum.png"));
            imageView.setImage(arenaMapImage);
            imageView.setFitWidth(600);
            imageView.setPreserveRatio(true);
            mapContainer.getChildren().add(imageView);
        } catch (Exception e) {
            mapContainer.setStyle("-fx-background-color: #cccccc; -fx-border-color: red");
            mapContainer.setPrefSize(600, 450);
        }

        Polygon block1 = new Polygon(new double[]{373.6, 124.0, 373.6, 68.0, 583.2, 68.0, 583.2, 160.0, 423.2, 160.0, 422.4, 124.0});
        setupStandardBlock(block1, "Block 1");

        Polygon block2 = new Polygon(new double[]{158.4, 160.0, 320.0, 160.0, 320.0, 125.0, 368.8, 125.0, 368.8, 68.0, 158.4, 68.0});
        setupStandardBlock(block2, "Block 2");

        Polygon block3 = new Polygon(new double[]{155.2, 354.4, 368.8, 354.4, 369.6, 447.2, 156.0, 447.2});
        setupStandardBlock(block3, "Block 3");

        Polygon block4 = new Polygon(new double[]{372.8, 354.4, 583.2, 354.4, 583.2, 447.2, 372.8, 447.2});
        setupStandardBlock(block4, "Block 4");

        Polygon block6 = new Polygon(new double[]{97.6, 176.0, 166.4, 176.8, 165.6, 336.0, 97.6, 336.0});
        setupStandardBlock(block6, "Block 6");

        Polygon vipBlock = new Polygon(new double[]{319.2, 160.0, 319.2, 124.0, 421.6, 124.0, 423.2, 160.0});
        setupStandardBlock(vipBlock, "VIP");

        Polygon standingArea = new Polygon(new double[]{185.6, 176.8, 548.8, 176.8, 548.8, 336.0, 185.6, 336.0});
        standingArea.setStyle("-fx-cursor: hand;");
        standingArea.setFill(Color.web("#2c3e50", 0.15));
        standingArea.setStroke(Color.web("#2c3e50", 0.4));
        standingArea.setStrokeWidth(1);

        Section standingSection = app.findSectionByName("Innenraum");
        if (standingSection != null && app.getCurrentSelectedEvent() != null) {
            double calculatedPrice = app.getCurrentSelectedEvent().getBasePrice() * standingSection.getPriceFactor();
            Tooltip standingTooltip = new Tooltip(String.format("Innenraum (Stehplatz)\n-----------------------\nTicketpreis: %.2f â‚¬", calculatedPrice));
            standingTooltip.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-background-color: #2c3e50;" +
                "-fx-text-fill: white;" +
                "-fx-padding: 8px;" +
                "-fx-background-radius: 4px;"
            );
            standingTooltip.setShowDelay(javafx.util.Duration.millis(100));
            Tooltip.install(standingArea, standingTooltip);
        }

        standingArea.setOnMouseEntered(e -> standingArea.setFill(Color.web("#2c3e50", 0.5)));
        standingArea.setOnMouseExited(e -> standingArea.setFill(Color.web("#2c3e50", 0.15)));
        standingArea.setOnMouseClicked(e -> {
            app.setCurrentSelectedSection(app.findSectionByName("Innenraum"));
            if (app.getCurrentSelectedSection() instanceof StandingSection) {
                app.navigateTo(ScreenManager.Screen.STANDING_AREA_SELECTION);
            } else {
                app.showAlert(Alert.AlertType.WARNING, "Fehler", "Der Stehplatzbereich konnte nicht geladen werden.");
            }
        });

        clickLayer.getChildren().addAll(block1, block2, block3, block4, block6, vipBlock, standingArea);
        mapContainer.getChildren().addAll(clickLayer);
        return mapContainer;
    }

    private StackPane createBasketballLayout() {
        StackPane mapContainer = new StackPane();
        Pane clickLayer = new Pane();
        mapContainer.setStyle("-fx-border-color: rgba(0,0,0,0.1);");

        Image arenaMapImage = new Image(getClass().getResourceAsStream("/ui/resources/images/saalplan_basketball.png"));
        ImageView imageView = new ImageView(arenaMapImage);
        imageView.setFitWidth(600);
        imageView.setPreserveRatio(true);

        Polygon block2 = new Polygon(new double[]{191.0, 96.0, 369.0, 96.0, 369.0, 137.0, 318.0, 138.0, 318.0, 174.0, 191.0, 174.0});
        setupStandardBlock(block2, "Block 2");

        Polygon vipBlock = new Polygon(new double[]{318.4, 175.2, 318.4, 139.0, 422.4, 139.0, 422.4, 175.2});
        setupStandardBlock(vipBlock, "VIP");

        Polygon block1 = new Polygon(new double[]{372.0, 96.0, 551.0, 96.0, 551.0, 174.0, 422.0, 174.0, 422.0, 138.0, 372.0, 138.0});
        setupStandardBlock(block1, "Block 1");

        Polygon block6 = new Polygon(new double[]{140.8, 190.4, 198.4, 190.4, 198.4, 324.8, 140.8, 324.8});
        setupStandardBlock(block6, "Block 6");

        Polygon block5 = new Polygon(new double[]{541.6, 190.4, 600.8, 190.4, 600.8, 324.8, 541.6, 324.8});
        setupStandardBlock(block5, "Block 5");

        Polygon block3 = new Polygon(new double[]{190.4, 340.0, 370.4, 340.0, 370.4, 419.2, 190.4, 419.2});
        setupStandardBlock(block3, "Block 3");

        Polygon block4 = new Polygon(new double[]{372.8, 340.0, 552.8, 340.0, 552.8, 419.2, 372.8, 419.2});
        setupStandardBlock(block4, "Block 4");

        clickLayer.getChildren().addAll(block1, block2, vipBlock, block3, block4, block5, block6);
        mapContainer.getChildren().addAll(imageView, clickLayer);
        return mapContainer;
    }

    private StackPane createGalaLayout() {
        StackPane mapContainer = new StackPane();
        mapContainer.setStyle("-fx-background-color: #34495e; -fx-border-color: gold;");
        mapContainer.setPrefSize(600, 400);

        Label placeholder = new Label("Gala-Saalplan (Noch in Entwicklung)");
        placeholder.setStyle("-fx-text-fill: white; -fx-font-size: 16px");

        mapContainer.getChildren().add(placeholder);
        return mapContainer;
    }

    private void setupStandardBlock(Polygon block, String sectionName) {
        block.setStyle("-fx-cursor: hand;");
        block.setFill(Color.web("#2c3e50", 0.15));
        block.setStroke(Color.web("#2c3e50", 0.4));
        block.setStrokeWidth(1);

        block.setOnMouseEntered(e -> block.setFill(Color.web("#2c3e50", 0.5)));
        block.setOnMouseExited(e -> block.setFill(Color.web("#2c3e50", 0.15)));

        block.setOnMouseClicked(e -> {
            app.setCurrentSelectedSection(app.findSectionByName(sectionName));
            if (app.getCurrentSelectedSection() != null) {
                app.navigateTo(ScreenManager.Screen.SEAT_SELECTION);
            }
        });

        Section section = app.findSectionByName(sectionName);
        if (section != null && app.getCurrentSelectedEvent() != null) {
            double calculatedPrice = app.getCurrentSelectedEvent().getBasePrice() * section.getPriceFactor();
            String tooltipText = String.format("%s\n-----------------------\nTicketpreis: %.2f â‚¬", sectionName, calculatedPrice);

            Tooltip tooltip = new Tooltip(tooltipText);
            tooltip.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-background-color: #2c3e50;" +
                "-fx-text-fill: white;" +
                "-fx-padding: 8px;" +
                "-fx-background-radius: 4px"
            );
            tooltip.setShowDelay(javafx.util.Duration.millis(100));
            Tooltip.install(block, tooltip);
        }
    }
}



