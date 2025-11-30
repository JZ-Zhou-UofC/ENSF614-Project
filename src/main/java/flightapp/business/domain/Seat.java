package flightapp.business.domain;

public class Seat {

    private int id; 
    private int airplaneId;
    private int row;
    private char letter;
    private String seatType;

    public Seat() {}

    public Seat(int airplaneId, int row, char letter) {
        this.airplaneId = airplaneId;
        this.row = row;
        this.letter = letter;
        this.seatType = "Economy"; // planning to only use this constructor fornow
    }

    // cant use this until we implement Class Fare functionality
    public Seat(int id, int airplaneId, int row, char letter, String seatType) {
        this.id = id;
        this.airplaneId = airplaneId;
        this.row = row;
        this.letter = letter;
        this.seatType = seatType;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getAirplaneId() {
        return airplaneId;
    }

    public int getRow() {
        return row;
    }

    public char getLetter() {
        return letter;
    }

    public String getSeatType() {
        return seatType;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setAirplaneId(int airplaneId) {
        this.airplaneId = airplaneId;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public String getSeatLabel() {
        return row + String.valueOf(letter);
    }

    @Override
    public String toString() {
        return getSeatLabel() + " (" + seatType + ")";
    }
}
