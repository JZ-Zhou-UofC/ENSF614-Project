package flightapp.business.domain;

import java.time.LocalDateTime;

public class Reservation {

    private int id;
    private Customer customer;
    private Flight flight;
    private LocalDateTime bookingDate;
    private String status;

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public Customer getCustomer() { return customer; }

    public void setCustomer(Customer customer) { this.customer = customer; }

    public Flight getFlight() { return flight; }

    public void setFlight(Flight flight) { this.flight = flight; }

    public LocalDateTime getBookingDate() { return bookingDate; }

    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("Reservation #%d: %s (%s)", id,
                flight != null ? flight.toString() : "no flight",
                status);
    }
}
