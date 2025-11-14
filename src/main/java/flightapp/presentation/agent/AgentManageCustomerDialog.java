package flightapp.presentation.agent;

import flightapp.business.AppSession;
import flightapp.business.controller.BookingController;
import flightapp.business.domain.Customer;
import flightapp.business.domain.Flight;
import flightapp.business.domain.Reservation;
import flightapp.data.FlightDAO;
import flightapp.data.ReservationDAO;
import flightapp.presentation.FlightTableModel;
import flightapp.presentation.ReservationTableModel;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AgentManageCustomerDialog extends JDialog {

    private final AppSession session;
    private final BookingController bookingController;

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final FlightDAO flightDAO = new FlightDAO();

    private JTable reservationTable;

    public AgentManageCustomerDialog(Window parent,
                                     AppSession session,
                                     BookingController bookingController) {
        super(parent, "Manage Customer", ModalityType.APPLICATION_MODAL);

        this.session = session;
        this.bookingController = bookingController;

        setSize(800, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        Customer customer = (Customer) session.getActiveCustomer();

        JLabel title = new JLabel(
                "<html><h2>Managing Customer: " + customer.getName() +
                        " (ID: " + customer.getId() + ")</h2></html>"
        );

        add(title, BorderLayout.NORTH);

        loadReservations();

        JButton btnBook = new JButton("Book Flight for Customer");
        JButton btnModify = new JButton("Modify Selected Reservation");
        JButton btnClose = new JButton("Close");

        JPanel bottom = new JPanel();
        bottom.add(btnBook);
        bottom.add(btnModify);
        bottom.add(btnClose);

        btnBook.addActionListener(e -> openFlightSelector());
        btnModify.addActionListener(e -> modifyReservation());
        btnClose.addActionListener(e -> dispose());

        add(bottom, BorderLayout.SOUTH);
    }

    private void loadReservations() {
        try {
            Customer c = (Customer) session.getActiveCustomer();
            List<Reservation> list = reservationDAO.findByCustomer(c.getId());
            reservationTable = new JTable(new ReservationTableModel(list));
            add(new JScrollPane(reservationTable), BorderLayout.CENTER);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading reservations: " + ex.getMessage());
        }
    }

    private void openFlightSelector() {
        new AgentBookFlightDialog(this, session, bookingController).setVisible(true);
        reload();
    }

    private void reload() {
        try {
            Customer c = (Customer) session.getActiveCustomer();
            List<Reservation> list = reservationDAO.findByCustomer(c.getId());
            reservationTable.setModel(new ReservationTableModel(list));
        } catch (SQLException ignored) {}
    }

    private void modifyReservation() {
        JOptionPane.showMessageDialog(this, "Modify reservation functionality coming soon.");
    }
}
