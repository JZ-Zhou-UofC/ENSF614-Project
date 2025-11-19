package flightapp.data;

import flightapp.business.domain.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT id, name, email, role, phone FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return null;

                int id = rs.getInt("id");
                String name = rs.getString("name");
                String role = rs.getString("role");
                String phone = rs.getString("phone");

                return switch (role) {
                    case "CUSTOMER" -> new Customer(id, name, email, phone);
                    case "AGENT" -> new Agent(id, name, email);
                    case "ADMIN" -> new Admin(id, name, email);
                    default -> null;
                };
            }
        }
    }

    public List<Customer> findAllCustomers() throws SQLException {
        String sql = "SELECT id, name, email, phone FROM users WHERE role = 'CUSTOMER'";
        List<Customer> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Customer c = new Customer();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setEmail(rs.getString("email"));
                c.setPhone(rs.getString("phone"));
                list.add(c);
            }
        }

        return list;
    }
}
