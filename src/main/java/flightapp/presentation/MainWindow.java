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

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class MainWindow extends JFrame {

    private User currentUser; // ⭐ Logged-in session user

    // ⭐ Controllers — each controller internally creates its own DAOs
    private final AuthController authController;
    private final BookingController bookingController;
    private final FlightController flightController;

    private final JLabel lblCurrentUser = new JLabel("<html>Not logged in</html>");

    private JButton btnSearchFlights;
    private JButton btnCustomerBook;
    private JButton btnAgentPanel;
    private JButton btnAdminFlights;

    public MainWindow() {
        super("FlightApp");

        // ⭐ Clean architecture: UI owns controllers, controllers own services/DAOs
        this.flightController = new FlightController();
        this.authController = new AuthController();
        this.bookingController = new BookingController();

        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //
        // ----- TOP PANEL -----
        //
        JButton btnLogin = new JButton("Login...");
        btnLogin.addActionListener(e -> doLogin());

        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lblCurrentUser.setVerticalAlignment(SwingConstants.TOP);

        top.add(lblCurrentUser, BorderLayout.CENTER);
        top.add(btnLogin, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);


        //
        // ----- CENTER PANEL -----
        //
        JPanel center = new JPanel(new GridLayout(4, 1, 10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Everyone can search flights
        btnSearchFlights = new JButton("Search Flights");
        btnSearchFlights.addActionListener(e ->
            new FlightSearchDialog(this, flightController).setVisible(true)
        );

        //
        // CUSTOMER BOOKING
        //
        btnCustomerBook = new JButton("Book a Flight");
        btnCustomerBook.setEnabled(false);
        btnCustomerBook.addActionListener(e -> {
            if (currentUser instanceof Customer customer) {
                new CustomerFlightListDialog(
                        this,
                        flightController,
                        bookingController,
                        customer
                ).setVisible(true);
            }
        });

        //
        // AGENT PANEL
        //
        btnAgentPanel = new JButton("Agent Panel");
        btnAgentPanel.setEnabled(false);
        btnAgentPanel.addActionListener(e -> {
            if (currentUser instanceof Agent agent) {
                new AgentMainDialog(
                        this,
                        flightController,
                        bookingController,
                        agent
                ).setVisible(true);
            }
        });

        //
        // ADMIN PANEL
        //
        btnAdminFlights = new JButton("Admin: Manage Flights");
        btnAdminFlights.setEnabled(false);
        btnAdminFlights.addActionListener(e ->
           {   if (currentUser instanceof Admin admin) {new AdminFlightManagementDialog(
                    this,
                    flightController,
                    admin
            ).setVisible(true);}} 
        );

        center.add(btnSearchFlights);
        center.add(btnCustomerBook);
        center.add(btnAgentPanel);
        center.add(btnAdminFlights);

        add(center, BorderLayout.CENTER);
    }

    //
    // ===== LOGIN LOGIC =====
    //
    private void doLogin() {
        LoginDialog dialog = new LoginDialog(this);
        String email = dialog.showDialog();

        if (email == null || email.isBlank())
            return;

        try {
            User user = authController.loginByEmail(email.trim());

            if (user == null) {
                JOptionPane.showMessageDialog(this, "No user found for: " + email);
                return;
            }

            this.currentUser = user;

            lblCurrentUser.setText("<html>Logged in as:<br>" + user + "</html>");

            updateUIByRole();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Login failed: " + ex.getMessage());
        }
    }

    //
    // ===== ROLE-BASED UI =====
    //
    private void updateUIByRole() {

        btnSearchFlights.setEnabled(true);

        if (currentUser == null) {
            btnCustomerBook.setEnabled(false);
            btnAgentPanel.setEnabled(false);
            btnAdminFlights.setEnabled(false);
            return;
        }

        btnCustomerBook.setEnabled(currentUser.isCustomer());
        btnAgentPanel.setEnabled(currentUser.isAgent());
        btnAdminFlights.setEnabled(currentUser.isAdmin());
    }
}
