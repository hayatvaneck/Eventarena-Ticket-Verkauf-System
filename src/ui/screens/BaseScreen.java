package ui.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Die abstrakte Klasse BaseScreen stellt gemeinsame UI-Helfer fuer alle JavaFX-Screens bereit.

 */

public abstract class BaseScreen {

    protected static final double DEFAULT_WIDTH = 800;
    protected static final double DEFAULT_HEIGHT = 700;

    private static final String COLOR_BG = "#f5f5f7";
    private static final String COLOR_TEXT = "#2c3e50";

    protected static final String TITLE_STYLE = "-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_TEXT + ";";
    protected static final String SUBTITLE_STYLE = "-fx-font-size: 14px; -fx-text-fill: #7f8c8d;";

    protected static final String CONFIRM_BUTTON_STYLE =
        "-fx-background-color: #2c3e50;" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 14px;" +
        "-fx-font-weight: bold;" +
        "-fx-padding: 10 20 10 20;" +
        "-fx-background-radius: 6px;" +
        "-fx-cursor: hand;";

    protected static final String BACK_BUTTON_STYLE =
        "-fx-background-color: #a6a6ac;" +
        "-fx-text-fill: black;" +
        "-fx-font-size: 14px;" +
        "-fx-padding: 10 20 10 20;" +
        "-fx-background-radius: 6px;";

    protected static final String SECONDARY_BUTTON_STYLE =
        "-fx-background-color: #7f8c8d;" +
        "-fx-text-fill: white;" +
        "-fx-background-radius: 6px;" +
        "-fx-cursor: hand;";

    protected static final String DANGER_BUTTON_STYLE =
        "-fx-background-color: #e74c3c;" +
        "-fx-text-fill: white;" +
        "-fx-font-weight: bold;" +
        "-fx-background-radius: 6px;" +
        "-fx-cursor: hand;";

    public abstract Scene buildScene();

    protected VBox createRoot(double spacing, Insets padding, Pos alignment) {
        VBox root = new VBox(spacing);
        root.setPadding(padding);
        root.setAlignment(alignment);
        root.setStyle("-fx-background-color: " + COLOR_BG + ";");
        return root;
    }

    protected VBox createVBox(double spacing, Pos alignment) {
        VBox box = new VBox(spacing);
        box.setAlignment(alignment);
        return box;
    }

    protected HBox createHBox(double spacing, Pos alignment) {
        HBox box = new HBox(spacing);
        box.setAlignment(alignment);
        return box;
    }

    protected Label createTitle(String text) {
        Label label = new Label(text);
        label.setStyle(TITLE_STYLE);
        return label;
    }

    protected Label createSubtitle(String text) {
        Label label = new Label(text);
        label.setStyle(SUBTITLE_STYLE);
        return label;
    }

    protected Button createConfirmButton(String text) {
        Button button = new Button(text);
        button.setStyle(CONFIRM_BUTTON_STYLE);
        return button;
    }

    protected Button createSecondaryButton(String text) {
        Button button = new Button(text);
        button.setStyle(SECONDARY_BUTTON_STYLE);
        return button;
    }

    protected Button createDangerButton(String text) {
        Button button = new Button(text);
        button.setStyle(DANGER_BUTTON_STYLE);
        return button;
    }

    protected Button createBackButton(String text) {
        Button button = createConfirmButton(text);
        button.setStyle(BACK_BUTTON_STYLE + "-fx-cursor: hand;");
        return button;
    }

    protected Label createMutedInfoLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-style: italic; -fx-text-fill: #7f8c8d; -fx-font-size: 14px;");
        return label;
    }

    protected ScrollPane createTransparentScrollPane(Parent content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        return scrollPane;
    }

    protected Region createHorizontalSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    protected Region createVerticalSpacer() {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    protected Scene createDefaultScene(Parent root) {
        return new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}



