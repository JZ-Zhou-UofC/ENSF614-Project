package flightapp.business.controller;

import flightapp.business.domain.Admin;
import flightapp.business.domain.Airplane;
import flightapp.business.domain.Flight;
import flightapp.business.domain.FlightSeat;
import flightapp.data.FlightDAO;
import flightapp.data.AirplaneDAO;
import flightapp.data.FlightSeatDAO;
import flightapp.data.ReservationDAO;


import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FlightController {

    private final FlightDAO flightDAO;
    private final AirplaneDAO airplaneDAO;
    private final FlightSeatDAO flightSeatDAO;
    private final ReservationDAO reservationDAO;


    // Controller manages its own DAO
    public FlightController() {
        this.flightDAO = new FlightDAO();
        this.airplaneDAO = new AirplaneDAO();
        this.flightSeatDAO = new FlightSeatDAO();
        this.reservationDAO = new ReservationDAO();
    }

    // Optional constructor for testing
    public FlightController(FlightDAO flightDAO, AirplaneDAO airplaneDAO, FlightSeatDAO flightSeatDAO, ReservationDAO reservationDAO) {
        this.flightDAO = flightDAO;
        this.airplaneDAO = airplaneDAO;
        this.flightSeatDAO = flightSeatDAO;
        this.reservationDAO = reservationDAO;
    }





    public List<Airplane> findAllAvailableAirplanes() throws SQLException {
        return airplaneDAO.findAll();
    }


    public Flight createFlight(Admin admin, Flight flight) throws SQLException {
        if (admin == null) {
            throw new IllegalArgumentException("Admin cannot be null");
        }
        if (flight == null) {
            throw new IllegalArgumentException("Flight cannot be null");
        }

        Flight saved = flightDAO.save(flight);

        Airplane airplane = airplaneDAO.findById(saved.getAirplaneId());
        if (airplane == null) {
            throw new SQLException("Airplane ID " + saved.getAirplaneId() + " does not exist.");
        }

        // ChatGPT assisted with generating the flightSeats List
        List<FlightSeat> flightSeats = airplane.getSeats()
                .stream()
                .map(seat -> new FlightSeat(saved.getId(), seat, false))
                .collect(Collectors.toList());

        // Save the flightSeats
        flightSeatDAO.saveAll(flightSeats);

        // 
        saved.setSeatsAvailable(flightSeats.size());
        flightDAO.update(saved);

        return saved;
    }


    public void deleteFlight(Admin admin, int flightId) throws SQLException {

        if (admin == null)
            throw new IllegalArgumentException("Admin cannot be null");

        reservationDAO.deleteByFlight(flightId);

        flightSeatDAO.deleteByFlight(flightId);

        flightDAO.delete(flightId);
    }

    
    

    public List<Flight> searchFlights(String origin, String destination, LocalDate date) throws SQLException {
        List<Flight> all = flightDAO.findAll();

        return all.stream()
                .filter(f -> origin == null || origin.isBlank() ||
                        f.getOrigin().equalsIgnoreCase(origin))
                .filter(f -> destination == null || destination.isBlank() ||
                        f.getDestination().equalsIgnoreCase(destination))
                .filter(f -> date == null ||
                        (f.getDepartureTime() != null &&
                                f.getDepartureTime().toLocalDate().equals(date)))
                .collect(Collectors.toList());
    }

    public List<Flight> getAllFlights() throws SQLException {
        return flightDAO.findAll();
    }

    public Flight findById(int id) throws SQLException {
        return flightDAO.findById(id);
    }

    public Flight updateFlightAsAdmin(Admin admin, Flight flight) throws SQLException {
        if (admin == null)
            throw new IllegalArgumentException("Admin cannot be null.");
        if (flight == null)
            throw new IllegalArgumentException("Flight cannot be null.");

        flight.setLastModifiedAt(LocalDateTime.now());
        flight.setLastModifiedByUserId(admin.getId());

        return flightDAO.update(flight);
    }
}
