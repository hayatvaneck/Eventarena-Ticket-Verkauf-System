package controller;

import domain.Seat;
import domain.SeatedSection;
import domain.Section;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class SeatSelectionController {

    private final GridPane seatGrid;

    public SeatSelectionController(GridPane seatGrid) {
        this.seatGrid = seatGrid;
    }

    public void populateSeatPlan(Section section) {
        seatGrid.getChildren().clear();

        if (section instanceof SeatedSection) {
            SeatedSection seatedSection = (SeatedSection) section;
            
            int rows = seatedSection.getRowCount();
            int seatsPerRow = seatedSection.getSeatsPerRow();

            for (int r = 0; r < rows; r++) {
                for (int s = 0; s < seatsPerRow; s++) {
                    Seat seat = seatedSection.getSeat(r + 1, s + 1);

                    Button seatButton = new Button((s + 1) + "");
                    seatButton.setPrefSize(40,40);

                    if (seat.isBooked()) {
                        seatButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white");
                        seatButton.setDisable(true);
                    } else {
                        seatButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white");

                        seatButton.setOnAction(event -> {
                            seatButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white");
                            System.out.println("Sitz gewählt: Reihe " + seat.getRowNumber() + ", Platz " + seat.getSeatNumber());
                        });
                    }

                    seatGrid.add(seatButton, s, r);
                }
            }

        }

    }
}
