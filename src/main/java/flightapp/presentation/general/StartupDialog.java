package flightapp.presentation.general;

import flightapp.business.controller.UserController;

import javax.swing.*;
import java.awt.*;

public class StartupDialog extends JDialog {

    public enum RunMode {
        CUSTOMER, AGENT, ADMIN
    }

    private RunMode selectedMode = null;

    private final UserController userController;

    public StartupDialog(JFrame parent, UserController userController) {
        super(parent, "Welcome to FlightApp", true);
        this.userController = userController;
        initUI();
    }

    private void initUI() {
        setSize(500, 200);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Run the program as:", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

   

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



        JPanel signupRow = new JPanel();
        JButton btnSignUp = new JButton("Sign Up");

        btnSignUp.addActionListener(e -> {
            new SignUpDialog((JFrame) getParent(), userController).setVisible(true);
        });

        signupRow.add(btnSignUp);



        JPanel center = new JPanel(new GridLayout(2, 1, 10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        center.add(roleRow);
        center.add(signupRow);

        add(center, BorderLayout.CENTER);
    }

    private void select(RunMode mode) {
        this.selectedMode = mode;
        dispose();
    }

    public RunMode getSelectedMode() {
        return selectedMode;
    }
}
