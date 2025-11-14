package flightapp.business.controllers;

import java.util.List;

import flightapp.business.domain.Customer;
import flightapp.business.domain.Flight;
import flightapp.business.domain.Payment;
import flightapp.business.domain.Reservation;
import flightapp.data.PaymentDAO;
import flightapp.data.ReservationDAO;

public class BookingController {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();

    public Reservation bookFlight(Customer c, Flight f) {
        Reservation r = new Reservation();
        r.setCustomer(c);
        r.setFlight(f);
        r.setStatus("CONFIRMED");
        r = reservationDAO.create(r);

        // Simulated payment (just full price)
        Payment p = new Payment();
        p.setReservation(r);
        p.setAmount(f.getPrice());
        paymentDAO.create(p);

        return r;
    }

    public List<Reservation> getReservationsForCustomer(Customer c) {
        return reservationDAO.findByCustomerId(c.getId());
    }

    public void cancelReservation(Reservation r) {
        reservationDAO.updateStatus(r.getId(), "CANCELLED");
    }
}
