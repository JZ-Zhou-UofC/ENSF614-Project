package flightapp.presentation.agent;

import flightapp.business.controller.AgentController;
import flightapp.business.domain.Agent;
import flightapp.business.domain.Customer;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Dialog for agents to edit customer information.
 * Allows editing: first name, last name, email, phone, and subscription status.
 */
public class AgentEditCustomerDialog extends JDialog {

    private final Agent agentUser;
    private final Customer originalCustomer;
    private final AgentController agentController;
    
    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JCheckBox chkSubscribed;
    
    private Customer updatedCustomer;

    public AgentEditCustomerDialog(
            Window parent,
            Agent agentUser,
            Customer customer,
            AgentController agentController
    ) {
        super(parent, "Edit Customer Information", ModalityType.APPLICATION_MODAL);

        this.agentUser = agentUser;
        this.originalCustomer = customer;
        this.agentController = agentController;

        setSize(500, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        initUI();
    }

    private void initUI() {
        // Title
        JLabel title = new JLabel(
                "<html><h3>Editing Customer: " + originalCustomer.getFirstName() + " " + 
                originalCustomer.getLastName() + " (ID: " + originalCustomer.getId() + ")</h3></html>"
        );
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // First Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtFirstName = new JTextField(originalCustomer.getFirstName(), 20);
        formPanel.add(txtFirstName, gbc);

        // Last Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtLastName = new JTextField(originalCustomer.getLastName(), 20);
        formPanel.add(txtLastName, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtEmail = new JTextField(originalCustomer.getEmail(), 20);
        formPanel.add(txtEmail, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtPhone = new JTextField(
                originalCustomer.getPhone() != null ? originalCustomer.getPhone() : "", 
                20
        );
        formPanel.add(txtPhone, gbc);

        // Subscribed
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Subscribed:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        chkSubscribed = new JCheckBox("Receive promotional emails");
        chkSubscribed.setSelected(originalCustomer.isSubscribed());
        formPanel.add(chkSubscribed, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton btnSave = new JButton("Save Changes");
        JButton btnCancel = new JButton("Cancel");
        
        btnSave.addActionListener(e -> saveChanges());
        btnCancel.addActionListener(e -> dispose());
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveChanges() {
        // Validate input
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        boolean subscribed = chkSubscribed.isSelected();

        // Validation
        if (firstName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "First name cannot be empty.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Last name cannot be empty.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (email.isEmpty() || !email.contains("@")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create updated customer object
        Customer updated = new Customer(
                originalCustomer.getId(),
                firstName,
                lastName,
                email,
                phone.isEmpty() ? null : phone,
                subscribed
        );

        try {
            // Update via controller
            updatedCustomer = agentController.updateCustomer(agentUser, updated);

            JOptionPane.showMessageDialog(this,
                    "Customer information updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error updating customer:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Returns the updated customer object if changes were saved, null otherwise.
     */
    public Customer getUpdatedCustomer() {
        return updatedCustomer;
    }
}

