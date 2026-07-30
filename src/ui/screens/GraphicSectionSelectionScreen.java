package ui.screens;

import domain.Event.MapType;
import domain.Section;
import domain.StandingSection;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ui.App;
import ui.ScreenManager;

/**
 * Die Klasse GraphicSectionSelectionScreen zeigt den grafischen Saalplan und ermöglicht die Bereichsauswahl.

 */

public class GraphicSectionSelectionScreen extends BaseScreen {

    private static final String ROOT_STYLE = "-fx-background-color: #f5f5f7;";
    private static final String CARD_STYLE = "-fx-background-color: #ffffff; -fx-background-radius: 16; -fx-border-color: #cbd5e1; -fx-border-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.04), 8, 0, 0, 2);";
    private static final String BLOCK_BUTTON_STYLE = "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 2; -fx-border-radius: 12; -fx-background-radius: 12; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 4, 0, 0, 1);";

    private final App app;

    public GraphicSectionSelectionScreen(App app) {
        this.app = app;
    }

    @Override
    public Scene buildScene() {
        if (app.getCurrentSelectedEvent() == null) {
            VBox emptyRoot = createRoot(10, new Insets(20), Pos.CENTER);
            emptyRoot.getChildren().add(new Label("Kein Event ausgewählt."));
            return createDefaultScene(emptyRoot);
        }

        VBox root = createRoot(15, new Insets(20), Pos.CENTER);
        root.setStyle(ROOT_STYLE);

        Label title = createTitle("Blockauswahl für: " + app.getCurrentSelectedEvent().getTitle());
        Label instruction = createSubtitle("Wählen Sie einen Block aus:");

        StackPane mapContainer;
        if (app.getCurrentSelectedEvent().getMapType() == MapType.ARENA) {
            mapContainer = createArenaLayout();
        } else if (app.getCurrentSelectedEvent().getMapType() == MapType.STAGE_STANDING) {
            mapContainer = createStageStandingLayout();
        } else if (app.getCurrentSelectedEvent().getMapType() == MapType.STAGE_SEATED) {
            mapContainer = createStageSeatedLayout();
        } else {
            mapContainer = null;
        }

        if (mapContainer == null) {
            mapContainer = new StackPane(new Label("Fehler: Saalplan-Layout ist null!"));
            mapContainer.setStyle("-fx-background-color: #7f8c8d; -fx-border-color: red;");
            mapContainer.setPrefSize(600, 400);
        }

        Button backButton = createBackButton("Zurück zu den Events");
        backButton.setOnAction(e -> app.navigateTo(ScreenManager.Screen.MAIN_MENU));

        root.getChildren().addAll(title, instruction, mapContainer, backButton);
        return createDefaultScene(root);
    }

    private StackPane createStageStandingLayout() {
        StackPane mapContainer = new StackPane();
         VBox mapWrapper = new VBox(20);
        mapWrapper.setAlignment(Pos.CENTER);
        mapWrapper.setPadding(new Insets(24));
        mapWrapper.setStyle(CARD_STYLE);

        // GRID OF SECTIONS
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        // TOP ROW: Block 2 & Block 1
        Button block2Btn = createBlockButton("Block 2", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 215+20, 85+10);
        Button block1Btn = createBlockButton("Block 1", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 215+20, 85+10);

        // MIDDLE ROW: Block 6, VIP Balkon, INNENRAUM, BÜHNE
        Button block6Btn = createBlockButton("Block 6", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 100, 180+20);
        Button vipBtn = createBlockButton("VIP", "#fde047", "#d97706", "#78350f", "VIP BALKON", 70, 180+20);
        Button standingBtn = createBlockButton("Innenraum (Stehplatz)", "#e0e7ff", "#2563eb", "#1d4ed8", "INNENRAUM (Stehplätze)", 442, 180);

        // BÜHNE Block
        StackPane stageBox = new StackPane();
        Rectangle stageRect = new Rectangle(90, 180, Color.web("#0f172a"));
        stageRect.setArcWidth(14);
        stageRect.setArcHeight(14);
        stageRect.setStroke(Color.web("#334155"));
        stageRect.setStrokeWidth(2);

        Label stageLabel = new Label("B Ü H N E");
        stageLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        stageLabel.setTextFill(Color.WHITE);
        stageLabel.setRotate(-90);
        stageBox.getChildren().addAll(stageRect, stageLabel);

        // BOTTOM ROW: Block 3 & Block 4
        Button block3Btn = createBlockButton("Block 3", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 215+20, 85+10);
        Button block4Btn = createBlockButton("Block 4", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 215, 85+10);

        // GRID ASSEMBLY
        grid.add(block2Btn, 2, 0);
        grid.add(block1Btn, 3, 0);

        grid.add(block6Btn, 0, 1);
        grid.add(vipBtn, 1, 1);
        grid.add(standingBtn, 2, 1, 2, 1); // Spans cols 2 & 3
        grid.add(stageBox, 4, 1);

        grid.add(block3Btn, 2, 2);
        grid.add(block4Btn, 3, 2);

        // Legend
        HBox legend = new HBox(28);
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(10, 0, 0, 0));
        legend.getChildren().addAll(
            createLegendItem("Sitzplatz Blöcke", "#bae6fd", "#0284c7"),
            createLegendItem("VIP Balkon", "#fde047", "#d97706"),
            createLegendItem("Innenraum (Stehplätze)", "#e0e7ff", "#2563eb"),
            createLegendItem("Bühne", "#0f172a", "#334155")
        );

        mapWrapper.getChildren().addAll(grid, legend);
        mapContainer.getChildren().add(mapWrapper);
        return mapContainer;
    }

    private Button createBlockButton(String name, String bgHex, String borderHex, String textHex, String subtitle, double width, double height) {
        Button btn = new Button();
        btn.setPrefSize(width, height);

        String sectionLookupName = name.contains("Innenraum") ? "Innenraum" : name;
        Section sec = app.findSectionByName(sectionLookupName);
        double priceFactor = sec != null ? sec.getPriceFactor() : 1.0;
        double calcPrice = app.getCurrentSelectedEvent().getBasePrice() * priceFactor;

        VBox content = new VBox(3);
        content.setAlignment(Pos.CENTER);

        Label nameLbl = new Label(name.toUpperCase());
        nameLbl.setFont(Font.font("System", FontWeight.BOLD, 14));
        nameLbl.setTextFill(Color.web(textHex));

        Label subLbl = new Label(subtitle);
        subLbl.setFont(Font.font("System", 11));
        subLbl.setTextFill(Color.web(textHex));

        Label priceLbl = new Label(String.format("%.2f €", calcPrice));
        priceLbl.setFont(Font.font("System", FontWeight.BOLD, 13));
        priceLbl.setTextFill(Color.web(borderHex));

        content.getChildren().addAll(nameLbl, subLbl, priceLbl);
        btn.setGraphic(content);

        btn.setStyle(String.format(BLOCK_BUTTON_STYLE, bgHex, borderHex));

        btn.setOnAction(e -> {
            if (sec == null) {
                app.showAlert(Alert.AlertType.WARNING, "Fehler", "Der Bereich '" + name + "' konnte im Event nicht gefunden werden.");
                return;
            }

            app.setCurrentSelectedSection(sec);
            if (sec instanceof StandingSection) {
                app.navigateTo(ScreenManager.Screen.STANDING_AREA_SELECTION);
            } else {
                app.navigateTo(ScreenManager.Screen.SEAT_SELECTION);
            }
        });

        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle().replace(bgHex, "#ffffff")));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace("#ffffff", bgHex)));

        if (sec != null) {
            Tooltip tooltip = new Tooltip(String.format("%s\nTicketpreis: %.2f €", sec.getName(), calcPrice));
            Tooltip.install(btn, tooltip);
        }

        return btn;
    }

    private HBox createLegendItem(String labelText, String bgHex, String borderHex) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER);

        Rectangle r = new Rectangle(14, 14, Color.web(bgHex));
        r.setArcWidth(4);
        r.setArcHeight(4);
        r.setStroke(Color.web(borderHex));
        r.setStrokeWidth(1.5);

        Label lbl = new Label(labelText);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 12));
        lbl.setTextFill(Color.web("#475569"));

        box.getChildren().addAll(r, lbl);
        return box;
    }

    private StackPane createArenaLayout() {
        StackPane mapContainer = new StackPane();
        
        VBox mapWrapper = new VBox(20);
        mapWrapper.setAlignment(Pos.CENTER);
        mapWrapper.setPadding(new Insets(24));
        mapWrapper.setStyle(CARD_STYLE);

        // GRID OF SECTIONS
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        // Wir berechnen die Breite des Spielfelds passend zu Block 1 & 2 (235 + 235 + 12 Lücke = 482)
        double blockWidth = 235;
        double blockHeight = 95;
        double centerHeight = 200;

        // TOP ROW: Block 2 & Block 1
        Button block2Btn = createBlockButton("Block 2", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", blockWidth, blockHeight);
        Button block1Btn = createBlockButton("Block 1", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", blockWidth, blockHeight);

        // MIDDLE ROW: Block 6, VIP Balkon, SPIELFELD, Block 5
        Button block6Btn = createBlockButton("Block 6", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 100, centerHeight);
        Button vipBtn = createBlockButton("VIP", "#fde047", "#d97706", "#78350f", "VIP BALKON", 70, centerHeight);
        
        // SPIELFELD (Visuelles Element, nicht klickbar)
        StackPane courtBox = new StackPane();
        Rectangle courtRect = new Rectangle(482, centerHeight, Color.web("#fef3c7")); // Helle Holz-Farbe (Parkett)
        courtRect.setArcWidth(14);
        courtRect.setArcHeight(14);
        courtRect.setStroke(Color.web("#d97706")); // Dunkelorange Begrenzungslinie
        courtRect.setStrokeWidth(3);

        // Arena-Flair: Mittellinie und Mittelkreis
        Rectangle centerLine = new Rectangle(3, centerHeight, Color.web("#d97706"));
        javafx.scene.shape.Circle centerCircle = new javafx.scene.shape.Circle(25, Color.TRANSPARENT);
        centerCircle.setStroke(Color.web("#d97706"));
        centerCircle.setStrokeWidth(3);

        Label courtLabel = new Label("VERANSTAlTUNGSFLÄCHE");
        courtLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        courtLabel.setTextFill(Color.web("#b45309")); // Dunkelbraun/Orange für Text
        courtLabel.setTranslateY(-centerHeight / 2 + 60); // Positioniert den Text oben im Spielfeld
        courtLabel.setTranslateX(-4); // Positioniert den Text leicht links

        // Alles übereinanderlegen
        courtBox.getChildren().addAll(courtRect, centerLine, centerCircle, courtLabel);

        // RECHTE SEITE: Block 5 (statt der Bühne)
        Button block5Btn = createBlockButton("Block 5", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 100, centerHeight);

        // BOTTOM ROW: Block 3 & Block 4
        Button block3Btn = createBlockButton("Block 3", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", blockWidth, blockHeight);
        Button block4Btn = createBlockButton("Block 4", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", blockWidth, blockHeight);

        // GRID ASSEMBLY
        // Reihe 0: Block 2 und 1
        grid.add(block2Btn, 2, 0);
        grid.add(block1Btn, 3, 0);

        // Reihe 1: Block 6, VIP, Spielfeld (nimmt 2 Spalten ein), Block 5
        grid.add(block6Btn, 0, 1);
        grid.add(vipBtn, 1, 1);
        grid.add(courtBox, 2, 1, 2, 1); // Spans cols 2 & 3
        grid.add(block5Btn, 4, 1);

        // Reihe 2: Block 3 und 4
        grid.add(block3Btn, 2, 2);
        grid.add(block4Btn, 3, 2);

        // Legende (Angepasst für Arena)
        HBox legend = new HBox(28);
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(10, 0, 0, 0));
        legend.getChildren().addAll(
            createLegendItem("Sitzplatz Blöcke", "#bae6fd", "#0284c7"),
            createLegendItem("VIP Balkon", "#fde047", "#d97706"),
            createLegendItem("Veranstaltungsfläche", "#fef3c7", "#d97706")
        );

        mapWrapper.getChildren().addAll(grid, legend);
        mapContainer.getChildren().add(mapWrapper);
        
        return mapContainer;
    }

    private StackPane createStageSeatedLayout() {
        StackPane mapContainer = new StackPane();

        VBox mapWrapper = new VBox(20);
        mapWrapper.setAlignment(Pos.CENTER);
        mapWrapper.setPadding(new Insets(24));
        mapWrapper.setStyle(CARD_STYLE);
// GRID OF SECTIONS
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        // TOP ROW: Block 2 & Block 1
        Button block2Btn = createBlockButton("Block 2", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 215+20, 85+10);
        Button block1Btn = createBlockButton("Block 1", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 215+20, 85+10);

        // MIDDLE ROW: Block 6, VIP Balkon, INNENRAUM, BÜHNE
        Button block6Btn = createBlockButton("Block 6", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 100, 180+20);
        Button vipBtn = createBlockButton("VIP", "#fde047", "#d97706", "#78350f", "VIP BALKON", 70, 180+20);
        Button blockInnerBtn = createBlockButton("Innenraum (Sitzplatz)", "#e0e7ff", "#2563eb", "#1d4ed8", "INNENRAUM (Sitzplätze)", 442, 180);
        //Button text= createBlockButton("Innenraum (Stehplatz)", "#e0e7ff", "#2563eb", "#1d4ed8", "INNENRAUM (Sitzplätze)", 442, 180);

        // BÜHNE Block
        StackPane stageBox = new StackPane();
        Rectangle stageRect = new Rectangle(90, 180, Color.web("#0f172a"));
        stageRect.setArcWidth(14);
        stageRect.setArcHeight(14);
        stageRect.setStroke(Color.web("#334155"));
        stageRect.setStrokeWidth(2);

        Label stageLabel = new Label("B Ü H N E");
        stageLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        stageLabel.setTextFill(Color.WHITE);
        stageLabel.setRotate(-90);
        stageBox.getChildren().addAll(stageRect, stageLabel);

        // BOTTOM ROW: Block 3 & Block 4
        Button block3Btn = createBlockButton("Block 3", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 215+20, 85+10);
        Button block4Btn = createBlockButton("Block 4", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 215, 85+10);

        // GRID ASSEMBLY
        grid.add(block2Btn, 2, 0);
        grid.add(block1Btn, 3, 0);

        grid.add(block6Btn, 0, 1);
        grid.add(vipBtn, 1, 1);
        grid.add(blockInnerBtn, 2, 1, 2, 1); // Spans cols 2 & 3
        grid.add(stageBox, 4, 1);

        grid.add(block3Btn, 2, 2);
        grid.add(block4Btn, 3, 2);

        // Legend
        HBox legend = new HBox(28);
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(10, 0, 0, 0));
        legend.getChildren().addAll(
            createLegendItem("Sitzplatz Blöcke", "#bae6fd", "#0284c7"),
            createLegendItem("VIP Balkon", "#fde047", "#d97706"),
            createLegendItem("Innenraum (Sitzplätze)", "#e0e7ff", "#2563eb"),
            createLegendItem("Bühne", "#0f172a", "#334155")
        );
        mapWrapper.getChildren().addAll(grid, legend);
        mapContainer.getChildren().add(mapWrapper);

        return mapContainer;
    }
} 



