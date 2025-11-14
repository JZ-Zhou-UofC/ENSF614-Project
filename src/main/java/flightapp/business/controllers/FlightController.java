package flightapp.business.controllers;

import java.time.LocalDate;
import java.util.List;

import flightapp.business.domain.Flight;
import flightapp.data.FlightDAO;

public class FlightController {

    private final FlightDAO flightDAO = new FlightDAO();

    public List<Flight> searchFlights(String origin, String destination, LocalDate date) {
        return flightDAO.searchFlights(origin, destination, date);
    }

    public List<Flight> getAllFlights() {
        return flightDAO.findAll();
    }

    public Flight addFlight(Flight flight) {
        return flightDAO.create(flight);
    }

    public void updateFlight(Flight flight) {
        flightDAO.update(flight);
    }

    public void deleteFlight(int id) {
        flightDAO.delete(id);
    }
}
