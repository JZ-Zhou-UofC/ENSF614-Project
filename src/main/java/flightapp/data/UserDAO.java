package flightapp.data;

import flightapp.business.domain.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT id, first_name, last_name, email, role, phone, subscribed FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return null;

                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String role = rs.getString("role");
                String phone = rs.getString("phone");

                return switch (role) {
                    case "CUSTOMER" -> {
                        boolean subscribed = rs.getBoolean("subscribed");
                        yield new Customer(id, firstName, lastName, email, phone, subscribed);
                    }
                    case "AGENT" -> new Agent(id, firstName, lastName, email);
                    case "ADMIN" -> new Admin(id, firstName, lastName, email);
                    default -> null;
                };
            }
        }
    }

    public List<Customer> findAllCustomers() throws SQLException {
        String sql = "SELECT id, first_name, last_name, email, phone, subscribed FROM users WHERE role = 'CUSTOMER'";
        List<Customer> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Customer c = new Customer();
                c.setId(rs.getInt("id"));
                c.setFirstName(rs.getString("first_name"));
                c.setLastName(rs.getString("last_name"));
                c.setEmail(rs.getString("email"));
                c.setPhone(rs.getString("phone"));
                c.setSubscribed(rs.getBoolean("subscribed"));
                list.add(c);
            }
        }

        return list;
    }

    public User registerCustomer(String firstName, String lastName, String email, boolean subscribed) throws SQLException {

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
                            null, // phone is optional
                            subscribed
                    );
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }



	public User findById(int id) throws SQLException {
	    String sql = "SELECT id, first_name, last_name, email, role, phone, subscribed FROM users WHERE id = ?";
	    try (Connection conn = DBConnection.getConnection();
	            PreparedStatement ps = conn.prepareStatement(sql)) {
	
	        ps.setInt(1, id);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (!rs.next())
	                return null;
	
	            String email = rs.getString("email");
	            String firstName = rs.getString("first_name");
	            String lastName = rs.getString("last_name");
	            String role = rs.getString("role");
	            String phone = rs.getString("phone");
	
	            return switch (role) {
	                case "CUSTOMER" -> {
	                    boolean subscribed = rs.getBoolean("subscribed");
	                    yield new Customer(id, firstName, lastName, email, phone, subscribed);
	                }
	                case "AGENT" -> new Agent(id, firstName, lastName, email);
	                case "ADMIN" -> new Admin(id, firstName, lastName, email);
	                default -> null;
	            };
	        }
	    }
	}

	
	public Customer updateCustomer(Customer customer) throws SQLException {
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
			
			int affectedRows = ps.executeUpdate();
			
			if (affectedRows == 0) {
				throw new SQLException("Updating customer failed, no rows affected. Customer may not exist or is not a CUSTOMER role.");
			}
			
			return customer;
		}
	}
	
	
}
