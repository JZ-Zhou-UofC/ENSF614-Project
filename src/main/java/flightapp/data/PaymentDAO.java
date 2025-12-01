package flightapp.data;

import flightapp.business.domain.*;
import java.sql.*;
import java.util.ArrayList;

public class PaymentDAO {
    public ArrayList<PaymentMethod> retrievePaymentMethod(User customer) throws SQLException {
        String sql = """
                SELECT * FROM user_payment_information
                WHERE UserID = ?
                """;
        ArrayList<PaymentMethod> paymentMethods = new ArrayList<PaymentMethod>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customer.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PaymentMethod pm = new PaymentMethod(rs.getInt("id"));
                    String payMethd = rs.getString("PaymentMethod");
                    if (payMethd.equals("Credit Card")) {
                        pm.setPaymentStrategy(new CreditCardPayment("555555555555", payMethd));
                    }
                    if (payMethd.equals("PayPal")) {
                        pm.setPaymentStrategy(new PaypalPayment("acct_name", payMethd));
                    }
                    paymentMethods.add(pm);
                }
            }
        }
        return paymentMethods;
    }

    public void savePayment(Reservation r, PaymentMethod pm) throws SQLException {
        String sql = """
                INSERT INTO booking_payments(PaymentInformationID,ReservationID)
                VALUES(?,?)
                """;
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, pm.getId());
            ps.setInt(2, r.getId());

            ps.executeUpdate();
        }
    }

    public void savePaymentMethod(User customer, String paymentMethod) throws SQLException {
        String sql = """
                INSERT INTO user_payment_information(UserID,PaymentMethod)
                VALUES(?,?)
                """;
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, customer.getId());
            ps.setString(2, paymentMethod);

            ps.executeUpdate();
        }
    }

    public boolean updatePaymentMethod(User customer, String newPaymentMethod)
            throws SQLException {

        String sql = """
                UPDATE user_payment_information
                SET PaymentMethod = ?
                WHERE UserID = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPaymentMethod);
            ps.setInt(2, customer.getId());

            int rows = ps.executeUpdate();
            return rows >= 1; // ✅ allow multiple rows updated
        }
    }

}
