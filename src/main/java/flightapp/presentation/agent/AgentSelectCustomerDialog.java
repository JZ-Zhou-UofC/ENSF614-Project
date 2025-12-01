package flightapp.presentation.agent;

import flightapp.business.controller.BookingController;
import flightapp.business.controller.FlightController;
import flightapp.business.domain.Agent;
import flightapp.business.domain.Customer;
import flightapp.data.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AgentSelectCustomerDialog extends JDialog {

    private final Agent agentUser;
    private final FlightController flightController;
    private final BookingController bookingController;
    private final UserDAO userDAO = new UserDAO();

    private JTable table;

    public AgentSelectCustomerDialog(
            Window parent,
            Agent agentUser,
            FlightController flightController,
            BookingController bookingController
    ) {
        super(parent, "Select Customer", ModalityType.APPLICATION_MODAL);

        this.agentUser = agentUser;
        this.flightController = flightController;
        this.bookingController = bookingController;

        setSize(520, 420);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // ✅ Load customers into table
        loadCustomers();

        // ✅ Buttons
        JButton btnManageFlights = new JButton("Manage User Flights");
        JButton btnManageProfile = new JButton("Manage User Profile");
        JButton btnRefresh = new JButton("Refresh");   // ✅ NEW
        JButton btnClose = new JButton("Close");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnManageFlights);
        bottom.add(btnManageProfile);
        bottom.add(btnRefresh);       // ✅ NEW
        bottom.add(btnClose);

        add(bottom, BorderLayout.SOUTH);

        // ✅ Button actions
        btnManageFlights.addActionListener(e -> manageCustomerFlights());
        btnManageProfile.addActionListener(e -> manageCustomerProfile());
        btnRefresh.addActionListener(e -> reloadCustomers());     // ✅ NEW
        btnClose.addActionListener(e -> dispose());
    }

    // ============================
    // LOAD ALL CUSTOMERS (INITIAL)
    // ============================
    private void loadCustomers() {
        try {
            List<Customer> list = userDAO.findAllCustomers();
            table = new JTable(new AgentCustomerTableModel(list));
            add(new JScrollPane(table), BorderLayout.CENTER);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading customers:\n" + ex.getMessage());
        }
    }

    // ============================
    // ✅ MANUAL REFRESH ONLY
    // ============================
    private void reloadCustomers() {
        try {
            List<Customer> list = userDAO.findAllCustomers();
            table.setModel(new AgentCustomerTableModel(list));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error refreshing customers:\n" + ex.getMessage());
        }
    }

    // ============================
    // MANAGE CUSTOMER FLIGHTS
    // ============================
    private void manageCustomerFlights() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer.");
            return;
        }

        Customer customer =
                ((AgentCustomerTableModel) table.getModel()).getCustomerAt(row);

        new AgentManageCustomerDialog(
                this,
                agentUser,
                customer,
                flightController,
                bookingController
        ).setVisible(true);

        dispose();
    }

    // ============================
    // MANAGE CUSTOMER PROFILE
    // ============================
    private void manageCustomerProfile() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer.");
            return;
        }

        Customer customer =
                ((AgentCustomerTableModel) table.getModel()).getCustomerAt(row);

        new AgentEditCustomerProfileDialog(this, customer).setVisible(true);
    }
}
