package flightapp.business.service;

import flightapp.business.domain.Admin;
import flightapp.business.domain.Flight;
import flightapp.data.FlightDAO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;

public class FlightService {

    private final FlightDAO flightDAO;

    public FlightService(FlightDAO flightDAO) {
        this.flightDAO = flightDAO;
    }

    public Flight updateScheduleAsAdmin(Admin admin, Flight flight) throws SQLException {
        Objects.requireNonNull(admin);
        Objects.requireNonNull(flight);

        flight.setLastModifiedAt(LocalDateTime.now());
        flight.setLastModifiedByUserId(admin.getId());
        return flightDAO.update(flight);
    }
}
