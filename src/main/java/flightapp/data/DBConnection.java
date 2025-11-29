package flightapp.data;

import flightapp.util.SystemLogger;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final Dotenv dotenv = Dotenv.configure()
            .directory("./")     // look in project root
            .ignoreIfMissing()   // prevents crash if no .env (optional)
            .load();

    private static final String URL  = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASS = dotenv.get("DB_PASS");

    public static Connection getConnection() throws SQLException {
        try {
            SystemLogger.logDatabaseStatus(
                SystemLogger.SystemStatus.INFO, 
                "CONNECTION_ATTEMPT", 
                "Connecting to database: " + URL
            );
            
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            
            SystemLogger.logDatabaseStatus(
                SystemLogger.SystemStatus.INFO, 
                "CONNECTION_SUCCESS", 
                "Database connection established successfully"
            );
            
            return conn;
        } catch (SQLException e) {
            SystemLogger.logDatabaseStatus(
                SystemLogger.SystemStatus.ERROR, 
                "CONNECTION_FAILED", 
                "Failed to connect to database: " + e.getMessage(), 
                e
            );
            throw e;
        }
    }
}
