package flightapp.business.service;

import flightapp.business.domain.*;
import flightapp.data.ReservationDAO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;

public class ReservationService {

    private final ReservationDAO reservationDAO;

    public ReservationService(ReservationDAO reservationDAO) {
        this.reservationDAO = reservationDAO;
    }

    public Reservation bookFlightAsCustomer(Customer customer, Flight flight, int seatCount) throws SQLException {
        Objects.requireNonNull(customer);
        Objects.requireNonNull(flight);

        Reservation r = new Reservation();
        r.setCustomer(customer);
        r.setFlight(flight);
        r.setSeatCount(seatCount);
        r.setBookedAt(LocalDateTime.now());
        r.setBookedByUserId(customer.getId());

        return reservationDAO.insert(r);
    }

    public Reservation bookFlightAsAgent(Agent agent, Customer customer, Flight flight, int seatCount) throws SQLException {
        Objects.requireNonNull(agent);
        Objects.requireNonNull(customer);
        Objects.requireNonNull(flight);

        Reservation r = new Reservation();
        r.setCustomer(customer);
        r.setFlight(flight);
        r.setSeatCount(seatCount);
        r.setBookedAt(LocalDateTime.now());
        r.setBookedByUserId(agent.getId());

        return reservationDAO.insert(r);
    }
}
