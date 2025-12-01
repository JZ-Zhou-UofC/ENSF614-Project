package flightapp.data;

import flightapp.business.domain.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";

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

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<Customer> findAllCustomers() throws SQLException {
        String sql = "SELECT * FROM users WHERE role = 'CUSTOMER'";
        List<Customer> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add((Customer) mapRow(rs));
            }
        }

        return list;
    }

    public Customer registerCustomer(String firstName, String lastName, String email, boolean subscribed)
            throws SQLException {

        String sql = """
                INSERT INTO users (first_name, last_name, email, role, subscribed)
                VALUES (?, ?, ?, 'CUSTOMER', ?)
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setBoolean(4, subscribed);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new Customer(id, firstName, lastName, email, null, subscribed);
                }
            }
        }

        throw new SQLException("Failed to create customer: no ID returned.");
    }

    public Admin registerAdmin(String firstName, String lastName, String email) throws SQLException {
        String sql = """
                INSERT INTO users (first_name, last_name, email, role)
                VALUES (?, ?, ?, 'ADMIN')
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Admin(rs.getInt(1), firstName, lastName, email);
                }
            }
        }

        throw new SQLException("Failed to create admin: no ID returned.");
    }

    public Agent registerAgent(String firstName, String lastName, String email) throws SQLException {
        String sql = """
                INSERT INTO users (first_name, last_name, email, role)
                VALUES (?, ?, ?, 'AGENT')
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Agent(rs.getInt(1), firstName, lastName, email);
                }
            }
        }

        throw new SQLException("Failed to create agent: no ID returned.");
    }

    public boolean updateCustomer(Customer customer) throws SQLException {
        String sql = """
                UPDATE users
                SET first_name = ?, last_name = ?, email = ?, phone = ?, subscribed = ?
                WHERE id = ? AND role = 'CUSTOMER'
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customer.getFirstName());
            ps.setString(2, customer.getLastName());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getPhone());
            ps.setBoolean(5, customer.isSubscribed());
            ps.setInt(6, customer.getId());

            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated == 1;
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String first = rs.getString("first_name");
        String last = rs.getString("last_name");
        String email = rs.getString("email");
        String role = rs.getString("role");

        switch (role) {
            case "CUSTOMER" -> {
                boolean subscribed = rs.getBoolean("subscribed");
                String phone = rs.getString("phone");
                return new Customer(id, first, last, email, phone, subscribed);
            }
            case "AGENT" -> {
                return new Agent(id, first, last, email);
            }
            case "ADMIN" -> {
                return new Admin(id, first, last, email);
            }
            default -> throw new SQLException("Unknown user role: " + role);
        }
    }
}
