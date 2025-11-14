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

    private final FlightDAO flightDAO = new FlightDAO();

    private final JLabel lblCurrentUser = new JLabel("<html>Not logged in</html>");

    private JButton btnSearchFlights;
    private JButton btnAdminFlights;
    private JButton btnCustomerBook;   // ⭐ NEW BUTTON FOR CUSTOMER BOOKING ⭐

    public MainWindow() {
        super("FlightApp");

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
        setLayout(new BorderLayout());

        //
        // --- TOP PANEL ---
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
        // --- CENTER PANEL ---
        //
        JPanel center = new JPanel(new GridLayout(4, 1, 10, 10)); // 🔥 now 4 rows
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ✔ Everyone can search flights
        btnSearchFlights = new JButton("Search Flights");
        btnSearchFlights.setEnabled(true);
        btnSearchFlights.addActionListener(e ->
                new FlightSearchDialog(this, flightDAO).setVisible(true)
        );

        // ⭐ Customer booking button (initially disabled)
        btnCustomerBook = new JButton("Book a Flight");
        btnCustomerBook.setEnabled(false);
        btnCustomerBook.addActionListener(e ->
                new CustomerFlightListDialog(this, flightDAO, bookingController).setVisible(true)
        );

        // ✔ Admin-only panel
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

        // Add to layout
        center.add(btnSearchFlights);
        center.add(btnCustomerBook); // ⭐ ADDED HERE
        center.add(btnAdminFlights);

        add(center, BorderLayout.CENTER);
    }

    //
    // --- LOGIN LOGIC ---
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
                lblCurrentUser.setText("<html>Logged in as:<br>" + user + "</html>");
                updateUIBasedOnRole();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Login failed: " + ex.getMessage());
        }
    }

    //
    // --- ROLE-BASED UI LOGIC ---
    //
    private void updateUIBasedOnRole() {
        User user = session.getCurrentUser();

        // ✔ Everyone can search flights
        btnSearchFlights.setEnabled(true);

        if (user == null) {
            btnAdminFlights.setEnabled(false);
            btnCustomerBook.setEnabled(false);
            return;
        }

        // ⭐ Customer can book flights
        btnCustomerBook.setEnabled(user.isCustomer());

        // ⭐ Admin can manage flights
        btnAdminFlights.setEnabled(user.isAdmin());
    }
}
