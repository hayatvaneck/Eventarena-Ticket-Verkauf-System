package controller;

import domain.Seat;
import domain.SeatedSection;
import domain.Section;
import javafx.scene.control.Button;
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

            for (int r = 0; r < totalRows; r++) {
                for (int s = 0; s < seatsPerRow; s++) {

                    int rowNum = r + 1;
                    int seatNum = s + 1;

                    Seat seat = seatedSection.getSeat(r, s);
                    if (seat == null) {
                        seat = new Seat (rowNum, seatNum);
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
            statusUpdater.accept("Keine PlÃ¤tze ausgewÃ¤hlt");
        } else {
            StringBuilder sb = new StringBuilder("AusgewÃ¤hlt: ");
            for (Seat s : selectedSeats) {
                sb.append(String.format("| Reihe: %d, Platz: %d ", s.getRowNumber(), s.getSeatNumber()));
            }
            statusUpdater.accept(sb.toString());
        }
    }

    public List<Seat> getSelectedSeats() {
        return selectedSeats;
    }
}


