package flightapp.presentation.agent;

import flightapp.business.controller.BookingController;
import flightapp.business.controller.FlightController;
import flightapp.business.domain.Agent;
import flightapp.business.domain.Customer;
import flightapp.business.domain.Flight;
import flightapp.business.domain.Reservation;
import flightapp.presentation.general.FlightTableModel;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AgentBookFlightDialog extends JDialog {

    private final Agent agentUser;
    private final Customer targetCustomer;
    private final FlightController flightController;
    private final BookingController bookingController;

    private JTable table;

    public AgentBookFlightDialog(
            Window parent,
            Agent agentUser,
            Customer targetCustomer,
            FlightController flightController,
            BookingController bookingController
    ) {
        super(parent, "Book Flight for Customer", ModalityType.APPLICATION_MODAL);

        this.agentUser = agentUser;
        this.targetCustomer = targetCustomer;
        this.flightController = flightController;
        this.bookingController = bookingController;

        setSize(800, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        loadFlights();

        JButton btnBook = new JButton("Book Selected Flight");
        JButton btnClose = new JButton("Close");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnBook);
        bottom.add(btnClose);

        btnBook.addActionListener(e -> doBook());
        btnClose.addActionListener(e -> dispose());

        add(bottom, BorderLayout.SOUTH);
    }

    private void loadFlights() {
        try {
            List<Flight> list = flightController.getAllFlights();
            table = new JTable(new FlightTableModel(list));
            add(new JScrollPane(table), BorderLayout.CENTER);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading flights:\n" + ex.getMessage());
        }
    }

    private void doBook() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a flight.");
            return;
        }

        Flight flight = ((FlightTableModel) table.getModel()).getFlightAt(row);

        try {
            Reservation r = bookingController.bookForAgent(
                    agentUser,
                    targetCustomer,
                    flight,
                    1
            );

            JOptionPane.showMessageDialog(this,
                    "Booking successful!\nReservation ID: " + r.getId(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error booking flight:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
