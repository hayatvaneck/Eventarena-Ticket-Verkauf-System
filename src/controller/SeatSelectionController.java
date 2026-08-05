package controller;

import domain.CartItem;
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
 * Die Klasse SeatSelectionController steuert die Sitzplatzauswahl im Saalplan
 * und synchronisiert den Auswahlstatus.
 */
public class SeatSelectionController {

    /** JavaFX-Raster, in das Reihen und Sitzschaltflächen eingefügt werden. */
    private final GridPane seatGrid;
    /** Callback zur Aktualisierung des Auswahltexts im übergeordneten Screen. */
    private final Consumer<String> statusUpdater;

    /** Domain-Sitze, die im aktuellen Screen neu ausgewählt wurden. */
    private final List<Seat> selectedSeats = new ArrayList<>();
    /** Zugehörige Buttons zur Synchronisierung des visuellen Auswahlzustands. */
    private final List<Button> selectedButtons = new ArrayList<>();

    /**
     * Erstellt den Controller für ein vorhandenes Sitzraster.
     *
     * @param seatGrid zu steuerndes JavaFX-Raster
     * @param statusUpdater Empfänger für den lesbaren Auswahlstatus
     */
    public SeatSelectionController(GridPane seatGrid, Consumer<String> statusUpdater) {
        this.seatGrid = seatGrid;
        this.statusUpdater = statusUpdater;
    }

    /**
     * Baut den Sitzplan neu auf und berücksichtigt verkaufte sowie bereits im
     * Warenkorb befindliche Plätze.
     *
     * @param section darzustellender Bereich; nur Sitzbereiche werden verarbeitet
     * @param cartSeats aktuelle Warenkorbeinträge zur Vermeidung doppelter Auswahl
     */
    public void populateSeatPlan(Section section, List<CartItem> cartSeats) {
        seatGrid.getChildren().clear();
        selectedSeats.clear();
        selectedButtons.clear();

        if (!(section instanceof SeatedSection)) {
            return;
        }

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

        // Ein unsichtbarer Platzhalter rechts gleicht die Reihenbeschriftung links aus.
        Label dummyHeader = new Label("Reihe");
        dummyHeader.setVisible(false);
        seatGrid.add(dummyHeader, seatsPerRow + 1, 0);

        for (int r = 0; r < totalRows; r++) {
            int rowNum = r + 1;
            Label rowLabel = new Label(String.valueOf(rowNum));
            rowLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            seatGrid.add(rowLabel, 0, r + 1);

            // Die unsichtbare Nummer hält den Sitzplan trotz linker Reihennummer mittig.
            Label dummyRowLabel = new Label(String.valueOf(rowNum));
            dummyRowLabel.setVisible(false);
            seatGrid.add(dummyRowLabel, seatsPerRow + 1, r + 1);

            for (int s = 0; s < seatsPerRow; s++) {
                int seatNum = s + 1;

                Seat seat = seatedSection.getSeat(rowNum, seatNum);
                if (seat == null) {
                    continue;
                }

                boolean isBookedInRepo = seat.isBooked();

                boolean isInCart = false;
                if (cartSeats != null) {
                    for (CartItem cartItem : cartSeats) {
                        if (cartItem == null || cartItem.getSeat() == null || cartItem.getSection() == null) {
                            continue;
                        }

                        if (cartItem.getSection() == section
                                && cartItem.getSeat().getRowNumber() == rowNum
                                && cartItem.getSeat().getSeatNumber() == seatNum) {
                            isInCart = true;
                            break;
                        }
                    }
                }

                Button seatButton = new Button();
                seatButton.setPrefSize(seatButtonWidth, seatButtonHeight);

                if (isBookedInRepo) {
                    seatButton.setStyle("-fx-background-color: #e74c3c;");
                    seatButton.setDisable(true);

                    Tooltip tooltip = new Tooltip("Verkauft (Reihe " + rowNum + ", Platz " + seatNum + ")");
                    tooltip.setShowDelay(Duration.millis(100));
                    Tooltip.install(seatButton, tooltip);

                } else if (isInCart) {
                    seatButton.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: black;");
                    seatButton.setDisable(true);

                    Tooltip tooltip = new Tooltip("Bereits im Warenkorb (Reihe " + rowNum + ", Platz " + seatNum + ")");
                    tooltip.setShowDelay(Duration.millis(100));
                    Tooltip.install(seatButton, tooltip);

                } else {
                    seatButton.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-cursor: hand;");

                    Tooltip tooltip = new Tooltip("Reihe " + rowNum + ", Platz " + seatNum);
                    tooltip.setShowDelay(Duration.millis(100));
                    Tooltip.install(seatButton, tooltip);

                    final Seat finalSeat = seat;

                    seatButton.setOnAction(event -> {
                        Seat existingSeat = findSelectedSeat(rowNum, seatNum);

                        if (existingSeat != null) {
                            // Platz wieder abwählen
                            seatButton
                                    .setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-cursor: hand;");
                            selectedSeats.remove(existingSeat);
                            selectedButtons.remove(seatButton);
                        } else {
                            // Das Ticketlimit wird bereits bei der Auswahl durchgesetzt.
                            int currentCartSize = (cartSeats != null) ? cartSeats.size() : 0;

                            if (currentCartSize + selectedSeats.size() >= 10) {
                                // Der Sitz bleibt bei erreichtem Limit unverändert.
                                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                                        javafx.scene.control.Alert.AlertType.WARNING);
                                alert.setTitle("Limit erreicht");
                                alert.setHeaderText(null);
                                alert.setContentText("Sie können insgesamt maximal 10 Tickets in den Warenkorb legen.");
                                alert.showAndWait();
                                return;
                            }

                            // Freie Kapazität: Sitz markieren und in die Auswahl aufnehmen.
                            seatButton
                                    .setStyle("-fx-background-color: #d4af37; -fx-text-fill: black; -fx-cursor: hand;");
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

    /**
     * Sucht einen aktuell ausgewählten Sitz anhand seiner Position.
     *
     * @param row Reihennummer
     * @param seat Platznummer
     * @return ausgewählter Sitz oder {@code null}
     */
    private Seat findSelectedSeat(int row, int seat) {
        for (Seat s : selectedSeats) {
            if (s.getRowNumber() == row && s.getSeatNumber() == seat) {
                return s;
            }
        }
        return null;
    }

    /** Übermittelt eine lesbare Zusammenfassung der aktuellen Auswahl an die UI. */
    private void updateStatusLabel() {
        if (selectedSeats.isEmpty()) {
            statusUpdater.accept("Keine Plätze ausgewählt");
        } else {
            StringBuilder sb = new StringBuilder("Ausgewählt: ");
            for (Seat s : selectedSeats) {
                sb.append(String.format("| Reihe: %d, Platz: %d ", s.getRowNumber(), s.getSeatNumber()));
            }
            statusUpdater.accept(sb.toString());
        }
    }

    /** @return defensive Kopie aller aktuell ausgewählten Sitze */
    public List<Seat> getSelectedSeats() {
        return new ArrayList<>(selectedSeats);
    }
}
