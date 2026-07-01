package controller;

import domain.Seat;
import domain.SeatedSection;
import domain.Section;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ui.App;

public class SeatSelectionController {

    private final GridPane seatGrid;
    private final App mainApp;
    private Seat selectedSeat = null;
    private Button selectedButton = null;

    public SeatSelectionController(GridPane seatGrid, App mainApp) {
        this.seatGrid = seatGrid;
        this.mainApp = mainApp;
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

                    // 1. Bereits gebuchte Sitze rot markieren und deaktivieren
                    if (seat.isBooked()) {
                        seatButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white");
                        seatButton.setDisable(true);
                    } else {
                        // 2. Freie Plätze grün markieren und auswählbar machen
                        seatButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white");

                        seatButton.setOnAction(event -> {
                            // Deselektieren des zuvor ausgewählten Sitzes
                            if (selectedSeat == seat) {
                                selectedButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white");
                                selectedSeat = null;
                                selectedButton = null;
                                mainApp.updateSelectionLabel("Kein Platz ausgewählt");
                            } else {
                                if (selectedSeat != null) {
                                    selectedButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white");
                                }
                            }

                            // Selektieren des neuen Sitzes
                            seatButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white");
                            selectedSeat = seat;
                            selectedButton = seatButton;
                            mainApp.updateSelectionLabel("Sitz gewählt: Reihe " + seat.getRowNumber() + ", Platz " + seat.getSeatNumber());
                        });
                    }

                    seatGrid.add(seatButton, s, r);
                }
            }

        }

    }

    public Seat getSelectedSeat() {
        return selectedSeat;
    }
}
