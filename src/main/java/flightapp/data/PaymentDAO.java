package flightapp.data;

import java.sql.*;
import java.time.LocalDateTime;

import flightapp.business.domain.Payment;

public class PaymentDAO {

    public Payment create(Payment p) {
        String sql = """
                INSERT INTO payment (reservation_id, amount, payment_date)
                VALUES (?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, p.getReservation().getId());
            stmt.setDouble(2, p.getAmount());
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                p.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }
}
