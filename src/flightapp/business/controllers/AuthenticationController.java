package flightapp.business.controllers;

import flightapp.business.domain.Customer;
import flightapp.business.domain.UserRole;
import flightapp.data.CustomerDAO;

public class AuthenticationController {

    private final CustomerDAO customerDAO = new CustomerDAO();

    /**
     * Very simple "login or register" by email.
     * If the customer exists -> login.
     * If not -> create and login.
     */
    public Customer loginOrRegister(String name, String email, String phone) {
        Customer c = customerDAO.findByEmail(email);
        if (c == null) {
            c = new Customer();
            c.setName(name);
            c.setEmail(email);
            c.setPhone(phone);
            c = customerDAO.create(c);
        }
        return c;
    }

    /**
     * Fake role logic: if email ends with 'admin.com' -> ADMIN,
     * if email ends with 'agent.com' -> AGENT, else CUSTOMER.
     */
    public UserRole inferRoleFromEmail(String email) {
        if (email.endsWith("admin.com")) {
            return UserRole.ADMIN;
        } else if (email.endsWith("agent.com")) {
            return UserRole.AGENT;
        } else {
            return UserRole.CUSTOMER;
        }
    }
}
