package flightapp.business.domain;

import java.time.LocalDateTime;

public class Reservation {

    private int id;
    private Customer customer;
    private Flight flight;
    private int seatCount;
    private LocalDateTime bookedAt;
    private LocalDateTime modifiedAt;
    private Integer bookedByUserId;
    private Integer modifiedByUserId;

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public Customer getCustomer() { return customer; }

    public void setCustomer(Customer customer) { this.customer = customer; }

    public Flight getFlight() { return flight; }

    public void setFlight(Flight flight) { this.flight = flight; }

    public int getSeatCount() { return seatCount; }

    public void setSeatCount(int seatCount) { this.seatCount = seatCount; }

    public LocalDateTime getBookedAt() { return bookedAt; }

    public void setBookedAt(LocalDateTime bookedAt) { this.bookedAt = bookedAt; }

    public LocalDateTime getModifiedAt() { return modifiedAt; }

    public void setModifiedAt(LocalDateTime modifiedAt) { this.modifiedAt = modifiedAt; }

    public Integer getBookedByUserId() { return bookedByUserId; }

    public void setBookedByUserId(Integer bookedByUserId) { this.bookedByUserId = bookedByUserId; }

    public Integer getModifiedByUserId() { return modifiedByUserId; }

    public void setModifiedByUserId(Integer modifiedByUserId) { this.modifiedByUserId = modifiedByUserId; }

    @Override
    public String toString() {
        return "Reservation{" +
               "id=" + id +
               ", customer=" + (customer != null ? customer.getFirstName() + " " + customer.getLastName() : "null") +
               ", flight=" + (flight != null ? flight.getId() : -1) +
               ", seatCount=" + seatCount +
               ", bookedAt=" + bookedAt +
               ", modifiedAt=" + modifiedAt +
               '}';
    }
}
