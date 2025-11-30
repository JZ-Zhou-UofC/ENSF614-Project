package flightapp.business.domain;

public class FlightSeat {

    private int id;
    private int flightId;
    private Seat seat;
    private boolean reserved;


    public FlightSeat() {}

    /**
     * Constructor for creating a new FlightSeat for a specific seat on a flight.
     */
    public FlightSeat(int flightId, Seat seat, boolean reserved) {
        this.flightId = flightId;
        this.seat = seat;
        this.reserved = reserved;
    }

    /**
     * Full constructor including ID (for DB retrieval).
     */
    public FlightSeat(int id, int flightId, Seat seat, boolean reserved) {
        this.id = id;
        this.flightId = flightId;
        this.seat = seat;
        this.reserved = reserved;
    }

    // ===== Getters =====
    public int getId() {
        return id;
    }

    public int getFlightId() {
        return flightId;
    }

    public Seat getSeat() { 
        return this.seat; 
    };

    public boolean isReserved() {
        return reserved;
    }

    // ===== Setters =====
    public void setId(int id) {
        this.id = id;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    @Override
    public String toString() {
        return "FlightSeat{" +
                "id=" + id +
                ", flightId=" + flightId +
                ", airplaneSeatId=" + seat.getId() +
                ", reserved=" + reserved +
                '}';
    }
}

