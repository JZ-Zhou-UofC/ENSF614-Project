package flightapp.data;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import flightapp.business.domain.Flight;

public class FlightDAO {

    public List<Flight> searchFlights(String origin, String destination, LocalDate date) {
        List<Flight> flights = new ArrayList<>();

        String sql = """
                SELECT * FROM flight
                WHERE origin = ? AND destination = ?
                AND DATE(departure_time) = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, origin);
            stmt.setString(2, destination);
            stmt.setDate(3, Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    flights.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return flights;
    }

    public List<Flight> findAll() {
        List<Flight> flights = new ArrayList<>();
        String sql = "SELECT * FROM flight";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                flights.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flights;
    }

    public Flight create(Flight f) {
        String sql = """
                INSERT INTO flight(origin, destination, departure_time, arrival_time,
                                   airline, price, seats_available)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, f.getOrigin());
            stmt.setString(2, f.getDestination());
            stmt.setTimestamp(3, Timestamp.valueOf(f.getDepartureTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(f.getArrivalTime()));
            stmt.setString(5, f.getAirline());
            stmt.setDouble(6, f.getPrice());
            stmt.setInt(7, f.getSeatsAvailable());

            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                f.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return f;
    }

    public void update(Flight f) {
        String sql = """
                UPDATE flight
                SET origin=?, destination=?, departure_time=?, arrival_time=?,
                    airline=?, price=?, seats_available=?
                WHERE id=?
                """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, f.getOrigin());
            stmt.setString(2, f.getDestination());
            stmt.setTimestamp(3, Timestamp.valueOf(f.getDepartureTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(f.getArrivalTime()));
            stmt.setString(5, f.getAirline());
            stmt.setDouble(6, f.getPrice());
            stmt.setInt(7, f.getSeatsAvailable());
            stmt.setInt(8, f.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int flightId) {
        String sql = "DELETE FROM flight WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, flightId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Flight mapRow(ResultSet rs) throws SQLException {
        Flight f = new Flight();
        f.setId(rs.getInt("id"));
        f.setOrigin(rs.getString("origin"));
        f.setDestination(rs.getString("destination"));
        f.setAirline(rs.getString("airline"));
        f.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
        f.setArrivalTime(rs.getTimestamp("arrival_time").toLocalDateTime());
        f.setPrice(rs.getDouble("price"));
        f.setSeatsAvailable(rs.getInt("seats_available"));
        return f;
    }
}
