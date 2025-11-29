package flightapp.business.controller;

import flightapp.business.domain.*;

import flightapp.util.SystemLogger;

import flightapp.data.FlightDAO;
import flightapp.data.ReservationDAO;

import java.util.List;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;

public class BookingController {

    private final ReservationDAO reservationDAO;
    private final FlightDAO flightDAO;

    public BookingController() {
        this.reservationDAO = new ReservationDAO();
        this.flightDAO = new FlightDAO();
    }

    // Optional for testing
    public BookingController(ReservationDAO reservationDAO, FlightDAO flightDAO) {
        this.reservationDAO = reservationDAO;
        this.flightDAO = flightDAO;
    }

    // ------------------------------------------------------------------------
    // BOOKING MAIN LOGIC
    // ------------------------------------------------------------------------
    public Reservation bookFlight(User performer, Customer customer, Flight flight, int seatCount) throws SQLException {
        Objects.requireNonNull(performer);
        Objects.requireNonNull(customer);
        Objects.requireNonNull(flight);

        SystemLogger.logBusinessOperation(
            SystemLogger.SystemStatus.INFO,
            "BOOK_FLIGHT",
            String.format("User %d booking %d seats on flight %d for customer %d", 
                performer.getId(), seatCount, flight.getId(), customer.getId())
        );

        try {
            validateSeatAvailability(flight, seatCount);

            Reservation reservation = new Reservation();
            reservation.setCustomer(customer);
            reservation.setFlight(flight);
            reservation.setSeatCount(seatCount);
            reservation.setBookedAt(LocalDateTime.now());
            reservation.setBookedByUserId(performer.getId());

            Reservation saved = reservationDAO.insert(reservation);

            // Update flight seat count
            flight.setSeatsAvailable(flight.getSeatsAvailable() - seatCount);
            flightDAO.update(flight);

            SystemLogger.logBusinessOperation(
                SystemLogger.SystemStatus.INFO,
                "BOOK_FLIGHT_SUCCESS",
                String.format("Reservation ID %d created successfully", saved.getId())
            );

            return saved;
        } catch (SQLException | IllegalArgumentException e) {
            SystemLogger.logBusinessOperation(
                SystemLogger.SystemStatus.ERROR,
                "BOOK_FLIGHT_FAILED",
                String.format("Failed to book flight: %s", e.getMessage()),
                e
            );
            throw e;
        }
    }
    
    // ------------------------------------------------------------------------
    // CUSTOMER books for themselves
    // ------------------------------------------------------------------------
    public Reservation bookForCustomer(Customer customer, Flight flight, int seatCount) throws SQLException {
        return bookFlight(customer, customer, flight, seatCount);
    }

    // ------------------------------------------------------------------------
    // AGENT books for a specific customer
    // ------------------------------------------------------------------------
    public Reservation bookForAgent(Agent agent, Customer targetCustomer, Flight flight, int seatCount)
            throws SQLException {

        return bookFlight(agent, targetCustomer, flight, seatCount);
    }

    // ------------------------------------------------------------------------
    // HELPER: seat validation
    // ------------------------------------------------------------------------
    private void validateSeatAvailability(Flight flight, int seatCount) throws SQLException {
        if (seatCount <= 0) {
            throw new IllegalArgumentException("Seat count must be >= 1");
        }

        Flight fresh = flightDAO.findById(flight.getId());
        if (fresh == null) {
            throw new SQLException("Flight not found in DB.");
        }

        if (fresh.getSeatsAvailable() < seatCount) {
            throw new SQLException("Not enough seats available.");
        }
    }

    // ------------------------------------------------------------------------
    // CANCEL RESERVATION
    // ------------------------------------------------------------------------
    public void cancelReservation(Reservation reservation) throws SQLException {
        Objects.requireNonNull(reservation);

        SystemLogger.logBusinessOperation(
            SystemLogger.SystemStatus.WARN,
            "CANCEL_RESERVATION",
            String.format("Cancelling reservation ID %d", reservation.getId())
        );

        try {
            Flight flight = flightDAO.findById(reservation.getFlight().getId());

            // restore seats
            flight.setSeatsAvailable(flight.getSeatsAvailable() + reservation.getSeatCount());
            flightDAO.update(flight);

            reservationDAO.delete(reservation.getId());
            
            SystemLogger.logBusinessOperation(
                SystemLogger.SystemStatus.INFO,
                "CANCEL_RESERVATION_SUCCESS",
                String.format("Reservation ID %d cancelled successfully", reservation.getId())
            );
        } catch (SQLException e) {
            SystemLogger.logBusinessOperation(
                SystemLogger.SystemStatus.ERROR,
                "CANCEL_RESERVATION_FAILED",
                String.format("Failed to cancel reservation ID %d: %s", reservation.getId(), e.getMessage()),
                e
            );
            throw e;
        }
    }

    // ------------------------------------------------------------------------
    // MODIFY
    // ------------------------------------------------------------------------
    public Reservation modifyReservation(Reservation reservation) throws SQLException {
        reservation.setModifiedAt(LocalDateTime.now());
        return reservationDAO.update(reservation);
    }

    // ------------------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------------------
    public void deleteReservation(Reservation reservation) throws SQLException {
        reservationDAO.delete(reservation.getId());
    }

    public List<Reservation> getReservationsForCustomer(Customer customer) throws SQLException {
        Objects.requireNonNull(customer, "Customer cannot be null");
        return reservationDAO.findByCustomer(customer.getId());
    }

}
