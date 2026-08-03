package ui.screens;

import domain.Seat;
import domain.Section;
import domain.CartItem;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.VBox;
import ui.App;
import ui.ScreenManager;

/**
 * Die Klasse StandingAreaSelectionScreen erfasst die Anzahl von
 * Stehplatz-Tickets für den Warenkorb.
 * 
 */

public class StandingAreaSelectionScreen extends BaseScreen {

    private final App app;

    public StandingAreaSelectionScreen(App app) {
        this.app = app;
    }

    @Override
    public Scene buildScene() {
        int currentCartSize = app.getCartItems().size();
        int maxAllowed = 10 - currentCartSize;

        // Wenn der Warenkorb schon voll ist, direkt zurück in den Warenkorb leiten
        if (maxAllowed <= 0) {
            app.showAlert(Alert.AlertType.WARNING, "Warenkorb voll",
                    "Sie haben bereits die maximale Anzahl von 10 Tickets im Warenkorb.");
            app.navigateTo(ScreenManager.Screen.CART);
            return createDefaultScene(createRoot(10, Insets.EMPTY, Pos.CENTER));
        }

        Section selectedSection = app.getCurrentSelectedSection();

        VBox root = createRoot(20, new Insets(30), Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f6fa;");

        Label title = createTitle("Stehplatz-Auswahl: " + selectedSection.getName());
        Label instruction = createSubtitle("Bitte wählen Sie die Anzahl (max. " + maxAllowed + " weitere möglich):");

        // Spinner wird direkt auf das errechnete Limit gesetzt!
        Spinner<Integer> ticketSpinner = new Spinner<>(1, maxAllowed, 1);
        ticketSpinner.setStyle("-fx-font-size: 16px;");
        ticketSpinner.setPrefWidth(100);

        Button confirmButton = createConfirmButton("Auswahl bestätigen");
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
        backButton.setPrefWidth(200);
        backButton.setOnAction(e -> app.navigateTo(ScreenManager.Screen.GRAPHIC_SECTION_SELECTION));

        root.getChildren().addAll(title, instruction, ticketSpinner, confirmButton, backButton);

        return createDefaultScene(root);
    }
}
