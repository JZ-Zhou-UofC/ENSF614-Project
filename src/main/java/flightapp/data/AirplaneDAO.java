package flightapp.data;

import flightapp.business.domain.Airplane;
import flightapp.business.domain.Seat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AirplaneDAO {

    private final SeatDAO seatDAO = new SeatDAO();




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



    public void update(Airplane airplane) throws SQLException {
        String sql = """
            UPDATE airplanes
            SET model = ?, rows = ?, seat_letters = ?
            WHERE id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(2, airplane.getNumRows());
            ps.setString(3, String.valueOf(airplane.getSeatLetters()));
            ps.setInt(5, airplane.getId());

            ps.executeUpdate();
        }
    }

    // helper method that maps the airplane info to an airplane object
    private Airplane mapRow(ResultSet rs) throws SQLException {
        Airplane plane = new Airplane();

        plane.setId(rs.getInt("id"));
        plane.setNumRows(rs.getInt("num_rows"));

        String lettersString = rs.getString("seat_letters");
        plane.setSeatLetters(lettersString.toCharArray());


        List<Seat> seats = seatDAO.findByAirplaneId(plane.getId());
        plane.setSeats(seats);

        return plane;
    }
}

