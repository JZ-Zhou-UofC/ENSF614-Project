package flightapp;

import flightapp.business.domain.Customer;
import flightapp.business.domain.UserRole;

public class AppContext {

    private static Customer currentCustomer;
    private static UserRole currentRole = UserRole.GUEST;

    public static Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public static void setCurrentCustomer(Customer customer) {
        currentCustomer = customer;
    }

    public static UserRole getCurrentRole() {
        return currentRole;
    }

    public static void setCurrentRole(UserRole role) {
        currentRole = role;
    }

    public static boolean isLoggedIn() {
        return currentCustomer != null;
    }

    public static boolean isAdmin() {
        return currentRole == UserRole.ADMIN;
    }

    public static void logout() {
        currentCustomer = null;
        currentRole = UserRole.GUEST;
    }
}
