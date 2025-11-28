package flightapp.presentation.general;

import javax.swing.*;
import java.awt.*;

public class SignUpDialog extends JDialog {

    private JTextField txtName;
    private JTextField txtEmail;
    private JTextField txtRole;   // ✅ SHOWN but NOT editable
    private boolean success = false;

    public SignUpDialog(JFrame parent) {
        super(parent, "Sign Up", true);
        initUI();
    }

    private void initUI() {
        setSize(350, 250);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // ---------------- Name ----------------
        form.add(new JLabel("Name:"));
        txtName = new JTextField();
        form.add(txtName);

        // ---------------- Email ----------------
        form.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        form.add(txtEmail);

        // ---------------- Role (LOCKED) ----------------
        form.add(new JLabel("Role:"));
        txtRole = new JTextField("Customer");
        txtRole.setEditable(false);          // ✅ cannot change
        txtRole.setBackground(Color.LIGHT_GRAY);
        form.add(txtRole);

        // ---------------- Buttons ----------------
        JButton btnSignUp = new JButton("Create Account");
        JButton btnCancel = new JButton("Cancel");

        btnSignUp.addActionListener(e -> {
            if (txtName.getText().isBlank() || txtEmail.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "All fields are required.");
                return;
            }

            success = true;
            dispose();
        });

        btnCancel.addActionListener(e -> dispose());

        JPanel bottom = new JPanel();
        bottom.add(btnSignUp);
        bottom.add(btnCancel);

        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    // ---------------- Getters ----------------
    public boolean isSuccess() {
        return success;
    }

    public String getNameValue() {
        return txtName.getText().trim();
    }

    public String getEmail() {
        return txtEmail.getText().trim();
    }

    // ✅ Always returns "Customer"
    public String getRole() {
        return "Customer";
    }
}
