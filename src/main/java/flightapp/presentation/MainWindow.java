package flightapp.presentation;

import flightapp.business.controller.AuthController;
import flightapp.business.controller.BookingController;
import flightapp.business.controller.FlightController;

import flightapp.business.domain.Customer;
import flightapp.business.domain.Agent;
import flightapp.business.domain.User;
import flightapp.business.domain.Admin;

import flightapp.presentation.admin.AdminFlightManagementDialog;
import flightapp.presentation.agent.AgentMainDialog;
import flightapp.presentation.customer.CustomerFlightListDialog;
import flightapp.presentation.general.FlightSearchDialog;
import flightapp.presentation.general.LoginDialog;
import flightapp.presentation.general.SignUpDialog;
import flightapp.presentation.general.StartupDialog;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class MainWindow extends JFrame {

    private User currentUser;

    // ✅ Selected from StartupDialog
    private StartupDialog.RunMode runMode;

    // Controllers
    private final AuthController authController;
    private final BookingController bookingController;
    private final FlightController flightController;

    // UI
    private final JLabel lblCurrentUser = new JLabel();
    private JButton btnLogout;
    private JPanel center;

    public MainWindow() {
        super("FlightApp");

        this.flightController = new FlightController();
        this.authController = new AuthController();
        this.bookingController = new BookingController();

        // ✅ Custom first screen with:
        // [Customer] [Agent] [Admin]
        // [Sign Up]
        initStartupFlow();

        initUI();
        rebuildCenterPanel();
    }

    // ======================================================
    // ✅ STARTUP FLOW (CUSTOM UI, NOT JOptionPane)
    // ======================================================
    private void initStartupFlow() {

        StartupDialog startup = new StartupDialog(this);
        startup.setVisible(true);

        runMode = startup.getSelectedMode();

        if (runMode == null) {
            System.exit(0);
            return;
        }

        // ✅ AFTER role is chosen → FORCE LoginDialog
        doLogin();
    }

    // ======================================================
    // ✅ LOGIN VIA LoginDialog (ONLY WAY TO GET currentUser)
    // ======================================================
    private void doLogin() {

        LoginDialog dialog = new LoginDialog(this);
        String email = dialog.showDialog();

        if (email == null || email.isBlank()) {
            System.exit(0);
            return;
        }

        try {
            User user = authController.loginByEmail(email.trim());

            if (user == null) {
                JOptionPane.showMessageDialog(this, "Invalid login.");
                System.exit(0);
                return;
            }

            // ✅ HARD role enforcement
            if (runMode == StartupDialog.RunMode.CUSTOMER && !(user instanceof Customer) ||
                runMode == StartupDialog.RunMode.AGENT && !(user instanceof Agent) ||
                runMode == StartupDialog.RunMode.ADMIN && !(user instanceof Admin)) {

                JOptionPane.showMessageDialog(this, "Incorrect role selected.");
                System.exit(0);
                return;
            }

            currentUser = user;

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Login failed: " + ex.getMessage());
            System.exit(0);
        }
    }

    // ======================================================
    // ✅ MAIN UI
    // ======================================================
    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ================= TOP PANEL =================
        btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> doLogout());

        JPanel right = new JPanel();
        right.add(btnLogout);

        lblCurrentUser.setText("<html>Logged in as:<br>" + currentUser + "</html>");

        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        top.add(lblCurrentUser, BorderLayout.CENTER);
        top.add(right, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        // ================= CENTER PANEL =================
        center = new JPanel(new GridLayout(4, 1, 10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(center, BorderLayout.CENTER);
    }

    // ======================================================
    // ✅ LOGOUT → RESTART EVERYTHING
    // ======================================================
    private void doLogout() {
        dispose();
        new MainWindow().setVisible(true);
    }

    // ======================================================
    // ✅ ROLE-BASED UI (NO GREYED BUTTONS)
    // ======================================================
    private void rebuildCenterPanel() {

        center.removeAll();

        // ✅ Everyone can search
        JButton btnSearchFlights = new JButton("Search Flights");
        btnSearchFlights.addActionListener(e ->
                new FlightSearchDialog(this, flightController).setVisible(true)
        );
        center.add(btnSearchFlights);

        // ✅ CUSTOMER
        if (currentUser instanceof Customer customer) {
            JButton btnBook = new JButton("Book a Flight");
            btnBook.addActionListener(e ->
                    new CustomerFlightListDialog(
                            this,
                            flightController,
                            bookingController,
                            customer
                    ).setVisible(true)
            );
            center.add(btnBook);
        }

        // ✅ AGENT
        if (currentUser instanceof Agent agent) {
            JButton btnAgentPanel = new JButton("Agent Panel");
            btnAgentPanel.addActionListener(e ->
                    new AgentMainDialog(
                            this,
                            flightController,
                            bookingController,
                            agent
                    ).setVisible(true)
            );
            center.add(btnAgentPanel);
        }

        // ✅ ADMIN
        if (currentUser instanceof Admin admin) {
            JButton btnAdminPanel = new JButton("Admin: Manage Flights");
            btnAdminPanel.addActionListener(e ->
                    new AdminFlightManagementDialog(
                            this,
                            flightController,
                            admin
                    ).setVisible(true)
            );
            center.add(btnAdminPanel);
        }

        center.revalidate();
        center.repaint();
    }
}
