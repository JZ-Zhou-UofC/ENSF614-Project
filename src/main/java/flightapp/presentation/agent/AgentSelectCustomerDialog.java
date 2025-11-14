package flightapp.presentation.agent;

import flightapp.business.AppSession;
import flightapp.business.controller.BookingController;
import flightapp.business.domain.Customer;
import flightapp.data.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AgentSelectCustomerDialog extends JDialog {

    private final AppSession session;
    private final BookingController bookingController;
    private final UserDAO userDAO = new UserDAO();

    private JTable table;

    public AgentSelectCustomerDialog(Window parent,
                                     AppSession session,
                                     BookingController bookingController) {
        super(parent, "Select Customer", ModalityType.APPLICATION_MODAL);

        this.session = session;
        this.bookingController = bookingController;

        setSize(500, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        loadCustomers();

        JButton btnSelect = new JButton("Manage Selected Customer");
        JButton btnClose = new JButton("Close");

        JPanel bottom = new JPanel();
        bottom.add(btnSelect);
        bottom.add(btnClose);

        btnSelect.addActionListener(e -> selectCustomer());
        btnClose.addActionListener(e -> dispose());

        add(bottom, BorderLayout.SOUTH);
    }

    private void loadCustomers() {
        try {
            List<Customer> list = userDAO.findAllCustomers();
            table = new JTable(new AgentCustomerTableModel(list));
            add(new JScrollPane(table), BorderLayout.CENTER);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading customers: " + ex.getMessage());
        }
    }

    private void selectCustomer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a customer.");
            return;
        }

        Customer customer = ((AgentCustomerTableModel) table.getModel()).getCustomerAt(row);
        session.setActiveCustomer(customer);

        new AgentManageCustomerDialog(this, session, bookingController).setVisible(true);
        dispose();
    }
}
