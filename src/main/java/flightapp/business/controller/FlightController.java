package flightapp.business.controller;

import flightapp.business.domain.Admin;
import flightapp.business.domain.Flight;
import flightapp.data.FlightDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FlightController {

    private final FlightDAO flightDAO;

    // Controller manages its own DAO
    public FlightController() {
        this.flightDAO = new FlightDAO();
    }

    // Optional constructor for testing
    public FlightController(FlightDAO flightDAO) {
        this.flightDAO = flightDAO;
    }

    // ----- SEARCH -----
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

    // ----- GET ALL -----
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

        // Admin auditing fields
        flight.setLastModifiedAt(LocalDateTime.now());
        flight.setLastModifiedByUserId(admin.getId());

        return flightDAO.update(flight);
    }
}
