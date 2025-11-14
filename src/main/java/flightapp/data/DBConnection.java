package flightapp.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // TODO: adjust DB name / user / password
    private static final String URL  = "jdbc:mysql://localhost:3306/flightdb?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "john222";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
