package ui;

/**
 * Stellt zentral definierte JavaFX-Styles bereit, die von mehreren Screens
 * verwendet werden. Die gemeinsame Ablage verhindert abweichende Farben und
 * Formatierungen bei wiederkehrenden Oberflächenelementen.
 */
public class UIStyles {

    /** Primärfarbe der Anwendung für hervorgehobene Flächen und Bedienelemente. */
    public static final String COLOR_PRIMARY = "#2c3e50";

    /** Einheitlicher Stil für die Bühnen- beziehungsweise Spielfeldanzeige. */
    public static final String STAGE_LABEL_STYLE = 
            "-fx-background-color: " + COLOR_PRIMARY + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12 0 12 0; " +
            "-fx-alignment: center; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #1a252f; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 5, 0, 0, 2);";
}
