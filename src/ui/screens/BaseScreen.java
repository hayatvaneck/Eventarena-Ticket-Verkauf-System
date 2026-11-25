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
 * Die abstrakte Klasse BaseScreen stellt gemeinsame UI-Helfer fuer alle
 * JavaFX-Screens bereit.
 * 
 */

public abstract class BaseScreen {

    /** Standardbreite für alle Hauptszenen der Anwendung. */
    protected static final double DEFAULT_WIDTH = 800;

    /** Standardhöhe für alle Hauptszenen der Anwendung. */
    protected static final double DEFAULT_HEIGHT = 700;

    /** Einheitliche Hintergrundfarbe der Hauptansichten. */
    private static final String COLOR_BG = "#f5f5f7";

    /** Einheitliche Textfarbe für Titel und reguläre Beschriftungen. */
    private static final String COLOR_TEXT = "#2c3e50";

    /** Stil für die Hauptüberschrift eines Screens. */
    protected static final String TITLE_STYLE = "-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: "
            + COLOR_TEXT + ";";

    /** Stil für erklärende Unterüberschriften. */
    protected static final String SUBTITLE_STYLE = "-fx-font-size: 14px; -fx-text-fill: #2c3e50;";

    /** Stil für primäre Aktionen wie Bestätigen oder Fortfahren. */
    protected static final String CONFIRM_BUTTON_STYLE = "-fx-background-color: #2c3e50;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 20 10 20;" +
            "-fx-background-radius: 6px;" +
            "-fx-cursor: hand;";

    /** Stil für Schaltflächen, die zur vorherigen Ansicht zurückführen. */
    protected static final String BACK_BUTTON_STYLE = "-fx-background-color: #2c3e50;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10 20 10 20;" +
            "-fx-background-radius: 6px;";

    /** Stil für Schaltflächen innerhalb eines Auswahlvorgangs. */
    protected static final String SELECTING_BUTTON_STYLE = "-fx-background-color: #d4af37;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10 20 10 20;" +
            "-fx-background-radius: 6px;" +
            "-fx-cursor: hand;";

    /** Stil für ergänzende, nicht primäre Aktionen. */
    protected static final String SECONDARY_BUTTON_STYLE = "-fx-background-color: #2c3e50;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6px;" +
            "-fx-cursor: hand;";

    /** Stil für destruktive oder besonders kritische Aktionen. */
    protected static final String DANGER_BUTTON_STYLE = "-fx-background-color: #e74c3c;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6px;" +
            "-fx-cursor: hand;";

    /**
     * Erzeugt die vollständige JavaFX-Szene des konkreten Screens.
     *
     * @return anzeigefertige Szene
     */
    public abstract Scene buildScene();

    /**
     * Erstellt den einheitlich eingefärbten Wurzelcontainer eines Screens.
     *
     * @param spacing Abstand zwischen den Kindknoten
     * @param padding Innenabstand des Containers
     * @param alignment Ausrichtung der Kindknoten
     * @return vorkonfigurierter vertikaler Container
     */
    protected VBox createRoot(double spacing, Insets padding, Pos alignment) {
        VBox root = new VBox(spacing);
        root.setPadding(padding);
        root.setAlignment(alignment);
        root.setStyle("-fx-background-color: " + COLOR_BG + ";");
        return root;
    }

    /**
     * Erstellt einen vertikalen Hilfscontainer.
     *
     * @param spacing Abstand zwischen den Kindknoten
     * @param alignment Ausrichtung der Kindknoten
     * @return konfigurierter VBox-Container
     */
    protected VBox createVBox(double spacing, Pos alignment) {
        VBox box = new VBox(spacing);
        box.setAlignment(alignment);
        return box;
    }

    /**
     * Erstellt einen horizontalen Hilfscontainer.
     *
     * @param spacing Abstand zwischen den Kindknoten
     * @param alignment Ausrichtung der Kindknoten
     * @return konfigurierter HBox-Container
     */
    protected HBox createHBox(double spacing, Pos alignment) {
        HBox box = new HBox(spacing);
        box.setAlignment(alignment);
        return box;
    }

    /**
     * Erstellt eine einheitlich formatierte Hauptüberschrift.
     *
     * @param text anzuzeigender Titel
     * @return formatierte Beschriftung
     */
    protected Label createTitle(String text) {
        Label label = new Label(text);
        label.setStyle(TITLE_STYLE);
        return label;
    }

    /**
     * Erstellt eine einheitlich formatierte Unterüberschrift.
     *
     * @param text anzuzeigender Untertitel
     * @return formatierte Beschriftung
     */
    protected Label createSubtitle(String text) {
        Label label = new Label(text);
        label.setStyle(SUBTITLE_STYLE);
        return label;
    }

    /**
     * Erstellt eine Schaltfläche für eine primäre Bestätigungsaktion.
     *
     * @param text Beschriftung der Schaltfläche
     * @return formatierte Schaltfläche
     */
    protected Button createConfirmButton(String text) {
        Button button = new Button(text);
        button.setStyle(CONFIRM_BUTTON_STYLE);
        return button;
    }

    /**
     * Erstellt eine hervorgehobene Schaltfläche für Auswahlaktionen.
     *
     * @param text Beschriftung der Schaltfläche
     * @return formatierte Schaltfläche
     */
    protected Button createSelectingButton(String text) {
        Button button = new Button(text);
        button.setStyle(SELECTING_BUTTON_STYLE);
        return button;
    }

    /**
     * Erstellt eine Schaltfläche für eine nachgeordnete Aktion.
     *
     * @param text Beschriftung der Schaltfläche
     * @return formatierte Schaltfläche
     */
    protected Button createSecondaryButton(String text) {
        Button button = new Button(text);
        button.setStyle(SECONDARY_BUTTON_STYLE);
        return button;
    }

    /**
     * Erstellt eine rot hervorgehobene Schaltfläche für kritische Aktionen.
     *
     * @param text Beschriftung der Schaltfläche
     * @return formatierte Schaltfläche
     */
    protected Button createDangerButton(String text) {
        Button button = new Button(text);
        button.setStyle(DANGER_BUTTON_STYLE);
        return button;
    }

    /**
     * Erstellt eine einheitliche Zurück-Schaltfläche.
     *
     * @param text Beschriftung der Schaltfläche
     * @return formatierte Schaltfläche
     */
    protected Button createBackButton(String text) {
        Button button = createConfirmButton(text);
        button.setStyle(BACK_BUTTON_STYLE + "-fx-cursor: hand;");

        return button;
    }

    /**
     * Erstellt einen optisch zurückgenommenen Hinweistext.
     *
     * @param text anzuzeigender Hinweis
     * @return formatierte Beschriftung
     */
    protected Label createMutedInfoLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-style: italic; -fx-text-fill: #2c3e50; -fx-font-size: 14px;");
        return label;
    }

    /**
     * Bettet Inhalte in eine transparente, horizontal angepasste Scroll-Fläche
     * ein.
     *
     * @param content scrollbar darzustellender Inhalt
     * @return vorkonfigurierte ScrollPane
     */
    protected ScrollPane createTransparentScrollPane(Parent content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
                "-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        return scrollPane;
    }

    /**
     * Erzeugt einen flexibel wachsenden horizontalen Abstandshalter.
     *
     * @return leerer, horizontal wachsender Bereich
     */
    protected Region createHorizontalSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    /**
     * Erzeugt einen flexibel wachsenden vertikalen Abstandshalter.
     *
     * @return leerer, vertikal wachsender Bereich
     */
    protected Region createVerticalSpacer() {
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    /**
     * Verpackt den Wurzelknoten in eine Szene mit den Standardabmessungen.
     *
     * @param root Wurzelknoten des Screens
     * @return Szene in Standardgröße
     */
    protected Scene createDefaultScene(Parent root) {
        return new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Erstellt die standardisierte Fußzeile mit den Namen des Projektteams.
     *
     * @return sichtbare Fußzeile
     */
    protected HBox createStandardFooter() {
        Label teamLabel = new Label("Entwickelt von: Lukas Beck, Maren Bohlig, Gian-Luca Levels, Hayat van Eck");
        teamLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-style: italic;");

        HBox footerBar = new HBox();
        footerBar.setAlignment(Pos.BOTTOM_RIGHT);
        footerBar.setPadding(new Insets(10, 0, 0, 0));
        footerBar.getChildren().add(teamLabel);
        return footerBar;
    }

    /**
     * Erstellt eine unsichtbare Fußzeile, die ihre Abmessungen zur symmetrischen
     * Ausrichtung anderer Inhalte beibehält.
     *
     * @return unsichtbare Fußzeile mit Standardmaßen
     */
    protected HBox createInvisibleStandardFooter() {
        HBox footerBar = createStandardFooter();
        footerBar.setVisible(false); // Die Abmessungen bleiben als Layoutplatzhalter erhalten.
        return footerBar;
    }

    /**
     * Erstellt einen unsichtbaren Platzhalter in der Höhe des regulären Headers.
     *
     * @return unsichtbarer Header-Platzhalter
     */
    protected HBox createInvisibleHeader() {
        HBox dummyHeader = createHBox(15, Pos.CENTER_RIGHT);
        // Identische Innenabstände verhindern ein Springen des mittleren Inhalts.
        dummyHeader.setPadding(new Insets(10, 15, 10, 15));

        // Ein Platzhalter-Button erzeugt dieselbe Höhe wie die regulären Header-Buttons.
        Button dummyButton = new Button("Platzhalter");

        dummyHeader.getChildren().add(dummyButton);
        dummyHeader.setVisible(false); // Der belegte Layoutbereich bleibt erhalten.

        return dummyHeader;
    }

    /**
     * Erstellt einen hervorgehobenen Kopfbereich aus Titel und Untertitel.
     *
     * @param titleText Text der Hauptüberschrift
     * @param subtitleText erklärender Text unter der Hauptüberschrift
     * @return formatierter Kopfbereich
     */
    protected VBox createHeaderBox(String titleText, String subtitleText) {
        Label title = createTitle(titleText);
        Label subtitle = createSubtitle(subtitleText);

        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setMaxWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        headerBox.setStyle(
                "-fx-background-color: white; " +
                        "-fx-padding: 15 30 15 30; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 5, 0, 0, 2);");
        headerBox.getChildren().addAll(title, subtitle);
        return headerBox;
    }
}
