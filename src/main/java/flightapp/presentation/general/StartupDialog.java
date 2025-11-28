package flightapp.presentation.general;

import flightapp.business.controller.AuthController;

import javax.swing.*;
import java.awt.*;

public class StartupDialog extends JDialog {

    public enum RunMode {
        CUSTOMER, AGENT, ADMIN
    }

    private RunMode selectedMode = null;

    private final AuthController authController;

    public StartupDialog(JFrame parent, AuthController authController) {
        super(parent, "Welcome to FlightApp", true);
        this.authController = authController;
        initUI();
    }

    private void initUI() {
        setSize(500, 200);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Run the program as:", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        // =============================
        // ✅ FIRST ROW — ROLE BUTTONS
        // =============================
        JPanel roleRow = new JPanel(new GridLayout(1, 3, 15, 10));

        JButton btnCustomer = new JButton("Customer / Guest");
        JButton btnAgent = new JButton("Agent");
        JButton btnAdmin = new JButton("Admin");

        btnCustomer.addActionListener(e -> select(RunMode.CUSTOMER));
        btnAgent.addActionListener(e -> select(RunMode.AGENT));
        btnAdmin.addActionListener(e -> select(RunMode.ADMIN));

        roleRow.add(btnCustomer);
        roleRow.add(btnAgent);
        roleRow.add(btnAdmin);

        // =============================
        // ✅ SECOND ROW — SIGN UP
        // =============================
        JPanel signupRow = new JPanel();
        JButton btnSignUp = new JButton("Sign Up");

        btnSignUp.addActionListener(e -> {
            new SignUpDialog((JFrame) getParent(), authController).setVisible(true);
        });

        signupRow.add(btnSignUp);

        // =============================
        // Layout wrapper
        // =============================
        JPanel center = new JPanel(new GridLayout(2, 1, 10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        center.add(roleRow);
        center.add(signupRow);

        add(center, BorderLayout.CENTER);
    }

    private void select(RunMode mode) {
        this.selectedMode = mode;
        dispose(); // ✅ close startup dialog
    }

    public RunMode getSelectedMode() {
        return selectedMode;
    }
}
