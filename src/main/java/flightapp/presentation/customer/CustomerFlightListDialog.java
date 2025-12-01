package flightapp.presentation.customer;

import flightapp.business.controller.BookingController;
import flightapp.business.controller.FlightController;
import flightapp.business.domain.Customer;
import flightapp.business.domain.Flight;
import flightapp.business.domain.Reservation;
import flightapp.presentation.agent.SeatSelectionDialog;
import flightapp.presentation.general.FlightTableModel;
import flightapp.presentation.general.ReservationTableModel;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CustomerFlightListDialog extends JDialog {

    private final FlightController flightController;
    private final BookingController bookingController;
    private final Customer currentCustomer;

    private JTable tableAvailable;
    private JTable tableMyBookings;

    public CustomerFlightListDialog(
            Window parent,
            FlightController flightController,
            BookingController bookingController,
            Customer currentCustomer
    ) {
        super(parent, "Book a Flight", ModalityType.APPLICATION_MODAL);
        this.flightController = flightController;
        this.bookingController = bookingController;
        this.currentCustomer = currentCustomer;

        setSize(820, 520);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Available Flights", createAvailableFlightsPanel());
        tabs.add("My Bookings", createMyBookingsPanel());
        add(tabs, BorderLayout.CENTER);

        // Buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBook = new JButton("Book Selected Flight");
        JButton btnClose = new JButton("Close");
        bottom.add(btnBook);
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);

        btnBook.addActionListener(e -> openBookingDialog());
        btnClose.addActionListener(e -> dispose());
    }

    private JPanel createAvailableFlightsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        try {
            List<Flight> list = flightController.getAllFlights();
            tableAvailable = new JTable(new FlightTableModel(list));
            panel.add(new JScrollPane(tableAvailable), BorderLayout.CENTER);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading flights:\n" + ex.getMessage());
        }
        return panel;
    }

    private JPanel createMyBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        try {
            List<Reservation> list = bookingController.getReservationsForCustomer(currentCustomer);
            tableMyBookings = new JTable(new ReservationTableModel(list));
            panel.add(new JScrollPane(tableMyBookings), BorderLayout.CENTER);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading bookings:\n" + ex.getMessage());
        }
        return panel;
    }

    private void openBookingDialog() {
        int row = tableAvailable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a flight.");
            return;
        }

        Flight flight = ((FlightTableModel) tableAvailable.getModel()).getFlightAt(row);

        // new BookingDialog(this, currentCustomer, flight, bookingController).setVisible(true);
        new SeatSelectionDialog(
                this,
                currentCustomer,
                currentCustomer,
                flight,
                bookingController
        ).setVisible(true);
        refreshBookings();
    }

    private void refreshBookings() {
        try {
            List<Reservation> list = bookingController.getReservationsForCustomer(currentCustomer);
            tableMyBookings.setModel(new ReservationTableModel(list));
        } catch (Exception ignored) {}
    }
}
