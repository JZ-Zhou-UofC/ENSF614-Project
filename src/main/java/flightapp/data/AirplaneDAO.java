package flightapp.data;

import flightapp.business.domain.Airplane;
import flightapp.business.domain.Seat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AirplaneDAO {

    private final SeatDAO seatDAO = new SeatDAO(); // needed to load seats


    public Airplane save(Airplane airplane) throws SQLException {
        String sql = """
            INSERT INTO airplanes (model, num_rows, seat_letters, reserved_status)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, airplane.getModel());                            // FIXED
            ps.setInt(2, airplane.getNumRows());                             // FIXED POSITION
            ps.setString(3, String.valueOf(airplane.getSeatLetters()));      // FIXED POSITION
            ps.setBoolean(4, airplane.isReservedStatus());                   // FIXED POSITION

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    airplane.setId(rs.getInt(1));
                }
            }
        }

        // Auto-create seats
        seatDAO.insertSeatsForAirplane(airplane);

        // Reload seats so the POJO has them in memory
        airplane.setSeats(seatDAO.findByAirplaneId(airplane.getId()));

        return airplane;
    }




    public Airplane findById(int id) throws SQLException {
        String sql = "SELECT * FROM airplanes WHERE id = ?";

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


    public List<Airplane> findAll() throws SQLException {
        String sql = "SELECT * FROM airplanes";
        List<Airplane> result = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRow(rs));
            }
        }
        return result;
    }


    public List<Airplane> findAllAvailable() throws SQLException {
        String sql = "SELECT * FROM airplanes WHERE reserved_status = FALSE";
        List<Airplane> result = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRow(rs));
            }
        }

        return result;
    }

    public void update(Airplane airplane) throws SQLException {
        String sql = """
            UPDATE airplanes
            SET model = ?, rows = ?, seat_letters = ?, reserved_status = ?
            WHERE id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(2, airplane.getNumRows());
            ps.setString(3, String.valueOf(airplane.getSeatLetters()));
            ps.setBoolean(4, airplane.isReservedStatus());
            ps.setInt(5, airplane.getId());

            ps.executeUpdate();
        }
    }


    private Airplane mapRow(ResultSet rs) throws SQLException {
        Airplane plane = new Airplane();

        plane.setId(rs.getInt("id"));
        plane.setNumRows(rs.getInt("num_rows"));

        String lettersString = rs.getString("seat_letters");
        plane.setSeatLetters(lettersString.toCharArray());

        plane.setReservedStatus(rs.getBoolean("reserved_status"));

        // Load seats for this airplane (from seat table)
        List<Seat> seats = seatDAO.findByAirplaneId(plane.getId());
        plane.setSeats(seats);

        return plane;
    }
}

