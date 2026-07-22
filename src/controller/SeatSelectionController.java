package controller;

import domain.Seat;
import domain.SeatedSection;
import domain.Section;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import ui.App;

import java.util.ArrayList;
import java.util.List;

public class SeatSelectionController {

    private final GridPane seatGrid;
    private final App mainApp;

    private final List<Seat> selectedSeats = new ArrayList<>();
    private final List<Button> selectedButtons = new ArrayList<>();

    public SeatSelectionController(GridPane seatGrid, App mainApp) {
        this.seatGrid = seatGrid;
        this.mainApp = mainApp;
    }

    public void populateSeatPlan(Section section) {
        seatGrid.getChildren().clear();
        selectedSeats.clear();
        selectedButtons.clear();

        if (section instanceof SeatedSection) {
            SeatedSection seatedSection = (SeatedSection) section;
            
            int rows = seatedSection.getRowCount();
            int seatsPerRow = seatedSection.getSeatsPerRow();

            for (int r = 0; r < rows; r++) {
                // Text-Label für die Reihe (r + 1, da r bei 0 startet)
                javafx.scene.control.Label rowLabel = new javafx.scene.control.Label("Reihe " + (r + 1) + ": ");
                rowLabel.setStyle("-fx-font-weight: bold; -fx-padding: 0 10 0 0;");
                
                // Füge das Label in Spalte 0 der aktuellen Reihe ein
                seatGrid.add(rowLabel, 0, r);

                for (int s = 0; s < seatsPerRow; s++) {
                    Seat seat = seatedSection.getSeat(r + 1, s + 1);

                    Button seatButton = new Button((s + 1) + "");
                    seatButton.setPrefSize(40,5);

                    // 1. Bereits gebuchte Sitze rot markieren und deaktivieren
                    if (seat.isBooked()) {
                        seatButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white");
                        seatButton.setDisable(true);
                    } else {
                        // 2. Freie Plätze grün markieren und auswählbar machen
                        seatButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white");

                        seatButton.setOnAction(event -> {
                            // Deselektieren des zuvor ausgewählten Sitzes
                            if (selectedSeats.contains(seat)) {
                                seatButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white");
                                selectedSeats.remove(seat);
                                selectedButtons.remove(seatButton);
                            } else {
                                seatButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white");
                                selectedSeats.add(seat);
                                selectedButtons.add(seatButton);
                            }

                            updateStatusLabel();
                        });
                    }

                    seatGrid.add(seatButton, s+1, r);
                }
            }

        }

    }

    private void updateStatusLabel() {
        if (selectedSeats.isEmpty()) {
            mainApp.updateSelectionLabel("Keine Plätze ausgewählt");
        } else {
            StringBuilder sb = new StringBuilder("Ausgewählt: ");
            for (Seat s : selectedSeats) {
                sb.append(String.format("[R:%d, P:%d] ", s.getRowNumber(), s.getSeatNumber()));
            }
            mainApp.updateSelectionLabel(sb.toString());
        }
    }

    public List<Seat> getSelectedSeats() {
        return selectedSeats;
    }
}
