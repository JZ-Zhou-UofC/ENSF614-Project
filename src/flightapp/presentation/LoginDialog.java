package flightapp.presentation;

import flightapp.AppContext;
import flightapp.business.controllers.AuthenticationController;
import flightapp.business.domain.Customer;
import flightapp.business.domain.UserRole;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {

    private final AuthenticationController authController = new AuthenticationController();

    public LoginDialog(JFrame parent) {
        super(parent, "Login / Register", true);

        JTextField nameField  = new JTextField(15);
        JTextField emailField = new JTextField(15);
        JTextField phoneField = new JTextField(15);

        JButton submitButton = new JButton("Login / Register");

        submitButton.addActionListener(e -> {
            String name  = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email is required.");
                return;
            }

            if (name.isEmpty()) {
                name = email; // fallback
            }

            Customer c = authController.loginOrRegister(name, email, phone);
            UserRole role = authController.inferRoleFromEmail(email);

            AppContext.setCurrentCustomer(c);
            AppContext.setCurrentRole(role);

            JOptionPane.showMessageDialog(this,
                    "Logged in as " + c.getName() + " with role " + role);

            dispose();
        });

        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Phone:"));
        form.add(phoneField);
        form.add(new JLabel());
        form.add(submitButton);

        getContentPane().add(form, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}
