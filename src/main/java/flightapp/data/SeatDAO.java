package flightapp.data;

import flightapp.business.domain.Airplane;
import flightapp.business.domain.Seat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {


    public Seat findById(int id) throws SQLException {
        String sql = "SELECT * FROM seats WHERE id = ?";

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


    public List<Seat> findByAirplaneId(int airplaneId) throws SQLException {
        String sql = "SELECT * FROM seats WHERE airplane_id = ? ORDER BY seat_row, seat_letter";

        List<Seat> result = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, airplaneId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        }

        return result;
    }


    public Seat save(Seat seat) throws SQLException {
        String sql = """
            INSERT INTO seats (airplane_id, seat_row, seat_letter, seat_type)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, seat.getAirplaneId());
            ps.setInt(2, seat.getRow());
            ps.setString(3, String.valueOf(seat.getLetter()));
            ps.setString(4, seat.getSeatType());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    seat.setId(rs.getInt(1));
                }
            }
        }

        return seat;
    }


    public void saveAll(List<Seat> seats) throws SQLException {
        String sql = """
            INSERT INTO seats (airplane_id, seat_row, seat_letter, seat_type)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Seat seat : seats) {
                ps.setInt(1, seat.getAirplaneId());
                ps.setInt(2, seat.getRow());
                ps.setString(3, String.valueOf(seat.getLetter()));
                ps.setString(4, seat.getSeatType());
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }


    private Seat mapRow(ResultSet rs) throws SQLException {
        Seat seat = new Seat();

        seat.setId(rs.getInt("id"));
        seat.setAirplaneId(rs.getInt("airplane_id"));
        seat.setRow(rs.getInt("seat_row"));
        seat.setLetter(rs.getString("seat_letter").charAt(0));
        seat.setSeatType(rs.getString("seat_type"));

        return seat;
    }



}
