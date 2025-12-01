package flightapp.presentation.agent;

import flightapp.business.controller.BookingController;
import flightapp.business.controller.FlightController;
import flightapp.business.domain.Agent;
import flightapp.business.domain.Customer;
import flightapp.business.domain.Flight;
import flightapp.business.domain.Reservation;
import flightapp.presentation.general.FlightFilterDialog;
import flightapp.presentation.general.ReservationTableModel;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AgentManageCustomerDialog extends JDialog {

    private final Agent agentUser;
    private final Customer targetCustomer;
    private final FlightController flightController;
    private final BookingController bookingController;

    private JTable reservationTable;

    public AgentManageCustomerDialog(
            Window parent,
            Agent agentUser,
            Customer targetCustomer,
            FlightController flightController,
            BookingController bookingController
    ) {
        super(parent, "Manage Customer", ModalityType.APPLICATION_MODAL);

        this.agentUser = agentUser;
        this.targetCustomer = targetCustomer;
        this.flightController = flightController;
        this.bookingController = bookingController;

        setSize(820, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel(
                "<html><h2>Managing Customer: " + targetCustomer.getFirstName() + " " + targetCustomer.getLastName() +
                        " &nbsp;(ID: " + targetCustomer.getId() + ")</h2></html>"
        );
        add(title, BorderLayout.NORTH);

        loadReservations();

        JButton btnBook = new JButton("Book Flight for Customer");
        JButton btnModify = new JButton("Modify Selected Reservation");
        JButton btnClose = new JButton("Close");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnBook);
        bottom.add(btnModify);
        bottom.add(btnClose);

        add(bottom, BorderLayout.SOUTH);

        btnBook.addActionListener(e -> openFlightSelector());
        btnModify.addActionListener(e -> openModifyDialog());
        btnClose.addActionListener(e -> dispose());
    }

    // load customer reservations
    private void loadReservations() {
        try {
            List<Reservation> list =
                    bookingController.getReservationsForCustomer(targetCustomer);

            reservationTable = new JTable(new ReservationTableModel(list));
            add(new JScrollPane(reservationTable), BorderLayout.CENTER);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading reservations:\n" + ex.getMessage());
        }
    }

    // Reload table after changes
    private void reload() {
        try {
            List<Reservation> list =
                    bookingController.getReservationsForCustomer(targetCustomer);

            reservationTable.setModel(new ReservationTableModel(list));
        } catch (Exception ignored) {}
    }

    // book a new flight
    private void openFlightSelector() {
        new AgentBookFlightDialog(
                this,
                agentUser,
                targetCustomer,
                flightController,
                bookingController
        ).setVisible(true);

        reload();
    }

    // modify reservation
    private void openModifyDialog() {
        int row = reservationTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a reservation to modify.");
            return;
        }

        Reservation r = ((ReservationTableModel) reservationTable.getModel()).getReservationAt(row);

        new ModifyReservationDialog(this,r,agentUser,targetCustomer,bookingController).setVisible(true);

        reload();
    }
}
