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

        loadCustomers();

        JButton btnSelect = new JButton("Manage Customer");
        JButton btnClose = new JButton("Close");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnSelect);
        bottom.add(btnClose);

        add(bottom, BorderLayout.SOUTH);

        btnSelect.addActionListener(e -> selectCustomer());
        btnClose.addActionListener(e -> dispose());
    }

    private void loadCustomers() {
        try {
            List<Customer> list = userDAO.findAllCustomers();
            table = new JTable(new AgentCustomerTableModel(list));
            add(new JScrollPane(table), BorderLayout.CENTER);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading customers:\n" + ex.getMessage());
        }
    }

    private void selectCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer.");
            return;
        }

        Customer customer = ((AgentCustomerTableModel) table.getModel()).getCustomerAt(row);

        new AgentManageCustomerDialog(
                this,
                agentUser,
                customer,
                flightController,
                bookingController
        ).setVisible(true);

        dispose();
    }
}
