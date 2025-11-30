package flightapp.business.controller;

import flightapp.business.domain.*;
import flightapp.data.FlightDAO;
import flightapp.data.FlightSeatDAO;
import flightapp.data.ReservationDAO;

import java.util.List;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;

public class BookingController {

    private final ReservationDAO reservationDAO;
    private final FlightDAO flightDAO;
    private final FlightSeatDAO flightSeatDAO;

    public BookingController() {
        this.reservationDAO = new ReservationDAO();
        this.flightDAO = new FlightDAO();
        this.flightSeatDAO = new FlightSeatDAO();
    }

    // Optional for testing
    public BookingController(ReservationDAO reservationDAO, FlightDAO flightDAO, FlightSeatDAO flightSeatDAO) {
        this.reservationDAO = reservationDAO;
        this.flightDAO = flightDAO;
        this.flightSeatDAO = flightSeatDAO;
    }


    public Reservation bookSeat(User performer, Customer customer, Flight flight, Seat seat) throws SQLException {
        Objects.requireNonNull(performer);
        Objects.requireNonNull(customer);
        Objects.requireNonNull(flight);   
        Objects.requireNonNull(seat);
        
        FlightSeat fs = flightSeatDAO.findByFlightAndSeat(flight.getId(), seat.getId());
        
        if (fs == null) {
            throw new SQLException("Seat " + seat.getSeatLabel() + " is not available.");
        }

        if (fs.isReserved()) {
            throw new SQLException("Seat " + seat.getSeatLabel() + " is reserved.");
        }

        fs.setReserved(true);
        flightSeatDAO.update(fs);

        Reservation r = new Reservation(customer, flight, fs, performer.getId());
        r.setBookedAt(LocalDateTime.now());

        Reservation saved = reservationDAO.insert(r);

        flight.setSeatsAvailable(flight.getSeatsAvailable() - 1);
        flightDAO.update(flight);

        return saved;
    }



    public Reservation bookForCustomer(Customer customer, Flight flight, Seat seat) throws SQLException {
        return bookSeat(customer, customer, flight, seat);
    }


    public Reservation bookForAgent(Agent agent, Customer targetCustomer, Flight flight, Seat seat)
            throws SQLException {

        return bookSeat(agent, targetCustomer, flight, seat);
    }



    public void cancelReservation(Reservation reservation) throws SQLException {
        Objects.requireNonNull(reservation);

        FlightSeat fs = reservation.getFlightSeat();
        fs.setReserved(false);
        flightSeatDAO.update(fs);

        // restore seats
        Flight flight = flightDAO.findById(reservation.getFlight().getId());
        flight.setSeatsAvailable(flight.getSeatsAvailable() + 1);
        flightDAO.update(flight);

        reservationDAO.delete(reservation.getId());
    }
    // method to modify booking by changing seats
    public Reservation changeSeat(User performer,Reservation reservation,Seat newSeat) throws SQLException {

        Objects.requireNonNull(performer, "Performer cannot be null");
        Objects.requireNonNull(reservation, "Reservation cannot be null");
        Objects.requireNonNull(newSeat, "New seat cannot be null");

        Flight flight = reservation.getFlight();

        // Load the old flight seat
        FlightSeat oldFs = reservation.getFlightSeat();

        // Load the new seat as a FlightSeat for this flight
        FlightSeat newFs = flightSeatDAO.findByFlightAndSeat(flight.getId(), newSeat.getId());

        if (newFs == null) {
            throw new SQLException("Seat " + newSeat.getSeatLabel() +
                    " does not exist on this flight.");
        }

        // Check if new seat is already reserved
        if (newFs.isReserved()) {
            throw new SQLException("Seat " + newSeat.getSeatLabel() + " is already booked.");
        }

        // Unreserve the old seat
        oldFs.setReserved(false);
        flightSeatDAO.update(oldFs);

        // Reserve the new seat
        newFs.setReserved(true);
        flightSeatDAO.update(newFs);

        // Update reservation object
        reservation.setFlightSeat(newFs);
        reservation.setModifiedAt(LocalDateTime.now());
        reservation.setModifiedByUserId(performer.getId());

        // save reservation update
        reservationDAO.update(reservation);

        return reservation;
    }

    public List<Reservation> getReservationsForCustomer(Customer customer) throws SQLException {
        Objects.requireNonNull(customer, "Customer cannot be null");
        return reservationDAO.findByCustomer(customer.getId());
    }


}
