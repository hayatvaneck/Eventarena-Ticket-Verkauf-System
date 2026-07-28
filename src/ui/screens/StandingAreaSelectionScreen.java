package ui.screens;

import domain.Seat;
import domain.Section;
import domain.CartItem;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.VBox;
import ui.App;
import ui.ScreenManager;

/**
 * Die Klasse StandingAreaSelectionScreen erfasst die Anzahl von Stehplatz-Tickets für den Warenkorb.

 */

public class StandingAreaSelectionScreen extends BaseScreen {

    private final App app;

    public StandingAreaSelectionScreen(App app) {
        this.app = app;
    }

    @Override
    public Scene buildScene() {
        Section selectedSection = app.getCurrentSelectedSection();

        VBox root = createRoot(20, new Insets(30), Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f6fa;");

        Label header = new Label("Stehplatz-Auswahl: " + selectedSection.getName());
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label infoLabel = new Label("Bitte wählen Sie die Anzahl der gewünschten Stehplatz-Tickets aus.");
        infoLabel.setStyle("-fx-font-size: 14px;");

        Spinner<Integer> ticketSpinner = new Spinner<>(1, 10, 1);
        ticketSpinner.setStyle("-fx-font-size: 16px;");
        ticketSpinner.setPrefWidth(100);

        Button confirmButton = new Button("Auswahl bestätigen");
        confirmButton.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
        confirmButton.setPrefWidth(200);
        confirmButton.setOnAction(e -> {
            int count = ticketSpinner.getValue();
            for (int i = 1; i <= count; i++) {
                Seat seat = new Seat(0, i);
                seat.setSection(selectedSection);
                app.getCartItems().add(new CartItem(app.getCurrentSelectedEvent(), selectedSection, seat));
            }
            app.navigateTo(ScreenManager.Screen.CART);
        });

        Button backButton = createBackButton("Zurück zum Saalplan");
        backButton.setOnAction(e -> app.navigateTo(ScreenManager.Screen.GRAPHIC_SECTION_SELECTION));

        root.getChildren().addAll(header, infoLabel, ticketSpinner, confirmButton, backButton);
        return createDefaultScene(root);
    }
}



