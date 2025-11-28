package flightapp.data;

import flightapp.business.domain.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT id, first_Name, last_Name, email, role, phone FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return null;

                int id = rs.getInt("id");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String role = rs.getString("role");
                String phone = rs.getString("phone");

                return switch (role) {
                    case "CUSTOMER" -> new Customer(id, firstName, lastName, email, phone);
                    case "AGENT" -> new Agent(id, firstName, lastName, email);
                    case "ADMIN" -> new Admin(id, firstName, lastName, email);
                    default -> null;
                };
            }
        }
    }

    public List<Customer> findAllCustomers() throws SQLException {
        String sql = "SELECT id, firstName, lastName, email, phone FROM users WHERE role = 'CUSTOMER'";
        List<Customer> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Customer c = new Customer();
                c.setId(rs.getInt("id"));
                c.setFirstName(rs.getString("firstName"));
                c.setLastName(rs.getString("lastName"));
                c.setEmail(rs.getString("email"));
                c.setPhone(rs.getString("phone"));
                list.add(c);
            }
        }

        return list;
    }

    public User registerCustomer(String firstName, String lastName, String email) throws SQLException {

        String sql = """
                    INSERT INTO users (first_name, last_name, email, role)
                    VALUES (?, ?, ?, 'CUSTOMER')
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);

                    return new Customer(
                            id,
                            firstName,
                            lastName,
                            email,
                            null // phone is optional
                    );
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }

}
