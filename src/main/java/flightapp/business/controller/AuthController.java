package flightapp.business.controller;

import flightapp.business.AppSession;
import flightapp.business.domain.User;
import flightapp.data.UserDAO;

import java.sql.SQLException;

public class AuthController {

    private final AppSession session;
    private final UserDAO userDAO;

    public AuthController(AppSession session, UserDAO userDAO) {
        this.session = session;
        this.userDAO = userDAO;
    }

    public User loginByEmail(String email) throws SQLException {
        User user = userDAO.findByEmail(email);
        if (user != null) {
            session.setCurrentUser(user);
        }
        return user;
    }
}
