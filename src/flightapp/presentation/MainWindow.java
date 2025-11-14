package flightapp.presentation;

import flightapp.AppContext;
import flightapp.business.domain.UserRole;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    private final JButton loginButton = new JButton("Login / Profile");
    private final JButton logoutButton = new JButton("Logout");
    private final JButton searchButton = new JButton("Search Flights");
    private final JButton myReservationsButton = new JButton("My Reservations");
    private final JButton adminButton = new JButton("Admin - Flight Management");

    private final JLabel statusLabel = new JLabel("Not logged in");

    public MainWindow() {
        super("Flight Reservation System");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(loginButton);
        topPanel.add(logoutButton);
        topPanel.add(searchButton);
        topPanel.add(myReservationsButton);
        topPanel.add(adminButton);

        logoutButton.setEnabled(false);
        myReservationsButton.setEnabled(false);
        adminButton.setEnabled(false);

        loginButton.addActionListener(e -> openLoginDialog());
        logoutButton.addActionListener(e -> doLogout());
        searchButton.addActionListener(e -> new FlightSearchDialog(this));
        myReservationsButton.addActionListener(e -> new BookingDialog(this));
        adminButton.addActionListener(e -> new AdminFlightManagementDialog(this));

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.WEST);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(statusPanel, BorderLayout.SOUTH);

        refreshStatus();
        setVisible(true);
    }

    private void openLoginDialog() {
        new LoginDialog(this);
        refreshStatus();
    }

    private void doLogout() {
        AppContext.logout();
        refreshStatus();
    }

    private void refreshStatus() {
        if (AppContext.isLoggedIn()) {
            statusLabel.setText("Logged in as: " +
                    AppContext.getCurrentCustomer() +
                    " | Role: " + AppContext.getCurrentRole());
            logoutButton.setEnabled(true);
            myReservationsButton.setEnabled(true);
            adminButton.setEnabled(AppContext.getCurrentRole() == UserRole.ADMIN);
        } else {
            statusLabel.setText("Not logged in");
            logoutButton.setEnabled(false);
            myReservationsButton.setEnabled(false);
            adminButton.setEnabled(false);
        }
    }
}
