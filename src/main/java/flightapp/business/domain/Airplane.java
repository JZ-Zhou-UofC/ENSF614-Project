package flightapp.business.domain;

import java.util.ArrayList;
import java.util.List;

public class Airplane {

    private int id;

    private int rows;          // how many seat rows (e.g. 30)
    private char[] seatLetters; // seat columns (e.g. A–F)

    private List<Seat> seats;   // generated seat objects

    private boolean reservedStatus;     // is airplane currently assigned to a flight?

    public Airplane() {
        this.rows = 30;
        this.seatLetters = new char[] {'A', 'B', 'C', 'D', 'E', 'F'};
        this.reservedStatus = false;
    }

    public Airplane(int id, int rows, char[] seatLetters) {
        this.id = id;
        this.rows = rows;
        this.seatLetters = seatLetters;
        this.seats = generateSeats();
        this.reservedStatus = false;
    }

    /** Generate AirplaneSeat objects from rows and letters */
    private List<Seat> generateSeats() {
        List<Seat> list = new ArrayList<>();
        for (int r = 1; r <= rows; r++) {
            for (char c : seatLetters) {
                list.add(new Seat(this.id, r, c));
            }
        }
        return list;
    }

    /** Check if the airplane contains this seat (e.g. "12A") */
    public boolean hasSeat(String seatLabel) {
        return seats.stream()
                .anyMatch(s -> s.getSeatLabel().equalsIgnoreCase(seatLabel));
    }

    /** Find a seat object by its label (e.g. "4C") */
    public Seat getSeatByLabel(String seatLabel) {
        return seats.stream()
                .filter(s -> s.getSeatLabel().equalsIgnoreCase(seatLabel))
                .findFirst()
                .orElse(null);
    }

    // ===== Getters & Setters =====

    public int getId() { return id; }
    public int getRows() { return rows; }
    public char[] getSeatLetters() { return seatLetters; }
    public List<Seat> getSeats() { return seats; }
    public boolean isReservedStatus() { return reservedStatus; }

    public void setId(int id) { this.id = id; }
    public void setRows(int rows) { this.rows = rows; }
    public void setSeatLetters(char[] seatLetters) { this.seatLetters = seatLetters; }
    public void setSeats(List<Seat> seats) { this.seats = seats; }
    public void setReservedStatus(boolean status) { this.reservedStatus = status; }

    @Override
    public String toString() {
        return "Airplane{" +
                "id=" + id +
                ", rows=" + rows +
                ", seatLetters=" + String.valueOf(seatLetters) +
                ", totalSeats=" + seats.size() +
                ", reservedStatus=" + reservedStatus +
                '}';
    }
}


