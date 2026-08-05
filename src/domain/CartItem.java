package domain;

public class CartItem {
    private final Event event;
    private final Section section;
    private final Seat seat;

    public CartItem(Event event, Section section, Seat seat) {
        this.event = event;
        this.section = section;
        this.seat = seat;
    }

    public Event getEvent() {
        return event;
    }

    public Section getSection() {
        return section;
    }

    public Seat getSeat() {
        return seat;
    }
}