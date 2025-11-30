package flightapp.presentation.agent;

import flightapp.business.controller.BookingController;
import flightapp.business.controller.FlightController;
import flightapp.business.controller.UserController;
import flightapp.business.controller.AgentController;
import flightapp.business.domain.Agent;
import flightapp.business.domain.Customer;
import flightapp.business.domain.Reservation;
import flightapp.presentation.general.ReservationTableModel;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AgentManageCustomerDialog extends JDialog {

    private final Agent agentUser;
    private Customer targetCustomer; // Made non-final so we can update it
    private final FlightController flightController;
    private final BookingController bookingController;
    private final UserController userController;
    private final AgentController agentController;

    private JTable reservationTable;
    private JLabel titleLabel;

    public AgentManageCustomerDialog(
            Window parent,
            Agent agentUser,
            Customer targetCustomer,
            FlightController flightController,
            BookingController bookingController,
            UserController userController,
            AgentController agentController
    ) {
        super(parent, "Manage Customer", ModalityType.APPLICATION_MODAL);

        this.agentUser = agentUser;
        this.targetCustomer = targetCustomer;
        this.flightController = flightController;
        this.bookingController = bookingController;
        this.userController = userController;
        this.agentController = agentController;

        setSize(820, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        titleLabel = new JLabel(
                "<html><h2>Managing Customer: " + targetCustomer.getFirstName() + " " + targetCustomer.getLastName() +
                        " &nbsp;(ID: " + targetCustomer.getId() + ")</h2></html>"
        );
        add(titleLabel, BorderLayout.NORTH);

        loadReservations();

        JButton btnEdit = new JButton("Edit Customer Info");
        JButton btnBook = new JButton("Book Flight for Customer");
        JButton btnModify = new JButton("Modify Selected Reservation");
        JButton btnClose = new JButton("Close");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnEdit);
        bottom.add(btnBook);
        bottom.add(btnModify);
        bottom.add(btnClose);

        add(bottom, BorderLayout.SOUTH);

        btnEdit.addActionListener(e -> openEditDialog());
        btnBook.addActionListener(e -> openFlightSelector());
        btnModify.addActionListener(e -> openModifyDialog());
        btnClose.addActionListener(e -> dispose());
    }

    // ---------------------------------------------------------
    // Load reservations for this customer
    // ---------------------------------------------------------
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

    // ---------------------------------------------------------
    // Book a new flight for this customer
    // ---------------------------------------------------------
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

    // ---------------------------------------------------------
    // Modify existing reservation.
    // ---------------------------------------------------------
    private void openModifyDialog() {
        int row = reservationTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a reservation to modify.");
            return;
        }

        Reservation r =
                ((ReservationTableModel) reservationTable.getModel()).getReservationAt(row);

        new ModifyReservationDialog(
                this,
                r,
                agentUser,
                targetCustomer,
                flightController,
                bookingController
        ).setVisible(true);

        reload();
    }

    // ---------------------------------------------------------
    // Edit customer information
    // ---------------------------------------------------------
    private void openEditDialog() {
        AgentEditCustomerDialog editDialog = new AgentEditCustomerDialog(
                this,
                agentUser,
                targetCustomer,
                agentController
        );
        editDialog.setVisible(true);

        // Update targetCustomer if changes were saved
        Customer updated = editDialog.getUpdatedCustomer();
        if (updated != null) {
            targetCustomer = updated;
            // Update title to reflect changes
            titleLabel.setText(
                    "<html><h2>Managing Customer: " + targetCustomer.getFirstName() + " " + targetCustomer.getLastName() +
                            " &nbsp;(ID: " + targetCustomer.getId() + ")</h2></html>"
            );
        }
    }
}
