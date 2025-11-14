package flightapp.data;

import flightapp.business.domain.Customer;
import flightapp.business.domain.Flight;
import flightapp.business.domain.Reservation;

import java.sql.*;
import java.time.LocalDateTime;

public class ReservationDAO {

    public Reservation insert(Reservation r) throws SQLException {
        String sql = """
            INSERT INTO reservations
              (customer_id, flight_id, seat_count, booked_at, booked_by_user_id)
              VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, r.getCustomer().getId());
            ps.setInt(2, r.getFlight().getId());
            ps.setInt(3, r.getSeatCount());
            ps.setTimestamp(4, r.getBookedAt() != null ? Timestamp.valueOf(r.getBookedAt()) : Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(5, r.getBookedByUserId() != null ? r.getBookedByUserId() : r.getCustomer().getId());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    r.setId(keys.getInt(1));
                }
            }
        }
        return r;
    }

    public Reservation update(Reservation r) throws SQLException {
        String sql = """
            UPDATE reservations SET
              flight_id = ?, seat_count = ?, modified_at = ?, modified_by_user_id = ?
            WHERE id = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, r.getFlight().getId());
            ps.setInt(2, r.getSeatCount());
            ps.setTimestamp(3, r.getModifiedAt() != null ? Timestamp.valueOf(r.getModifiedAt()) : null);
            ps.setObject(4, r.getModifiedByUserId());
            ps.setInt(5, r.getId());

            ps.executeUpdate();
        }
        return r;
    }
}
