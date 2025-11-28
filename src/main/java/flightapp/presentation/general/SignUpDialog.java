package flightapp.presentation.general;

import flightapp.business.controller.AuthController;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class SignUpDialog extends JDialog {

    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtEmail;
    private JTextField txtRole;   // ✅ SHOWN but NOT editable
    private JCheckBox chkSubscribe;

    private boolean success = false;

    private final AuthController authController; // ✅ Injected

    // ✅ UPDATED CONSTRUCTOR (AuthController injected)
    public SignUpDialog(JFrame parent, AuthController authController) {
        super(parent, "Sign Up", true);
        this.authController = authController;
        initUI();
    }

    private void initUI() {
        setSize(350, 350);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // ---------------- First Name ----------------
        form.add(new JLabel("First Name:"));
        txtFirstName = new JTextField();
        form.add(txtFirstName);

        // ---------------- Last Name ----------------
        form.add(new JLabel("Last Name:"));
        txtLastName = new JTextField();
        form.add(txtLastName);

        // ---------------- Email ----------------
        form.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        form.add(txtEmail);

        // ---------------- Role (LOCKED) ----------------
        form.add(new JLabel("Role:"));
        txtRole = new JTextField("Customer");
        txtRole.setEditable(false);                 // ✅ cannot change
        txtRole.setBackground(Color.LIGHT_GRAY);
        form.add(txtRole);

        // ---------------- Subscribe to Monthly Promotions ----------------
        form.add(new JLabel("Subscribe:"));
        chkSubscribe = new JCheckBox("Monthly Promotions");
        form.add(chkSubscribe);

        // ---------------- Buttons ----------------
        JButton btnSignUp = new JButton("Create Account");
        JButton btnCancel = new JButton("Cancel");

        // ✅ REAL SIGN-UP WITH DATABASE INSERT
        btnSignUp.addActionListener(e -> doRealSignUp());

        btnCancel.addActionListener(e -> dispose());

        JPanel bottom = new JPanel();
        bottom.add(btnSignUp);
        bottom.add(btnCancel);

        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // ✅ UX polish (optional but nice)
        getRootPane().setDefaultButton(btnSignUp);
        SwingUtilities.invokeLater(() -> txtFirstName.requestFocusInWindow());
    }

    // ======================================================
    // ✅ REAL DATABASE SIGN-UP LOGIC
    // ======================================================
    private void doRealSignUp() {

        String firstName = txtFirstName.getText().trim();
        String lastName  = txtLastName.getText().trim();
        String email     = txtEmail.getText().trim();

        // ---------------- Validation ----------------
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Invalid email format.");
            return;
        }

        // ---------------- REAL DB INSERT ---------------- 
        try {
            boolean subscribed = chkSubscribe.isSelected();
            authController.register(firstName, lastName, email, subscribed);

            JOptionPane.showMessageDialog(
                    this,
                    "Account created successfully!\nPlease log in as Customer."
            );

            success = true;
            dispose();

        } catch (SQLException ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Sign up failed: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ---------------- Getters ----------------
    public boolean isSuccess() {
        return success;
    }

    public String getFirstName() {
        return txtFirstName.getText().trim();
    }

    public String getLastName() {
        return txtLastName.getText().trim();
    }

    public String getEmail() {
        return txtEmail.getText().trim();
    }

    // ✅ Always returns "Customer"
    public String getRole() {
        return "Customer";
    }
}
