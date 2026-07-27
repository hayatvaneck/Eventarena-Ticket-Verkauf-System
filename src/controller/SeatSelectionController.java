package controller;

import domain.Seat;
import domain.SeatedSection;
import domain.Section;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Die Klasse SeatSelectionController steuert die Sitzplatzauswahl im Saalplan und synchronisiert den Auswahlstatus.
 */

public class SeatSelectionController {

    private final GridPane seatGrid;
    private final Consumer<String> statusUpdater;

    private final List<Seat> selectedSeats = new ArrayList<>();
    private final List<Button> selectedButtons = new ArrayList<>();

    public SeatSelectionController(GridPane seatGrid, Consumer<String> statusUpdater) {
        this.seatGrid = seatGrid;
        this.statusUpdater = statusUpdater;
    }

    public void populateSeatPlan(Section section, List<Seat> cartSeats) {
        seatGrid.getChildren().clear();
        selectedSeats.clear();
        selectedButtons.clear();

        if (section instanceof SeatedSection) {
            SeatedSection seatedSection = (SeatedSection) section;
            
            int totalRows = seatedSection.getRowCount();
            int seatsPerRow = seatedSection.getSeatsPerRow();

            double horizontalGap = seatsPerRow > 18 ? 4 : 6;
            double verticalGap = totalRows > 12 ? 4 : 6;
            double seatButtonWidth = seatsPerRow > 18 ? 28 : 40;
            double seatButtonHeight = totalRows > 12 ? 18 : 22;

            seatGrid.setHgap(horizontalGap);
            seatGrid.setVgap(verticalGap);

            Label rowHeader = new Label("Reihe");
            rowHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            seatGrid.add(rowHeader, 0, 0);

            for (int r = 0; r < totalRows; r++) {
                int rowNum = r + 1;

                Label rowLabel = new Label(String.valueOf(rowNum));
                rowLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
                seatGrid.add(rowLabel, 0, r + 1);

                for (int s = 0; s < seatsPerRow; s++) {
                    int seatNum = s + 1;

                    Seat seat = seatedSection.getSeat(rowNum, seatNum);
                    if (seat == null) {
                        seat = new Seat(rowNum, seatNum);
                    }

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
                    seatButton.setPrefSize(seatButtonWidth, seatButtonHeight);

                    if (isBookedInRepo) {
                        seatButton.setStyle("-fx-background-color: #e74c3c");
                        seatButton.setDisable(true);

                        Tooltip tooltip = new Tooltip("Verkauft (Reihe " + rowNum + ", Platz " + seatNum + ")");
                        tooltip.setShowDelay(Duration.millis(100));
                        Tooltip.install(seatButton, tooltip);

                    } else if (isInCart) {
                        seatButton.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: black");
                        seatButton.setDisable(true);

                        Tooltip tooltip = new Tooltip("Bereits im Warenkorb (Reihe " +  rowNum + ", Platz " + seatNum + ")");
                        tooltip.setShowDelay(Duration.millis(100));
                        Tooltip.install(seatButton, tooltip);

                    } else {
                        seatButton.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white");

                        Tooltip tooltip = new Tooltip("Reihe " + (rowNum + 1) + ", Platz " + (seatNum + 1));
                        tooltip.setShowDelay(Duration.millis(100));
                        Tooltip.install(seatButton, tooltip);
                        
                        final Seat finalSeat = seat;

                        seatButton.setOnAction(event -> {
                            Seat existingSeat = findSelectedSeat(rowNum, seatNum);

                            if (existingSeat != null) {
                                seatButton.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white");
                                selectedSeats.remove(existingSeat);
                                selectedButtons.remove(seatButton);
                            } else {
                                seatButton.setStyle("-fx-background-color: #d4af37; -fx-text-fill: black");
                                selectedSeats.add(finalSeat);
                                selectedButtons.add(seatButton);
                            }

                            updateStatusLabel();
                        });
                    }

                    seatGrid.add(seatButton, s + 1, r + 1);
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
            statusUpdater.accept("Keine Plätze ausgewählt");
        } else {
            StringBuilder sb = new StringBuilder("Ausgewählt: ");
            for (Seat s : selectedSeats) {
                sb.append(String.format("| Reihe: %d, Platz: %d ", (s.getRowNumber() + 1), (s.getSeatNumber() + 1)));
            }
            statusUpdater.accept(sb.toString());
        }
    }

    public List<Seat> getSelectedSeats() {
        return selectedSeats;
    }
}


