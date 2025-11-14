package flightapp.business.controller;

import flightapp.business.domain.Flight;
import flightapp.business.domain.Admin;
import flightapp.business.service.FlightService;
import flightapp.data.FlightDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class FlightController {

    private final FlightDAO flightDAO;
    private final FlightService flightService;

    public FlightController(FlightDAO flightDAO, FlightService flightService) {
        this.flightDAO = flightDAO;
        this.flightService = flightService;
    }

    public List<Flight> searchFlights(String origin, String destination, LocalDate date) throws SQLException {
        List<Flight> all = flightDAO.findAll();

        return all.stream()
                .filter(f -> origin == null || origin.isBlank() || f.getOrigin().equalsIgnoreCase(origin))
                .filter(f -> destination == null || destination.isBlank() || f.getDestination().equalsIgnoreCase(destination))
                .filter(f -> date == null || (f.getDepartureTime() != null && f.getDepartureTime().toLocalDate().equals(date)))
                .collect(Collectors.toList());
    }

    public List<Flight> getAllFlights() throws SQLException {
        return flightDAO.findAll();
    }

    public Flight updateFlight(Admin admin, Flight flight) throws SQLException {
        return flightService.updateScheduleAsAdmin(admin, flight);
    }
}
