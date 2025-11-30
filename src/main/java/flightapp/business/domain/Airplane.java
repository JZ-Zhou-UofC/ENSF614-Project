package flightapp.business.domain;

import java.util.ArrayList;
import java.util.List;

public class Airplane {

    private int id;
    private String model;
    private int numRows;          
    private char[] seatLetters; 

    private List<Seat> seats;   

    private boolean reservedStatus;  

    public Airplane() {
        this.model = "Boeing 737";
        this.numRows = 30;
        this.seatLetters = new char[] {'A', 'B', 'C', 'D', 'E', 'F'};
        this.reservedStatus = false;
    }

    public Airplane(int id, int rows, char[] seatLetters) {
        this.id = id;
        this.numRows = rows;
        this.seatLetters = seatLetters;
        this.seats = generateSeats();
        this.reservedStatus = false;
    }

    // create list of seat names from rows and letters
    private List<Seat> generateSeats() {
        List<Seat> list = new ArrayList<>();
        for (int r = 1; r <= numRows; r++) {
            for (char c : seatLetters) {
                list.add(new Seat(this.id, r, c));
            }
        }
        return list;
    }

    public boolean hasSeat(String seatLabel) {
        return seats.stream()
                .anyMatch(s -> s.getSeatLabel().equalsIgnoreCase(seatLabel));
    }

    public Seat getSeatByLabel(String seatLabel) {
        return seats.stream()
                .filter(s -> s.getSeatLabel().equalsIgnoreCase(seatLabel))
                .findFirst()
                .orElse(null);
    }

    // Getters
    public int getId() { return id; }
    public String getModel() { return model; }
    public int getNumRows() { return numRows; }
    public char[] getSeatLetters() { return seatLetters; }
    public List<Seat> getSeats() { return seats; }
    public boolean isReservedStatus() { return reservedStatus; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNumRows(int rows) { this.numRows = rows; }
    public void setSeatLetters(char[] seatLetters) { this.seatLetters = seatLetters; }
    public void setSeats(List<Seat> seats) { this.seats = seats; }
    public void setReservedStatus(boolean status) { this.reservedStatus = status; }

    @Override
    public String toString() {
        return "Airplane{" +
                "id=" + id +
                "model= " + model +
                ", rows=" + numRows +
                ", seatLetters=" + String.valueOf(seatLetters) +
                ", totalSeats=" + seats.size() +
                ", reservedStatus=" + reservedStatus +
                '}';
    }
}


