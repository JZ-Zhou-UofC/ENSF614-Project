package flightapp.business.controller;

import flightapp.util.SystemLogger;
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
        SystemLogger.logBusinessOperation(
            SystemLogger.SystemStatus.INFO,
            "SEARCH_FLIGHTS",
            String.format("Searching flights: origin=%s, destination=%s, date=%s", origin, destination, date)
        );
        
        try {
            List<Flight> all = flightDAO.findAll();
            List<Flight> results = all.stream()
                    .filter(f -> origin == null || origin.isBlank() ||
                            f.getOrigin().equalsIgnoreCase(origin))
                    .filter(f -> destination == null || destination.isBlank() ||
                            f.getDestination().equalsIgnoreCase(destination))
                    .filter(f -> date == null ||
                            (f.getDepartureTime() != null &&
                                    f.getDepartureTime().toLocalDate().equals(date)))
                    .collect(Collectors.toList());
            
            SystemLogger.logBusinessOperation(
                SystemLogger.SystemStatus.INFO,
                "SEARCH_FLIGHTS_SUCCESS",
                String.format("Found %d flights matching criteria", results.size())
            );
            
            return results;
        } catch (SQLException e) {
            SystemLogger.logBusinessOperation(
                SystemLogger.SystemStatus.ERROR,
                "SEARCH_FLIGHTS_FAILED",
                "Error searching flights: " + e.getMessage(),
                e
            );
            throw e;
        }
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

        SystemLogger.logUserAction(
            SystemLogger.SystemStatus.INFO,
            "ADMIN",
            "UPDATE_FLIGHT",
            String.format("Admin ID %d updating flight ID %d", admin.getId(), flight.getId())
        );

        // Admin auditing fields
        flight.setLastModifiedAt(LocalDateTime.now());
        flight.setLastModifiedByUserId(admin.getId());

        try {
            Flight updated = flightDAO.update(flight);
            SystemLogger.logBusinessOperation(
                SystemLogger.SystemStatus.INFO,
                "UPDATE_FLIGHT_SUCCESS",
                String.format("Flight ID %d updated successfully by Admin ID %d", flight.getId(), admin.getId())
            );
            return updated;
        } catch (SQLException e) {
            SystemLogger.logBusinessOperation(
                SystemLogger.SystemStatus.ERROR,
                "UPDATE_FLIGHT_FAILED",
                String.format("Failed to update flight ID %d: %s", flight.getId(), e.getMessage()),
                e
            );
            throw e;
        }
    }
    
    }
    