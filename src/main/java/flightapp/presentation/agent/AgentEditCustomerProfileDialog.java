package flightapp.presentation.agent;

import flightapp.business.controller.PaymentController;
import flightapp.business.controller.UserController;
import flightapp.business.domain.Customer;
import flightapp.business.domain.PaymentMethod;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class AgentEditCustomerProfileDialog extends JDialog {

    private final Customer customer;

    // ✅ Controllers only (NO DAOs in UI)
    private final UserController userController = new UserController();
    private final PaymentController paymentController = new PaymentController();

    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtPhone;
    private JComboBox<String> cmbPayment; // ✅ Only 2 options

    public AgentEditCustomerProfileDialog(Window parent, Customer customer) {
        super(parent, "Edit Customer Profile", ModalityType.APPLICATION_MODAL);
        this.customer = customer;

        setSize(420, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        initUI();
        loadExistingPaymentMethod(); // ✅ LOAD FROM DB
    }

    // ============================
    // UI SETUP
    // ============================
    private void initUI() {
        JPanel form = new JPanel(new GridLayout(4, 2, 12, 12));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        txtFirstName = new JTextField(customer.getFirstName());
        txtLastName = new JTextField(customer.getLastName());
        txtPhone = new JTextField(customer.getPhone() != null ? customer.getPhone() : "");

        // ✅ Only valid DB options
        cmbPayment = new JComboBox<>(new String[] { "Credit Card", "PayPal" });

        form.add(new JLabel("First Name:"));
        form.add(txtFirstName);

        form.add(new JLabel("Last Name:"));
        form.add(txtLastName);

        form.add(new JLabel("Phone:"));
        form.add(txtPhone);

        form.add(new JLabel("Payment Method:"));
        form.add(cmbPayment);

        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnSave);
        bottom.add(btnCancel);

        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> saveCustomer());
        btnCancel.addActionListener(e -> dispose());
    }

    // ============================
    // ✅ LOAD EXISTING PAYMENT FROM DB
    // ============================
    private void loadExistingPaymentMethod() {
        try {
            ArrayList<PaymentMethod> methods = paymentController.getPaymentMethods(customer);

            String existingType = methods.get(0).getStrType(); // use first available
            cmbPayment.setSelectedItem(existingType);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load payment method:\n" + ex.getMessage());
            cmbPayment.setSelectedIndex(0); // safe fallback
        }
    }

    // ============================
    // SAVE PROFILE + PAYMENT
    // ============================
    private void saveCustomer() {

        // ✅ Validation
        if (txtFirstName.getText().isBlank() || txtLastName.getText().isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "First name and last name are required.");
            return;
        }

        // ✅ Update Customer object
        customer.setFirstName(txtFirstName.getText().trim());
        customer.setLastName(txtLastName.getText().trim());
        customer.setPhone(txtPhone.getText().trim());

        String selectedPayment = (String) cmbPayment.getSelectedItem();

        try {
            // ✅ 1. Update USER using CONTROLLER
            boolean success = userController.AgentUpdateUser(customer);

            if (!success) {
                JOptionPane.showMessageDialog(this,
                        "Customer update failed. User no longer exists.");
                return;
            }

            // ✅ 2. Update PAYMENT using CONTROLLER (by UserID)
            paymentController.updatePaymentMethod(customer, selectedPayment);

            JOptionPane.showMessageDialog(this,
                    "Customer profile and payment method updated successfully.");

            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error while saving:\n" + ex.getMessage());
        }
    }
}
