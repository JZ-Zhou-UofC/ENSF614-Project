package flightapp.data;

import flightapp.util.SystemLogger;

import flightapp.business.domain.Flight;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FlightDAO {

	public List<Flight> findAll() throws SQLException {
	    String sql = "SELECT * FROM flights";
	    SystemLogger.logDatabaseStatus(
	        SystemLogger.SystemStatus.DEBUG,
	        "QUERY",
	        "SELECT * FROM flights"
	    );
	    
	    List<Flight> result = new ArrayList<>();

	    try (Connection conn = DBConnection.getConnection();
	            PreparedStatement ps = conn.prepareStatement(sql);
	            ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            result.add(mapRow(rs));
	        }
	        
	        SystemLogger.logDatabaseStatus(
	            SystemLogger.SystemStatus.DEBUG,
	            "QUERY_SUCCESS",
	            String.format("Retrieved %d flights", result.size())
	        );
	    } catch (SQLException e) {
	        SystemLogger.logDatabaseStatus(
	            SystemLogger.SystemStatus.ERROR,
	            "QUERY_FAILED",
	            "Error retrieving flights: " + e.getMessage(),
	            e
	        );
	        throw e;
	    }
	    return result;
	}

    public Flight findById(int id) throws SQLException {
        String sql = "SELECT * FROM flights WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return null;
                return mapRow(rs);
            }
        }
    }

    public Flight update(Flight f) throws SQLException {
        String sql = """
                UPDATE flights SET
                  origin = ?, destination = ?, departure_time = ?, arrival_time = ?,
                  price = ?, seats_available = ?
                WHERE id = ?
                """;
        SystemLogger.logDatabaseStatus(
            SystemLogger.SystemStatus.INFO,
            "UPDATE",
            String.format("Updating flight ID %d", f.getId())
        );
        
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, f.getOrigin());
            ps.setString(2, f.getDestination());
            ps.setTimestamp(3, f.getDepartureTime() != null ? Timestamp.valueOf(f.getDepartureTime()) : null);
            ps.setTimestamp(4, f.getArrivalTime() != null ? Timestamp.valueOf(f.getArrivalTime()) : null);
            ps.setDouble(5, f.getPrice());
            ps.setInt(6, f.getSeatsAvailable());
            ps.setInt(7, f.getId());
            ps.executeUpdate();
            
            SystemLogger.logDatabaseStatus(
                SystemLogger.SystemStatus.INFO,
                "UPDATE_SUCCESS",
                String.format("Flight ID %d updated successfully", f.getId())
            );
        } catch (SQLException e) {
            SystemLogger.logDatabaseStatus(
                SystemLogger.SystemStatus.ERROR,
                "UPDATE_FAILED",
                String.format("Failed to update flight ID %d: %s", f.getId(), e.getMessage()),
                e
            );
            throw e;
        }
        return f;
    }

    private Flight mapRow(ResultSet rs) throws SQLException {
        Flight f = new Flight();
        f.setId(rs.getInt("id"));
        f.setOrigin(rs.getString("origin"));
        f.setDestination(rs.getString("destination"));

        Timestamp dep = rs.getTimestamp("departure_time");
        Timestamp arr = rs.getTimestamp("arrival_time");

        f.setDepartureTime(dep != null ? dep.toLocalDateTime() : null);
        f.setArrivalTime(arr != null ? arr.toLocalDateTime() : null);
        f.setPrice(rs.getDouble("price"));
        f.setSeatsAvailable(rs.getInt("seats_available"));
        return f;
    }

    public List<Flight> searchFlights(String origin, String destination) throws SQLException {
        String sql = """
                SELECT * FROM flights
                WHERE origin LIKE ? AND destination LIKE ?
                """;

        List<Flight> result = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, (origin == null || origin.isBlank()) ? "%" : origin + "%");
            ps.setString(2, (destination == null || destination.isBlank()) ? "%" : destination + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        }

        return result;
    }

}
