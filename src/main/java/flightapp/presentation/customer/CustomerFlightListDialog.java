package flightapp.presentation.customer;

import flightapp.business.AppSession;
import flightapp.business.controller.BookingController;
import flightapp.business.domain.Customer;
import flightapp.business.domain.Flight;
import flightapp.business.domain.Reservation;
import flightapp.data.FlightDAO;
import flightapp.data.ReservationDAO;
import flightapp.presentation.general.FlightTableModel;
import flightapp.presentation.general.ReservationTableModel;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CustomerFlightListDialog extends JDialog {

    private final FlightDAO flightDAO;
    private final BookingController bookingController;

    private JTable tableAvailable;
    private JTable tableMyBookings;

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final AppSession session;

    public CustomerFlightListDialog(Window parent,
                                    FlightDAO flightDAO,
                                    BookingController bookingController) {

        super(parent, "Book a Flight", ModalityType.APPLICATION_MODAL);
        this.flightDAO = flightDAO;
        this.bookingController = bookingController;
        this.session = bookingController.getSession();

        setSize(800, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        //
        // --- TABS ---
        //
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Available Flights", createAvailableFlightsPanel());
        tabs.add("My Bookings", createMyBookingsPanel());
        add(tabs, BorderLayout.CENTER);

        //
        // --- BOTTOM BUTTONS ---
        //
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBook = new JButton("Book Selected Flight");
        JButton btnClose = new JButton("Close");

        bottom.add(btnBook);
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);

        btnBook.addActionListener(e -> doBook());
        btnClose.addActionListener(e -> dispose());
    }

    //
    // TAB: Available Flights
    //
    private JPanel createAvailableFlightsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        try {
            List<Flight> list = flightDAO.findAll();
            tableAvailable = new JTable(new FlightTableModel(list));
            panel.add(new JScrollPane(tableAvailable), BorderLayout.CENTER);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading flights: " + ex.getMessage());
        }

        return panel;
    }

    //
    // TAB: My Bookings
    //
    private JPanel createMyBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        try {
            Customer c = (Customer) session.getCurrentUser();
            List<Reservation> list = reservationDAO.findByCustomer(c.getId());
            tableMyBookings = new JTable(new ReservationTableModel(list));
            panel.add(new JScrollPane(tableMyBookings), BorderLayout.CENTER);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + ex.getMessage());
        }

        return panel;
    }

    //
    // BOOK A FLIGHT
    //
    private void doBook() {
        int row = tableAvailable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a flight first.");
            return;
        }

        Flight flight = ((FlightTableModel) tableAvailable.getModel()).getFlightAt(row);
        new CustomerBookingDialog(this, flight, bookingController).setVisible(true);

        refreshBookings();
    }

    private void refreshBookings() {
        try {
            Customer c = (Customer) session.getCurrentUser();
            List<Reservation> list = reservationDAO.findByCustomer(c.getId());
            tableMyBookings.setModel(new ReservationTableModel(list));
        } catch (Exception ignored) {}
    }
}
