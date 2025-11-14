package flightapp.business.service;

import flightapp.business.domain.*;
import flightapp.data.DBConnection;
import flightapp.data.FlightDAO;
import flightapp.data.ReservationDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;

public class ReservationService {

    private final ReservationDAO reservationDAO;
    private final FlightDAO flightDAO;

    public ReservationService(ReservationDAO reservationDAO) {
        this.reservationDAO = reservationDAO;
        this.flightDAO = new FlightDAO(); // needed for seat updates
    }

    // ---------------------------------------------------------
    // BOOKING FOR CUSTOMER
    // ---------------------------------------------------------
    public Reservation bookFlightAsCustomer(Customer customer, Flight flight, int seatCount) throws SQLException {
        Objects.requireNonNull(customer, "customer cannot be null");
        Objects.requireNonNull(flight, "flight cannot be null");

        validateSeatAvailability(flight, seatCount);

        Reservation r = new Reservation();
        r.setCustomer(customer);
        r.setFlight(flight);
        r.setSeatCount(seatCount);
        r.setBookedAt(LocalDateTime.now());
        r.setBookedByUserId(customer.getId());

        Reservation saved = reservationDAO.insert(r);

        // deduct seats
        flight.setSeatsAvailable(flight.getSeatsAvailable() - seatCount);
        flightDAO.update(flight);

        return saved;
    }

    // ---------------------------------------------------------
    // BOOKING FOR AGENT (Agent books for a customer)
    // ---------------------------------------------------------
    public Reservation bookFlightAsAgent(Agent agent, Customer customer, Flight flight, int seatCount)
            throws SQLException {
        Objects.requireNonNull(agent, "agent cannot be null");
        Objects.requireNonNull(customer, "customer cannot be null");
        Objects.requireNonNull(flight, "flight cannot be null");

        validateSeatAvailability(flight, seatCount);

        Reservation r = new Reservation();
        r.setCustomer(customer);
        r.setFlight(flight);
        r.setSeatCount(seatCount);
        r.setBookedAt(LocalDateTime.now());
        r.setBookedByUserId(agent.getId()); // Agent tracked here

        Reservation saved = reservationDAO.insert(r);

        // deduct seats
        flight.setSeatsAvailable(flight.getSeatsAvailable() - seatCount);
        flightDAO.update(flight);

        return saved;
    }

    // ---------------------------------------------------------
    // HELPER: Check seat availability
    // ---------------------------------------------------------
    private void validateSeatAvailability(Flight flight, int seatCount) throws SQLException {
        if (seatCount <= 0) {
            throw new IllegalArgumentException("Seat count must be at least 1");
        }

        // reload the flight to get the latest seat count from DB
        Flight fresh = flightDAO.findById(flight.getId());
        if (fresh == null) {
            throw new SQLException("Flight not found in DB.");
        }

        if (fresh.getSeatsAvailable() < seatCount) {
            throw new SQLException("Not enough seats available.");
        }
    }

    public void cancelReservation(Reservation r, User performedBy) throws SQLException {
        Objects.requireNonNull(r, "Reservation cannot be null");
        Objects.requireNonNull(performedBy, "User cannot be null");

        // restore seats
        Flight flight = flightDAO.findById(r.getFlight().getId());
        flight.setSeatsAvailable(flight.getSeatsAvailable() + r.getSeatCount());
        flightDAO.update(flight);

        // delete reservation
        reservationDAO.delete(r.getId());
    }

    public Reservation modifyReservation(Reservation r) throws SQLException {
        Objects.requireNonNull(r);

        // set audit fields if needed
        r.setModifiedAt(LocalDateTime.now());

        return reservationDAO.update(r);
    }

    public void deleteReservation(Reservation reservation) throws SQLException {
        reservationDAO.delete(reservation.getId());
    }
}
