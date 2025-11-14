package flightapp.presentation;

import flightapp.business.controller.BookingController;
import flightapp.business.domain.Flight;
import flightapp.data.FlightDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CustomerFlightListDialog extends JDialog {

    private final FlightDAO flightDAO;
    private final BookingController bookingController;

    private JTable table;

    public CustomerFlightListDialog(Window parent, FlightDAO flightDAO, BookingController bookingController) {
        super(parent, "Book a Flight", ModalityType.APPLICATION_MODAL);
        this.flightDAO = flightDAO;
        this.bookingController = bookingController;

        setSize(750, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        loadFlights();

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBook = new JButton("Book Selected Flight");
        JButton btnClose = new JButton("Close");

        bottom.add(btnBook);
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);

        btnBook.addActionListener(e -> doBook());
        btnClose.addActionListener(e -> dispose());
    }

    private void loadFlights() {
        try {
            List<Flight> flights = flightDAO.findAll();
            table = new JTable(new FlightTableModel(flights));
            add(new JScrollPane(table), BorderLayout.CENTER);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading flights: " + ex.getMessage());
        }
    }

    private void doBook() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a flight first.");
            return;
        }

        Flight flight = ((FlightTableModel) table.getModel()).getFlightAt(row);
        new CustomerBookingDialog(this, flight, bookingController).setVisible(true);
    }
}
