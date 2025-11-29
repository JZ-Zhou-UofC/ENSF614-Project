package flightapp.business.controller;

import flightapp.business.domain.User;
import flightapp.data.UserDAO;

import java.sql.SQLException;

public class UserController {

    private final UserDAO userDAO;

    // ⭐ Controller creates its own DAO
    public UserController() {
        this.userDAO = new UserDAO();
    }

    // ⭐ Optional constructor for testing or overriding dependency
    public UserController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Login by email and return the User.
     * Let MainWindow set currentUser itself.
     */
    public User loginByEmail(String email) throws SQLException {
        return userDAO.findByEmail(email); // no AppSession anymore
    }

    public User register(String firstName, String lastName, String email, boolean subscribed) throws SQLException {
        if (userDAO.findByEmail(email) != null) {
            throw new SQLException("Email already exists.");
        }
        return userDAO.registerCustomer(firstName, lastName, email, subscribed);
    }

}
