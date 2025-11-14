package flightapp.data;

import flightapp.business.domain.Flight;
import flightapp.business.domain.Reservation;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public Reservation create(Reservation r) {
        String sql = """
                INSERT INTO reservation (customer_id, flight_id, booking_date, status)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, r.getCustomer().getId());
            stmt.setInt(2, r.getFlight().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(4, r.getStatus());

            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                r.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return r;
    }

    public List<Reservation> findByCustomerId(int customerId) {
        List<Reservation> list = new ArrayList<>();

        String sql = """
                SELECT r.id, r.booking_date, r.status,
                       f.id AS flight_id, f.origin, f.destination, f.airline,
                       f.departure_time, f.arrival_time, f.price, f.seats_available
                FROM reservation r
                JOIN flight f ON r.flight_id = f.id
                WHERE r.customer_id = ?
                ORDER BY r.booking_date DESC
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reservation r = new Reservation();
                    r.setId(rs.getInt("id"));
                    r.setBookingDate(rs.getTimestamp("booking_date").toLocalDateTime());
                    r.setStatus(rs.getString("status"));

                    Flight f = new Flight();
                    f.setId(rs.getInt("flight_id"));
                    f.setOrigin(rs.getString("origin"));
                    f.setDestination(rs.getString("destination"));
                    f.setAirline(rs.getString("airline"));
                    f.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
                    f.setArrivalTime(rs.getTimestamp("arrival_time").toLocalDateTime());
                    f.setPrice(rs.getDouble("price"));
                    f.setSeatsAvailable(rs.getInt("seats_available"));

                    r.setFlight(f);
                    list.add(r);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void updateStatus(int reservationId, String status) {
        String sql = "UPDATE reservation SET status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, reservationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
