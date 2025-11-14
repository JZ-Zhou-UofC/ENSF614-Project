package flightapp.business.controller;

import flightapp.business.AppSession;
import flightapp.business.domain.*;
import flightapp.business.service.ReservationService;

import java.sql.SQLException;

public class BookingController {

    private final AppSession session;
    private final ReservationService reservationService;

    public BookingController(AppSession session, ReservationService reservationService) {
        this.session = session;
        this.reservationService = reservationService;
    }

    public Reservation book(Flight flight, int seatCount) throws SQLException {
        User user = session.getCurrentUser();
        if (user instanceof Customer c) {
            return reservationService.bookFlightAsCustomer(c, flight, seatCount);
        }
        if (user instanceof Agent a) {
            Customer active = session.getActiveCustomer();
            if (active == null) {
                throw new IllegalStateException("Agent must select a customer first.");
            }
            return reservationService.bookFlightAsAgent(a, active, flight, seatCount);
        }
        throw new IllegalStateException("Only customers and agents can book flights.");
    }
}
