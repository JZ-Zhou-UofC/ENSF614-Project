package flightapp.data;

import flightapp.business.domain.Customer;
import flightapp.business.domain.Flight;
import flightapp.business.domain.Reservation;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    // ---------------------------------------------------------
    // INSERT NEW RESERVATION
    // ---------------------------------------------------------
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

            // booked_at
            LocalDateTime bookedAt = r.getBookedAt() != null
                    ? r.getBookedAt()
                    : LocalDateTime.now();
            ps.setTimestamp(4, Timestamp.valueOf(bookedAt));

            // booked_by_user_id
            ps.setInt(5, r.getBookedByUserId());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    r.setId(keys.getInt(1));
                }
            }
        }
        return r;
    }

    // ---------------------------------------------------------
    // UPDATE RESERVATION (change seat count or flight)
    // ---------------------------------------------------------
    public Reservation update(Reservation r) throws SQLException {
        String sql = """
                UPDATE reservations SET
                  flight_id = ?,
                  seat_count = ?,
                  modified_at = ?,
                  modified_by_user_id = ?
                WHERE id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, r.getFlight().getId());
            ps.setInt(2, r.getSeatCount());

            // modified_at
            if (r.getModifiedAt() != null) {
                ps.setTimestamp(3, Timestamp.valueOf(r.getModifiedAt()));
            } else {
                ps.setNull(3, Types.TIMESTAMP);
            }

            // modified_by_user_id
            if (r.getModifiedByUserId() != null) {
                ps.setInt(4, r.getModifiedByUserId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setInt(5, r.getId());

            ps.executeUpdate();
        }
        return r;
    }

    // ---------------------------------------------------------
    // FIND ALL RESERVATIONS OF A CUSTOMER
    // ---------------------------------------------------------
    public List<Reservation> findByCustomer(int customerId) throws SQLException {
        String sql = """
                SELECT r.id             AS res_id,
                       r.seat_count     AS res_seat_count,
                       r.booked_at      AS res_booked_at,
                       r.booked_by_user_id,

                       f.id             AS f_id,
                       f.origin,
                       f.destination,
                       f.departure_time,
                       f.arrival_time,
                       f.price,
                       f.seats_available
                FROM reservations r
                JOIN flights f ON r.flight_id = f.id
                WHERE r.customer_id = ?
                ORDER BY f.departure_time
                """;

        List<Reservation> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    // ---------------------
                    // Build Flight object
                    // ---------------------
                    Flight flight = new Flight();
                    flight.setId(rs.getInt("f_id"));
                    flight.setOrigin(rs.getString("origin"));
                    flight.setDestination(rs.getString("destination"));

                    Timestamp dep = rs.getTimestamp("departure_time");
                    Timestamp arr = rs.getTimestamp("arrival_time");

                    flight.setDepartureTime(dep != null ? dep.toLocalDateTime() : null);
                    flight.setArrivalTime(arr != null ? arr.toLocalDateTime() : null);
                    flight.setPrice(rs.getDouble("price"));
                    flight.setSeatsAvailable(rs.getInt("seats_available"));

                    // ---------------------
                    // Build Reservation object
                    // ---------------------
                    Reservation r = new Reservation();
                    r.setId(rs.getInt("res_id"));
                    r.setSeatCount(rs.getInt("res_seat_count"));

                    Timestamp bookedTs = rs.getTimestamp("res_booked_at");
                    r.setBookedAt(bookedTs != null ? bookedTs.toLocalDateTime() : null);

                    r.setBookedByUserId(rs.getInt("booked_by_user_id"));

                    // Attach nested objects
                    Customer c = new Customer();
                    c.setId(customerId);
                    r.setCustomer(c);
                    r.setFlight(flight);

                    list.add(r);
                }
            }
        }

        return list;
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM reservations WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

}
