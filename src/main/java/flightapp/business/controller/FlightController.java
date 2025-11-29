package flightapp.business.controller;

import flightapp.business.domain.Admin;
import flightapp.business.domain.Airplane;
import flightapp.business.domain.Flight;
import flightapp.business.domain.FlightSeat;
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

    public Flight createFlight(Flight flight) {
        Flight savedFlight = FlightDAO.saveFlight(flight);

            Flight saved = flightDAO.save(flight);

        // 2. Load airplane seats for this airplane
        Airplane airplane = airplaneDAO.findById(saved.getAirplaneId());
        List<AirplaneSeat> airplaneSeats = airplaneSeatDAO.findByAirplaneId(airplane.getId());

        // 3. Generate FlightSeat objects
        List<FlightSeat> flightSeats = airplaneSeats.stream()
                .map(seat -> new FlightSeat(saved.getId(), seat.getId(), false))
                .collect(Collectors.toList());

        // 4. Save them
        flightSeatDAO.saveAll(flightSeats);

        // 5. Attach to flight POJO
        saved.setSeats(flightSeats);

        return saved;
    }
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
