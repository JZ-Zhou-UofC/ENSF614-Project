package flightapp.business.domain;

import java.time.LocalDateTime;

public class Flight {

    private int id;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private double price;
    private int seatsAvailable;

    // Optional metadata
    private LocalDateTime lastModifiedAt;
    private Integer lastModifiedByUserId;

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

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
               ", origin='" + origin + '\'' +
               ", destination='" + destination + '\'' +
               ", departureTime=" + departureTime +
               ", arrivalTime=" + arrivalTime +
               ", price=" + price +
               ", seatsAvailable=" + seatsAvailable +
               '}';
    }
}
