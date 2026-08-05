package ui;

// Diese Klasse sammelt alle wiederkehrenden Designs der App
public class UIStyles {
    
    public static final String COLOR_PRIMARY = "#2c3e50";

    // Das ist dein langer Style für das Bühne/Spielfeld-Kästchen
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