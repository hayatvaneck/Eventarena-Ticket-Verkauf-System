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
                for (int s = 0; s < seatsPerRow; s++) {
                    Seat seat = seatedSection.getSeat(r + 1, s + 1);

                    Button seatButton = new Button();
                    seatButton.setPrefSize(40,5);

                    // Tooltip hinzufügen
                    javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(
                        "Reihe " + seat.getRowNumber() + ", Platz " + seat.getSeatNumber()
                    );
                    tooltip.setShowDelay(javafx.util.Duration.millis(100));
                    javafx.scene.control.Tooltip.install(seatButton, tooltip);

                    // 1. Bereits gebuchte Sitze rot markieren und deaktivieren
                    if (seat.isBooked()) {
                        seatButton.setStyle("-fx-background-color: #ff4d4d;");
                        seatButton.setDisable(true);
                    } else {
                        // 2. Freie Plätze grün markieren und auswählbar machen
                        seatButton.setStyle("-fx-background-color: #2c3e50;");

                        seatButton.setOnAction(event -> {
                            // Deselektieren des zuvor ausgewählten Sitzes
                            if (selectedSeats.contains(seat)) {
                                seatButton.setStyle("-fx-background-color: #2c3e50;");
                                selectedSeats.remove(seat);
                                selectedButtons.remove(seatButton);
                            } else {
                                seatButton.setStyle("-fx-background-color: #d4af37;");
                                selectedSeats.add(seat);
                                selectedButtons.add(seatButton);
                            }

                            updateStatusLabel();
                        });
                    }

                    seatGrid.add(seatButton, s, r);
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
                sb.append(String.format("| Reihe: %d, Platz: %d |", s.getRowNumber(), s.getSeatNumber()));
            }
            mainApp.updateSelectionLabel(sb.toString());
        }
    }

    public List<Seat> getSelectedSeats() {
        return selectedSeats;
    }
}