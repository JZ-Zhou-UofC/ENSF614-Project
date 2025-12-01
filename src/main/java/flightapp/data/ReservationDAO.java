package flightapp.data;

import flightapp.business.domain.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    private final UserDAO userDAO = new UserDAO();
    private final FlightDAO flightDAO = new FlightDAO();
    private final FlightSeatDAO flightSeatDAO = new FlightSeatDAO();


    public Reservation insert(Reservation r) throws SQLException {

        String sql = """
            INSERT INTO reservations (
                customer_id, flight_id, flight_seat_id,
                booked_at, booked_by_user_id
            ) VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, r.getCustomer().getId());
            ps.setInt(2, r.getFlight().getId());
            ps.setInt(3, r.getFlightSeat().getId());
            ps.setTimestamp(4, Timestamp.valueOf(r.getBookedAt()));
            ps.setInt(5, r.getBookedByUserId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    r.setId(rs.getInt(1));
                }
            }
        }

        return r;
    }


    public Reservation update(Reservation r) throws SQLException {

        String sql = """
            UPDATE reservations SET
                customer_id = ?, 
                flight_id = ?, 
                flight_seat_id = ?, 
                booked_at = ?, 
                modified_at = ?, 
                booked_by_user_id = ?, 
                modified_by_user_id = ?
            WHERE id = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, r.getCustomer().getId());
            ps.setInt(2, r.getFlight().getId());
            ps.setInt(3, r.getFlightSeat().getId());
            ps.setTimestamp(4, Timestamp.valueOf(r.getBookedAt()));

            if (r.getModifiedAt() != null)
                ps.setTimestamp(5, Timestamp.valueOf(r.getModifiedAt()));
            else
                ps.setNull(5, Types.TIMESTAMP);

            ps.setObject(6, r.getBookedByUserId(), Types.INTEGER);
            ps.setObject(7, r.getModifiedByUserId(), Types.INTEGER);
            ps.setInt(8, r.getId());

            ps.executeUpdate();
        }

        return r;
    }


    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM reservations WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Delete by flight
    public void deleteByFlight(int flightId) throws SQLException {
        String sql = "DELETE FROM reservations WHERE flight_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, flightId);
            ps.executeUpdate();
        }
    }


    public Reservation findById(int id) throws SQLException {
        String sql = "SELECT * FROM reservations WHERE id = ?";

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


    public List<Reservation> findByCustomer(int customerId) throws SQLException {

        String sql = "SELECT * FROM reservations WHERE customer_id = ?";
        List<Reservation> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }

        return list;
    }


    public List<Reservation> findByFlight(int flightId) throws SQLException {

        String sql = "SELECT * FROM reservations WHERE flight_id = ?";
        List<Reservation> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, flightId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }

        return list;
    }

    private Reservation mapRow(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();

        r.setId(rs.getInt("id"));

        int customerId = rs.getInt("customer_id");
        int flightId = rs.getInt("flight_id");
        int flightSeatId = rs.getInt("flight_seat_id");

        r.setCustomer((Customer) userDAO.findById(customerId));
        r.setFlight(flightDAO.findById(flightId));
        r.setFlightSeat(flightSeatDAO.findById(flightSeatId));

        Timestamp booked = rs.getTimestamp("booked_at");
        Timestamp modified = rs.getTimestamp("modified_at");

        if (booked != null)
            r.setBookedAt(booked.toLocalDateTime());

        if (modified != null)
            r.setModifiedAt(modified.toLocalDateTime());

        r.setBookedByUserId((Integer) rs.getObject("booked_by_user_id"));
        r.setModifiedByUserId((Integer) rs.getObject("modified_by_user_id"));

        return r;
    }
}


