package flightapp.business;

import flightapp.business.domain.Customer;
import flightapp.business.domain.User;
import flightapp.business.service.ReservationService;

public class AppSession {

    private User currentUser;
    private Customer activeCustomer;  // used when agent selects a customer
    private ReservationService reservationService;

    // --------------------------
    // User session management
    // --------------------------
    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    // --------------------------
    // Agent active-customer management
    // --------------------------
    public Customer getActiveCustomer() {
        return activeCustomer;
    }

    public void setActiveCustomer(Customer activeCustomer) {
        this.activeCustomer = activeCustomer;
    }

    // --------------------------
    // ReservationService Access
    // --------------------------
    public ReservationService getReservationService() {
        return reservationService;
    }

    public void setReservationService(ReservationService reservationService) {
        this.reservationService = reservationService;
    }
}
