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

    // ⭐ Allow other dialogs to read session (needed for booking UI)
    public AppSession getSession() {
        return session;
    }

    // ⭐ Customer or agent booking
    public Reservation book(Flight flight, int seatCount) throws SQLException {
        User user = session.getCurrentUser();
        
        if (user instanceof Customer c) {
            return reservationService.bookFlightAsCustomer(c, flight, seatCount);
        }

        if (user instanceof Agent a) {
            Customer active = session.getActiveCustomer();
            if (active == null)
                throw new IllegalStateException("Agent must select a customer first.");
            return reservationService.bookFlightAsAgent(a, active, flight, seatCount);
        }

        throw new IllegalStateException("Only customers and agents can book flights.");
    }

    // ⭐ Customer-only simple booking (seatCount=1)
    public Reservation bookForCurrentUser(Flight flight) throws SQLException {
        User user = session.getCurrentUser();

        if (!(user instanceof Customer c)) {
            throw new IllegalStateException("Only customers can book flights for themselves.");
        }

        return reservationService.bookFlightAsCustomer(c, flight, 1);
    }
}
