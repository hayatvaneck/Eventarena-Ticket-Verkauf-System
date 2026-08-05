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
import ui.screens.BaseScreen;

/**
 * Die Klasse GraphicSectionSelectionScreen zeigt den grafischen Saalplan und
 * ermöglicht die Bereichsauswahl.
 * 
 */

public class GraphicSectionSelectionScreen extends BaseScreen {

    /** Hintergrundstil des gesamten Screens. */
    private static final String ROOT_STYLE = "-fx-background-color: #f5f5f7;";

    /** Stil der Karte, in der der jeweilige Hallenplan dargestellt wird. */
    private static final String CARD_STYLE = "-fx-background-color: #ffffff; -fx-background-radius: 16; -fx-border-color: #cbd5e1; -fx-border-radius: 16; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.04), 8, 0, 0, 2);";

    /** Formatvorlage für anklickbare Bereichsschaltflächen im Hallenplan. */
    private static final String BLOCK_BUTTON_STYLE = "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 2; -fx-border-radius: 12; -fx-background-radius: 12; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 4, 0, 0, 1);";

    /** Anwendungskontext für Eventzustand, Bereichssuche und Navigation. */
    private final App app;

    /**
     * Erstellt die grafische Bereichsauswahl des gewählten Events.
     *
     * @param app zentraler Anwendungskontext
     */
    public GraphicSectionSelectionScreen(App app) {
        this.app = app;
    }

    /**
     * Wählt anhand des Hallenplantyps das passende Layout und baut den
     * vollständigen Auswahl-Screen auf.
     *
     * @return vollständige Szene der grafischen Bereichsauswahl
     */
    @Override
    public Scene buildScene() {
        if (app.getCurrentSelectedEvent() == null) {
            VBox emptyRoot = createRoot(10, new Insets(20), Pos.CENTER);
            emptyRoot.getChildren().add(new Label("Kein Event ausgewählt."));
            return createDefaultScene(emptyRoot);
        }

        javafx.scene.layout.BorderPane root = new javafx.scene.layout.BorderPane();
        root.setStyle(ROOT_STYLE);

        // Zentraler Inhalt aus Eventtitel und typabhängigem Hallenplan.
        VBox topBox = createRoot(25, new Insets(30, 30, 20, 30), Pos.TOP_CENTER);

        VBox headerBox = createHeaderBox(app.getCurrentSelectedEvent().getTitle(), "Wählen Sie einen Block aus:");

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

        topBox.getChildren().addAll(headerBox, mapContainer);

        // Fester Rückweg mit Layoutplatzhalter für eine symmetrische Ausrichtung.
        VBox bottomBox = createRoot(10, new Insets(0, 30, 30, 30), Pos.BOTTOM_CENTER);

        Button backButton = createBackButton("Zurück zu den Events");
        backButton.setPrefWidth(300);
        backButton.setMinHeight(45);
        backButton.setMaxHeight(45);
        backButton.setOnAction(e -> app.navigateTo(ScreenManager.Screen.MAIN_MENU));

        HBox dummyFooter = createInvisibleStandardFooter();

        bottomBox.getChildren().addAll(backButton, dummyFooter);

        // Zusammensetzen des Inhalts- und Navigationsbereichs.
        root.setCenter(topBox);
        root.setBottom(bottomBox);

        return createDefaultScene(root);
    }

    /**
     * Erstellt den Hallenplan für Bühnenveranstaltungen mit Stehplätzen im
     * Innenraum.
     *
     * @return grafisch aufgebauter Hallenplan
     */
    private StackPane createStageStandingLayout() {
        StackPane mapContainer = new StackPane();
        VBox mapWrapper = new VBox(20);
        mapWrapper.setAlignment(Pos.CENTER);
        mapWrapper.setPadding(new Insets(24));
        mapWrapper.setStyle(CARD_STYLE);

        // Raster der auswählbaren Hallenbereiche.
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        // Obere Reihe: Block 2 und Block 1.
        Button block2Btn = createBlockButton("Block 2", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 215 + 20,
                85 + 10);
        Button block1Btn = createBlockButton("Block 1", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 215 + 20,
                85 + 10);

        // Mittlere Reihe: Block 6, VIP-Balkon, Innenraum und Bühne.
        Button block6Btn = createBlockButton("Block 6", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 100, 180 + 20);
        Button vipBtn = createBlockButton("VIP", "#fde047", "#d97706", "#78350f", "VIP BALKON", 70, 180);
        Button standingBtn = createBlockButton("Innenraum (Stehplatz)", "#e0e7ff", "#2563eb", "#1d4ed8",
                "INNENRAUM (Stehplätze)", 482, 200);

        // Die Bühne dient nur der Orientierung und ist nicht auswählbar.
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

        // Untere Reihe: Block 3 und Block 4.
        Button block3Btn = createBlockButton("Block 3", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 215 + 20,
                85 + 10);
        Button block4Btn = createBlockButton("Block 4", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 215, 85 + 10);

        // Positionierung der Bereiche im Hallenraster.
        grid.add(block2Btn, 2, 0);
        grid.add(block1Btn, 3, 0);

        grid.add(block6Btn, 0, 1);
        grid.add(vipBtn, 1, 1);
        grid.add(standingBtn, 2, 1, 2, 1); // Der Innenraum belegt zwei Spalten.
        grid.add(stageBox, 4, 1);

        grid.add(block3Btn, 2, 2);
        grid.add(block4Btn, 3, 2);

        // Farblegende des Hallenplans.
        HBox legend = new HBox(28);
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(10, 0, 0, 0));
        legend.getChildren().addAll(
                createLegendItem("Sitzplatz Blöcke", "#bae6fd", "#0284c7"),
                createLegendItem("VIP Balkon", "#fde047", "#d97706"),
                createLegendItem("Innenraum (Stehplätze)", "#e0e7ff", "#2563eb"),
                createLegendItem("Bühne", "#0f172a", "#334155"));

        mapWrapper.getChildren().addAll(grid, legend);
        mapContainer.getChildren().add(mapWrapper);
        return mapContainer;
    }

    /**
     * Erstellt einen auswählbaren Hallenblock inklusive Typ, Preis, Tooltip und
     * passender Folgeschritt-Navigation.
     *
     * @param name fachlicher Name des Bereichs
     * @param bgHex Hintergrundfarbe
     * @param borderHex Rahmen- und Preisfarbe
     * @param textHex Textfarbe
     * @param subtitle erklärende Bereichsart
     * @param width Breite der Schaltfläche
     * @param height Höhe der Schaltfläche
     * @return konfigurierte Bereichsschaltfläche
     */
    private Button createBlockButton(String name, String bgHex, String borderHex, String textHex, String subtitle,
            double width, double height) {
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
                app.showAlert(Alert.AlertType.WARNING, "Fehler",
                        "Der Bereich '" + name + "' konnte im Event nicht gefunden werden.");
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

    /**
     * Erstellt einen farblich codierten Eintrag für die Hallenplanlegende.
     *
     * @param labelText Beschreibung der Flächenart
     * @param bgHex Flächenfarbe des Symbols
     * @param borderHex Rahmenfarbe des Symbols
     * @return formatierter Legendeneintrag
     */
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

    /**
     * Erstellt einen umlaufenden Arena-Hallenplan mit zentralem Spielfeld.
     *
     * @return grafisch aufgebauter Arena-Hallenplan
     */
    private StackPane createArenaLayout() {
        StackPane mapContainer = new StackPane();

        VBox mapWrapper = new VBox(20);
        mapWrapper.setAlignment(Pos.CENTER);
        mapWrapper.setPadding(new Insets(24));
        mapWrapper.setStyle(CARD_STYLE);

        // Raster der auswählbaren Hallenbereiche.
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        // Zwei Blockbreiten zuzüglich Rasterabstand ergeben die Spielfeldbreite.
        double blockWidth = 235;
        double blockHeight = 95;
        double centerHeight = 200;

        // Obere Reihe: Block 2 und Block 1.
        Button block2Btn = createBlockButton("Block 2", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", blockWidth,
                blockHeight);
        Button block1Btn = createBlockButton("Block 1", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", blockWidth,
                blockHeight);

        // Mittlere Reihe: Block 6, VIP-Balkon, Spielfeld und Block 5.
        Button block6Btn = createBlockButton("Block 6", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 100,
                centerHeight);
        Button vipBtn = createBlockButton("VIP", "#fde047", "#d97706", "#78350f", "VIP BALKON", 70, 180);

        // Das Spielfeld dient als nicht auswählbare Orientierungshilfe.
        StackPane courtBox = new StackPane();
        Rectangle courtRect = new Rectangle(482, centerHeight, Color.web("#fef3c7"));
        courtRect.setArcWidth(14);
        courtRect.setArcHeight(14);
        courtRect.setStroke(Color.web("#d97706"));
        courtRect.setStrokeWidth(3);

        // Mittellinie und Mittelkreis verdeutlichen das Arena-Layout.
        Rectangle centerLine = new Rectangle(3, centerHeight, Color.web("#d97706"));
        javafx.scene.shape.Circle centerCircle = new javafx.scene.shape.Circle(25, Color.TRANSPARENT);
        centerCircle.setStroke(Color.web("#d97706"));
        centerCircle.setStrokeWidth(3);

        Label courtLabel = new Label("S P I E L F E L D");
        courtLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        courtLabel.setTextFill(Color.web("#b45309"));
        courtLabel.setTranslateY(-centerHeight / 2 + 60);
        courtLabel.setTranslateX(-4);

        // Alle Spielfeldbestandteile werden innerhalb des StackPane überlagert.
        courtBox.getChildren().addAll(courtRect, centerLine, centerCircle, courtLabel);

        // Block 5 schließt das Arena-Layout auf der rechten Seite ab.
        Button block5Btn = createBlockButton("Block 5", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 100,
                centerHeight);

        // Untere Reihe: Block 3 und Block 4.
        Button block3Btn = createBlockButton("Block 3", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", blockWidth,
                blockHeight);
        Button block4Btn = createBlockButton("Block 4", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", blockWidth,
                blockHeight);

        // Positionierung der Bereiche im Arena-Raster.
        grid.add(block2Btn, 2, 0);
        grid.add(block1Btn, 3, 0);

        // Das Spielfeld belegt in der mittleren Reihe zwei Spalten.
        grid.add(block6Btn, 0, 1);
        grid.add(vipBtn, 1, 1);
        grid.add(courtBox, 2, 1, 2, 1);
        grid.add(block5Btn, 4, 1);

        grid.add(block3Btn, 2, 2);
        grid.add(block4Btn, 3, 2);

        // Farblegende des Arena-Hallenplans.
        HBox legend = new HBox(28);
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(10, 0, 0, 0));
        legend.getChildren().addAll(
                createLegendItem("Sitzplatz Blöcke", "#bae6fd", "#0284c7"),
                createLegendItem("Balkon", "#fde047", "#d97706"),
                createLegendItem("Veranstaltungsfläche", "#fef3c7", "#d97706"));

        mapWrapper.getChildren().addAll(grid, legend);
        mapContainer.getChildren().add(mapWrapper);

        return mapContainer;
    }

    /**
     * Erstellt den Hallenplan für Bühnenveranstaltungen mit bestuhltem Innenraum.
     *
     * @return grafisch aufgebauter Hallenplan
     */
    private StackPane createStageSeatedLayout() {
        StackPane mapContainer = new StackPane();

        VBox mapWrapper = new VBox(20);
        mapWrapper.setAlignment(Pos.CENTER);
        mapWrapper.setPadding(new Insets(24));
        mapWrapper.setStyle(CARD_STYLE);
        // Raster der auswählbaren Hallenbereiche.
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        // Obere Reihe: Block 2 und Block 1.
        Button block2Btn = createBlockButton("Block 2", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 215 + 20,
                85 + 10);
        Button block1Btn = createBlockButton("Block 1", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 215 + 20,
                85 + 10);

        // Mittlere Reihe: Block 6, VIP-Balkon, Innenraum und Bühne.
        Button block6Btn = createBlockButton("Block 6", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 100, 180 + 20);
        Button vipBtn = createBlockButton("VIP", "#fde047", "#d97706", "#78350f", "VIP BALKON", 70, 180);
        Button blockInnerBtn = createBlockButton("Innenraum (Sitzplatz)", "#e0e7ff", "#2563eb", "#1d4ed8",
                "INNENRAUM (Sitzplätze)", 482, 200);
        // Die Bühne dient nur der Orientierung und ist nicht auswählbar.
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

        // Untere Reihe: Block 3 und Block 4.
        Button block3Btn = createBlockButton("Block 3", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 235, 85 + 10);
        Button block4Btn = createBlockButton("Block 4", "#bae6fd", "#0284c7", "#0369a1", "Sitzplätze", 235, 85 + 10);

        // Positionierung der Bereiche im Hallenraster.
        grid.add(block2Btn, 2, 0);
        grid.add(block1Btn, 3, 0);

        grid.add(block6Btn, 0, 1);
        grid.add(vipBtn, 1, 1);
        grid.add(blockInnerBtn, 2, 1, 2, 1); // Der Innenraum belegt zwei Spalten.
        grid.add(stageBox, 4, 1);

        grid.add(block3Btn, 2, 2);
        grid.add(block4Btn, 3, 2);

        // Farblegende des Hallenplans.
        HBox legend = new HBox(28);
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(10, 0, 0, 0));
        legend.getChildren().addAll(
                createLegendItem("Sitzplatz Blöcke", "#bae6fd", "#0284c7"),
                createLegendItem("Balkon", "#fde047", "#d97706"),
                createLegendItem("Innenraum (Sitzplätze)", "#e0e7ff", "#2563eb"),
                createLegendItem("Bühne", "#0f172a", "#334155"));
        mapWrapper.getChildren().addAll(grid, legend);
        mapContainer.getChildren().add(mapWrapper);

        return mapContainer;
    }
}
