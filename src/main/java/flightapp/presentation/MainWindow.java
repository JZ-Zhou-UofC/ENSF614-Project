package flightapp.presentation;

import flightapp.business.AppSession;
import flightapp.business.controller.AuthController;
import flightapp.business.controller.BookingController;
import flightapp.business.domain.User;
import flightapp.data.FlightDAO;
import flightapp.data.ReservationDAO;
import flightapp.data.UserDAO;
import flightapp.business.service.ReservationService;
import flightapp.business.service.FlightService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class MainWindow extends JFrame {

    private final AppSession session = new AppSession();
    private final AuthController authController;
    private final BookingController bookingController;

    // Shared DAOs
    private final FlightDAO flightDAO = new FlightDAO();

    // UI Components
    private final JLabel lblCurrentUser = new JLabel("Not logged in");
    private JButton btnSearchFlights;
    private JButton btnAdminFlights;

    public MainWindow() {
        super("FlightApp");

        // Data access and services
        UserDAO userDAO = new UserDAO();
        ReservationDAO reservationDAO = new ReservationDAO();
        ReservationService reservationService = new ReservationService(reservationDAO);

        this.authController = new AuthController(session, userDAO);
        this.bookingController = new BookingController(session, reservationService);

        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        //
        // --- TOP PANEL (Login + Current User)
        //
        JButton btnLogin = new JButton("Login...");
        btnLogin.addActionListener(e -> doLogin());

        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        top.add(lblCurrentUser, BorderLayout.CENTER);
        top.add(btnLogin, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        //
        // --- CENTER ACTION PANEL ---
        //
        JPanel center = new JPanel(new GridLayout(3, 1, 10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Search flights (Customer + Agent)
        btnSearchFlights = new JButton("Search Flights");
        btnSearchFlights.setEnabled(false); // enabled after login
        btnSearchFlights.addActionListener(e ->
                new FlightSearchDialog(this, flightDAO, bookingController).setVisible(true)
        );

        // Admin-only flight management
        btnAdminFlights = new JButton("Admin: Manage Flights");
        btnAdminFlights.setEnabled(false);
        btnAdminFlights.addActionListener(e ->
                new AdminFlightManagementDialog(
                        this,
                        session,
                        flightDAO,
                        new FlightService(flightDAO)
                ).setVisible(true)
        );

        center.add(btnSearchFlights);
        center.add(btnAdminFlights);

        add(center, BorderLayout.CENTER);
    }

    //
    // --- LOGIN HANDLING ---
    //
    private void doLogin() {
        LoginDialog dialog = new LoginDialog(this);
        String email = dialog.showDialog();
        if (email == null || email.isBlank()) return;

        try {
            User user = authController.loginByEmail(email.trim());
            if (user == null) {
                JOptionPane.showMessageDialog(this, "No user found for email: " + email);
            } else {
                lblCurrentUser.setText("Logged in as: " + user);
                updateUIBasedOnRole();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Login failed: " + ex.getMessage());
        }
    }

    //
    // --- ROLE-BASED UI ENABLE/DISABLE ---
    //
    private void updateUIBasedOnRole() {
        var user = session.getCurrentUser();

        if (user == null) {
            btnSearchFlights.setEnabled(false);
            btnAdminFlights.setEnabled(false);
            return;
        }

        if (user.isCustomer()) {
            btnSearchFlights.setEnabled(true);
            btnAdminFlights.setEnabled(false);
        }
        else if (user.isAgent()) {
            btnSearchFlights.setEnabled(true);
            btnAdminFlights.setEnabled(false);
        }
        else if (user.isAdmin()) {
            btnSearchFlights.setEnabled(false);
            btnAdminFlights.setEnabled(true);
        }
    }
}
