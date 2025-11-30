package flightapp.data;

import flightapp.business.domain.FlightSeat;
import flightapp.business.domain.Seat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlightSeatDAO {

    private final SeatDAO seatDAO = new SeatDAO();


    public FlightSeat findById(int id) throws SQLException {
        String sql = "SELECT * FROM flight_seats WHERE id = ?";

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
    
    public FlightSeat findByFlightAndSeat(int flightId, int seatId) throws SQLException {
        String sql = """
            SELECT * FROM flight_seats
            WHERE flight_id = ? AND seat_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, flightId);
            ps.setInt(2, seatId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }

        return null;
    }


    public List<FlightSeat> findByFlight(int flightId) throws SQLException {
        String sql = "SELECT * FROM flight_seats WHERE flight_id = ? ORDER BY seat_id";

        List<FlightSeat> result = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, flightId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        }

        return result;
    }


    public void insertAll(List<FlightSeat> seats) throws SQLException {
        String sql = """
            INSERT INTO flight_seats (flight_id, seat_id, reserved)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (FlightSeat fs : seats) {
                ps.setInt(1, fs.getFlightId());
                ps.setInt(2, fs.getSeat().getId());
                ps.setBoolean(3, fs.isReserved());
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }


    public void update(FlightSeat fs) throws SQLException {
        String sql = """
            UPDATE flight_seats
            SET reserved = ?
            WHERE id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, fs.isReserved());
            ps.setInt(2, fs.getId());

            ps.executeUpdate();
        }
    }


    public void deleteByFlight(int flightId) throws SQLException {
        String sql = "DELETE FROM flight_seats WHERE flight_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, flightId);
            ps.executeUpdate();
        }
    }


    private FlightSeat mapRow(ResultSet rs) throws SQLException {
        FlightSeat fs = new FlightSeat();

        fs.setId(rs.getInt("id"));
        fs.setFlightId(rs.getInt("flight_id"));

        int seatId = rs.getInt("seat_id");
        Seat seat = seatDAO.findById(seatId);
        fs.setSeat(seat);

        fs.setReserved(rs.getBoolean("reserved"));

        return fs;
    }
}
