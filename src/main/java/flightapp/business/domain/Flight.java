package flightapp.business.domain;

import java.time.LocalDateTime;

public class Flight {

    private int id;
    private int airplaneId; 
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private double price;
    private int seatsAvailable;

    private LocalDateTime lastModifiedAt;
    private Integer lastModifiedByUserId;

    public Flight() {}

    public Flight(int airplaneId, String origin, String destination,
                  LocalDateTime departureTime, LocalDateTime arrivalTime,
                  double price) {
        this.airplaneId = airplaneId;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
    }

    // Getters & Setters 
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getAirplaneId() { return airplaneId; }

    public void setAirplaneId(int airplaneId) { this.airplaneId = airplaneId; }

    public String getOrigin() { return origin; }

    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }

    public void setDestination(String destination) { this.destination = destination; }

    public LocalDateTime getDepartureTime() { return departureTime; }

    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public LocalDateTime getArrivalTime() { return arrivalTime; }

    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public double getPrice() { return price; }

    public void setPrice(double price) { this.price = price; }

    public int getSeatsAvailable() { return seatsAvailable; }

    public void setSeatsAvailable(int seatsAvailable) { this.seatsAvailable = seatsAvailable; }

    public LocalDateTime getLastModifiedAt() { return lastModifiedAt; }

    public void setLastModifiedAt(LocalDateTime lastModifiedAt) { this.lastModifiedAt = lastModifiedAt; }

    public Integer getLastModifiedByUserId() { return lastModifiedByUserId; }

    public void setLastModifiedByUserId(Integer lastModifiedByUserId) { this.lastModifiedByUserId = lastModifiedByUserId; }

    @Override
    public String toString() {
        return "Flight{" +
               "id=" + id +
               ", airplaneId=" + airplaneId +
               ", origin='" + origin + '\'' +
               ", destination='" + destination + '\'' +
               ", departureTime=" + departureTime +
               ", arrivalTime=" + arrivalTime +
               ", price=" + price +
               ", seatsAvailable=" + seatsAvailable +
               '}';
    }
}

