package flightapp.data;

import flightapp.business.domain.Flight;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlightDAO {

    // INSERT new flight
    public Flight insert(Flight flight) throws SQLException {
        String sql = """
            INSERT INTO flights 
            (origin, destination, departure_time, arrival_time, price, seats_available,
             airplane_id, last_modified_at, last_modified_by_user_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, flight.getOrigin());
            ps.setString(2, flight.getDestination());
            ps.setTimestamp(3, Timestamp.valueOf(flight.getDepartureTime()));
            ps.setTimestamp(4, Timestamp.valueOf(flight.getArrivalTime()));
            ps.setDouble(5, flight.getPrice());
            ps.setInt(6, flight.getSeatsAvailable());
            ps.setInt(7, flight.getAirplaneId());

            ps.setTimestamp(8, flight.getLastModifiedAt() == null ? null :
                    Timestamp.valueOf(flight.getLastModifiedAt()));
            ps.setObject(9, flight.getLastModifiedByUserId());

            ps.executeUpdate();

            // Get generated flight ID
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    flight.setId(rs.getInt(1));
                }
            }
        }

        return flight;
    }

    // UPDATE existing flight
    public Flight update(Flight flight) throws SQLException {
        String sql = """
            UPDATE flights SET
                origin = ?, destination = ?, departure_time = ?, arrival_time = ?,
                price = ?, seats_available = ?, airplane_id = ?,
                last_modified_at = ?, last_modified_by_user_id = ?
            WHERE id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, flight.getOrigin());
            ps.setString(2, flight.getDestination());
            ps.setTimestamp(3, Timestamp.valueOf(flight.getDepartureTime()));
            ps.setTimestamp(4, Timestamp.valueOf(flight.getArrivalTime()));
            ps.setDouble(5, flight.getPrice());
            ps.setInt(6, flight.getSeatsAvailable());
            ps.setInt(7, flight.getAirplaneId());

            ps.setTimestamp(8, flight.getLastModifiedAt() == null ? null :
                    Timestamp.valueOf(flight.getLastModifiedAt()));
            ps.setObject(9, flight.getLastModifiedByUserId());

            ps.setInt(10, flight.getId());

            ps.executeUpdate();
        }

        return flight;
    }

    // DELETE flight
    public void delete(int flightId) throws SQLException {
        String sql = "DELETE FROM flights WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, flightId);
            ps.executeUpdate();
        }
    }

    // GET flight by ID
    public Flight findById(int id) throws SQLException {
        String sql = "SELECT * FROM flights WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }

        return null;
    }

    // GET all flights
    public List<Flight> findAll() throws SQLException {
        String sql = "SELECT * FROM flights";

        List<Flight> flights = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                flights.add(mapRow(rs));
            }
        }

        return flights;
    }

    // Map ResultSet → Flight object (code written by ChatGPT)
    private Flight mapRow(ResultSet rs) throws SQLException {
        Flight f = new Flight();

        f.setId(rs.getInt("id"));
        f.setOrigin(rs.getString("origin"));
        f.setDestination(rs.getString("destination"));

        f.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
        f.setArrivalTime(rs.getTimestamp("arrival_time").toLocalDateTime());

        f.setPrice(rs.getDouble("price"));
        f.setSeatsAvailable(rs.getInt("seats_available"));
        f.setAirplaneId(rs.getInt("airplane_id"));

        Timestamp modAt = rs.getTimestamp("last_modified_at");
        if (modAt != null) {
            f.setLastModifiedAt(modAt.toLocalDateTime());
        }

        f.setLastModifiedByUserId((Integer) rs.getObject("last_modified_by_user_id"));

        return f;
    }
}

