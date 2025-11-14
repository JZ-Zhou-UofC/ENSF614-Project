package flightapp.business;

import flightapp.business.domain.Customer;
import flightapp.business.domain.User;
import flightapp.business.domain.Agent;
import flightapp.business.domain.Admin;

public class AppSession {

    private User currentUser;
    private Customer activeCustomer; // For agents

    public User getCurrentUser() { return currentUser; }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        if (!(currentUser instanceof Agent)) {
            activeCustomer = null;
        }
    }

    public boolean isCustomer() { return currentUser instanceof Customer; }
    public boolean isAgent()    { return currentUser instanceof Agent; }
    public boolean isAdmin()    { return currentUser instanceof Admin; }

    public Customer getActiveCustomer() {
        if (currentUser instanceof Customer c) {
            return c;
        }
        return activeCustomer;
    }

    public void setActiveCustomer(Customer activeCustomer) {
        this.activeCustomer = activeCustomer;
    }

    public void clearActiveCustomer() {
        this.activeCustomer = null;
    }
}
