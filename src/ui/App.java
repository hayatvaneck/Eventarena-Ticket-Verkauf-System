package ui;

import controller.SeatSelectionController;
import domain.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import repository.*;

public class App extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            EventRepository repo = EventRepository.getInstance();
            Event testEvent = repo.findById(1L);

            SeatedSection seatedSection = null;
            for (Section s : testEvent.getSections()) {
                if (s instanceof SeatedSection) {
                    seatedSection = (SeatedSection) s;
                    break;
                }
            }

            if (seatedSection == null) {
                System.out.println("Kein Sitzplatz-Block gefunden!");
                return;
            }

            GridPane seatGrid = new GridPane();
            seatGrid.setHgap(5);
            seatGrid.setVgap(5);

            SeatSelectionController controller = new SeatSelectionController(seatGrid);
            controller.populateSeatPlan(seatedSection);

            VBox root = new VBox(10);
            root.setStyle("-fx-padding: 20; -fx-alignment: center;");

            Label titleLabel = new Label("Bühne / Spielfeld ist HIER oben");
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

            root.getChildren().addAll(titleLabel, seatGrid);

            Scene scene = new Scene(root, 800, 600);
            primaryStage.setTitle("Arena Ticketsystem - Saalplan: " + seatedSection.getName());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
