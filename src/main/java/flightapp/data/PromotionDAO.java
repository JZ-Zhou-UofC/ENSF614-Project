package flightapp.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PromotionDAO {

    private static final String INSERT_PROMOTION =
        "INSERT INTO promotions (creator_id, customer_id, content, created_date) " +
        "VALUES (?, ?, ?, NOW())";

    public void createPromotion(int agentId, int customerId, String content) throws SQLException {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_PROMOTION)) {

            stmt.setInt(1, agentId);
            stmt.setInt(2, customerId);
            stmt.setString(3, content);
            stmt.executeUpdate();
        }
    }
}
