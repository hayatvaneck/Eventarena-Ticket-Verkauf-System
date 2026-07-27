package controller;

import domain.Seat;
import domain.SeatedSection;
import domain.Section;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
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

    public void populateSeatPlan(Section section, List<Seat> cartSeats) {
        seatGrid.getChildren().clear();
        selectedSeats.clear();
        selectedButtons.clear();

        if (section instanceof SeatedSection) {
            SeatedSection seatedSection = (SeatedSection) section;
            
            int totalRows = seatedSection.getRowCount();
            int seatsPerRow = seatedSection.getSeatsPerRow();

            for (int r = 0; r < totalRows; r++) {
                for (int s = 0; s < seatsPerRow; s++) {

                    Seat seat = seatedSection.getSeat(r + 1, s + 1);

                    if (seat == null) {
                        continue;
                    }

                    int rowNum = seat.getRowNumber() ;
                    int seatNum = seat.getSeatNumber();

                    boolean isBookedInRepo = seat.isBooked();

                    boolean isInCart = false;
                    if (cartSeats != null) {
                        for (Seat cartSeat : cartSeats) {
                            if (cartSeat.getRowNumber() == rowNum && cartSeat.getSeatNumber() == seatNum) {
                                isInCart = true;
                                break;
                            }
                        }
                    }

                    Button seatButton = new Button();
                    seatButton.setPrefSize(40,5);

                    if (isBookedInRepo) {
                        seatButton.setStyle("-fx-background-color: #e74c3c;");
                        seatButton.setDisable(true);

                        Tooltip tooltip = new Tooltip("Verkauft (Reihe " + rowNum + ", Platz " + seatNum + ")");
                        tooltip.setShowDelay(Duration.millis(100));
                        Tooltip.install(seatButton, tooltip);

                    } else if (isInCart) {
                        seatButton.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: black;");
                        seatButton.setDisable(true);

                        Tooltip tooltip = new Tooltip("Bereits im Warenkorb (Reihe " +  rowNum + ", Platz " + seatNum + ")");
                        tooltip.setShowDelay(Duration.millis(100));
                        Tooltip.install(seatButton, tooltip);

                    } else {
                        seatButton.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;");

                        Tooltip tooltip = new Tooltip("Reihe " + rowNum + ", Platz " + seatNum);
                        tooltip.setShowDelay(Duration.millis(100));
                        Tooltip.install(seatButton, tooltip);
                        
                        final Seat finalSeat = seat;

                        seatButton.setOnAction(event -> {
                            Seat existingSeat = findSelectedSeat(rowNum, seatNum);

                            if (existingSeat != null) {
                                seatButton.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;");
                                selectedSeats.remove(existingSeat);
                                selectedButtons.remove(seatButton);
                            } else {
                                seatButton.setStyle("-fx-background-color: #d4af37; -fx-text-fill: black;");
                                selectedSeats.add(finalSeat);
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

    private Seat findSelectedSeat(int row, int seat) {
        for (Seat s : selectedSeats) {
            if (s.getRowNumber() == row && s.getSeatNumber() == seat) {
                return s;
            }
        }
        return null;
    }

    private void updateStatusLabel() {
        if (selectedSeats.isEmpty()) {
            mainApp.updateSelectionLabel("Keine Plätze ausgewählt");
        } else {
            StringBuilder sb = new StringBuilder("Ausgewählt: ");
            for (Seat s : selectedSeats) {
                sb.append(String.format("| Reihe: %d, Platz: %d ", s.getRowNumber(), s.getSeatNumber()));
            }
            mainApp.updateSelectionLabel(sb.toString());
        }
    }

    public List<Seat> getSelectedSeats() {
        return selectedSeats;
    }
}