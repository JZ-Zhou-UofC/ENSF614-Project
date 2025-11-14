package flightapp.presentation;

import flightapp.business.domain.Reservation;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ReservationTableModel extends AbstractTableModel {

    private final String[] columns = {
            "Reservation ID", "Origin", "Destination", "Departure", "Price"
    };

    private List<Reservation> reservations;

    public ReservationTableModel(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    @Override
    public int getRowCount() {
        return reservations == null ? 0 : reservations.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        Reservation r = reservations.get(row);
        switch (col) {
            case 0: return r.getId();
            case 1: return r.getFlight().getOrigin();
            case 2: return r.getFlight().getDestination();
            case 3: return r.getFlight().getDepartureTime();
            case 4: return r.getFlight().getPrice();
            default: return null;
        }
    }
}
