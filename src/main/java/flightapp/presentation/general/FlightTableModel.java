package flightapp.presentation.general;

import flightapp.business.domain.Flight;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class FlightTableModel extends AbstractTableModel {

    private final String[] columns = {
            "Origin", "Destination", "Departure", "Arrival", "Price", "Seats"
    };

    private List<Flight> flights;

    public FlightTableModel(List<Flight> flights) {
        this.flights = flights;
    }

    @Override
    public int getRowCount() {
        return flights == null ? 0 : flights.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Flight f = flights.get(rowIndex);

        switch (columnIndex) {
            case 0: return f.getOrigin();
            case 1: return f.getDestination();
            case 2: return f.getDepartureTime();
            case 3: return f.getArrivalTime();
            case 4: return f.getPrice();
            case 5: return f.getSeatsAvailable();
            default: return null;
        }
    }

    public Flight getFlightAt(int row) {
        return flights.get(row);
    }
}
