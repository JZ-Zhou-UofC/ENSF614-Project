package flightapp.business.controller;

import flightapp.util.SystemLogger;

import flightapp.business.domain.Agent;
import flightapp.business.domain.Customer;
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
        SystemLogger.logUserAction(
            SystemLogger.SystemStatus.INFO,
            "LOGIN",
            "ATTEMPT",
            String.format("Login attempt for email: %s", email)
        );
        
        try {
            User user = userDAO.findByEmail(email);
            
            if (user != null) {
                SystemLogger.logUserAction(
                    SystemLogger.SystemStatus.INFO,
                    user.getClass().getSimpleName().toUpperCase(),
                    "LOGIN_SUCCESS",
                    String.format("User ID %d logged in successfully", user.getId())
                );
            } else {
                SystemLogger.logUserAction(
                    SystemLogger.SystemStatus.WARN,
                    "UNKNOWN",
                    "LOGIN_FAILED",
                    String.format("Login failed: email %s not found", email)
                );
            }
            
            return user;
        } catch (SQLException e) {
            SystemLogger.logUserAction(
                SystemLogger.SystemStatus.ERROR,
                "SYSTEM",
                "LOGIN_ERROR",
                String.format("Database error during login: %s", e.getMessage()),
                e
            );
            throw e;
        }
    }

    public User register(String firstName, String lastName, String email, boolean subscribed) throws SQLException {
        SystemLogger.logUserAction(
            SystemLogger.SystemStatus.INFO,
            "REGISTRATION",
            "ATTEMPT",
            String.format("Registration attempt for email: %s", email)
        );
        
        try {
            if (userDAO.findByEmail(email) != null) {
                SystemLogger.logUserAction(
                    SystemLogger.SystemStatus.WARN,
                    "REGISTRATION",
                    "FAILED",
                    String.format("Registration failed: email %s already exists", email)
                );
                throw new SQLException("Email already exists.");
            }
            
            User newUser = userDAO.registerCustomer(firstName, lastName, email, subscribed);
            
            SystemLogger.logUserAction(
                SystemLogger.SystemStatus.INFO,
                "REGISTRATION",
                "SUCCESS",
                String.format("User ID %d registered successfully with email: %s", newUser.getId(), email)
            );
            
            return newUser;
        } catch (SQLException e) {
            SystemLogger.logUserAction(
                SystemLogger.SystemStatus.ERROR,
                "REGISTRATION",
                "ERROR",
                String.format("Database error during registration: %s", e.getMessage()),
                e
            );
            throw e;
        }
    }



}
