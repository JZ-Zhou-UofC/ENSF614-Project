package flightapp.presentation;

import flightapp.business.domain.Flight;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FlightTableModel extends AbstractTableModel {

    private final String[] columns = {
            "ID", "Origin", "Destination",
            "Departure", "Arrival",
            "Price", "Seats"
    };

    private final List<Flight> flights = new ArrayList<>();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void setFlights(List<Flight> list) {
        flights.clear();
        if (list != null) {
            flights.addAll(list);
        }
        fireTableDataChanged();
    }

    public Flight getFlightAt(int row) {
        if (row < 0 || row >= flights.size()) return null;
        return flights.get(row);
    }

    @Override
    public int getRowCount() {
        return flights.size();
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
        return switch (columnIndex) {
            case 0 -> f.getId();
            case 1 -> f.getOrigin();
            case 2 -> f.getDestination();
            case 3 -> f.getDepartureTime() != null ? fmt.format(f.getDepartureTime()) : "";
            case 4 -> f.getArrivalTime() != null ? fmt.format(f.getArrivalTime()) : "";
            case 5 -> f.getPrice();
            case 6 -> f.getSeatsAvailable();
            default -> "";
        };
    }
}
