package flightapp.presentation.agent;

import flightapp.business.AppSession;
import flightapp.business.controller.BookingController;
import flightapp.business.domain.Flight;
import flightapp.business.domain.Reservation;
import flightapp.data.FlightDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

import flightapp.presentation.FlightTableModel;

public class AgentBookFlightDialog extends JDialog {

    private final AppSession session;
    private final BookingController bookingController;
    private final FlightDAO flightDAO = new FlightDAO();

    private JTable table;

    public AgentBookFlightDialog(Window parent,
                                 AppSession session,
                                 BookingController bookingController) {
        super(parent, "Book Flight for Customer", ModalityType.APPLICATION_MODAL);

        this.session = session;
        this.bookingController = bookingController;

        setSize(750, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        loadFlights();

        JButton btnBook = new JButton("Book Selected");
        JButton btnClose = new JButton("Close");

        JPanel bottom = new JPanel();
        bottom.add(btnBook);
        bottom.add(btnClose);

        btnBook.addActionListener(e -> doBook());
        btnClose.addActionListener(e -> dispose());

        add(bottom, BorderLayout.SOUTH);
    }

    private void loadFlights() {
        try {
            List<Flight> list = flightDAO.findAll();
            table = new JTable(new FlightTableModel(list));
            add(new JScrollPane(table), BorderLayout.CENTER);
        } catch (SQLException ex) {
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

        try {
            Reservation r = bookingController.book(flight, 1);
            JOptionPane.showMessageDialog(this,
                    "Flight booked for customer!\nReservation ID: " + r.getId());
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error booking: " + ex.getMessage());
        }
    }
}
