package flightapp.presentation.customer;

import flightapp.business.controller.BookingController;
import flightapp.business.controller.FlightController;
import flightapp.business.domain.Customer;
import flightapp.business.domain.Flight;
import flightapp.business.domain.Reservation;
import flightapp.presentation.agent.SeatSelectionDialog;
import flightapp.presentation.general.FlightFilterDialog;
import flightapp.presentation.general.ReservationTableModel;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CustomerFlightListDialog extends JDialog {

    private final FlightController flightController;
    private final BookingController bookingController;
    private final Customer currentCustomer;

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

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Available Flights", createAvailableFlightsPanel());
        tabs.add("My Bookings", createMyBookingsPanel());
        add(tabs, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBook = new JButton("Browse & Book Flight");
        JButton btnClose = new JButton("Close");

        bottom.add(btnBook);
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);

        btnBook.addActionListener(e -> openBookingFlow());
        btnClose.addActionListener(e -> dispose());
    }

    // ✅ OPEN FILTER DIALOG ONLY (NO TABLE EMBEDDED)
    private JPanel createAvailableFlightsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton btnBrowse = new JButton("Browse & Filter Flights");
        panel.add(btnBrowse);

        btnBrowse.addActionListener(e -> openBookingFlow());
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

    // ✅ FULL BOOKING FLOW
    private void openBookingFlow() {
        try {
            List<Flight> list = flightController.getAllFlights();

            FlightFilterDialog dialog = new FlightFilterDialog(this, list);
            dialog.setVisible(true);

            Flight selected = dialog.getSelectedFlight();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "No flight selected.");
                return;
            }

            new SeatSelectionDialog(
                    this,
                    currentCustomer,
                    currentCustomer,
                    selected,
                    bookingController
            ).setVisible(true);

            refreshBookings();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading flights:\n" + ex.getMessage());
        }
    }

    private void refreshBookings() {
        try {
            List<Reservation> list = bookingController.getReservationsForCustomer(currentCustomer);
            tableMyBookings.setModel(new ReservationTableModel(list));
        } catch (Exception ignored) {}
    }
}
