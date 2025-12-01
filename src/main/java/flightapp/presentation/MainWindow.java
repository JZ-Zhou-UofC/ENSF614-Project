package flightapp.presentation;

import flightapp.business.controller.UserController;
import flightapp.business.controller.BookingController;
import flightapp.business.controller.FlightController;
import flightapp.business.controller.PromotionController;

import flightapp.business.domain.Customer;
import flightapp.business.domain.Agent;
import flightapp.business.domain.User;
import flightapp.business.domain.Admin;

import flightapp.presentation.admin.AdminFlightManagementDialog;
import flightapp.presentation.agent.AgentMainDialog;
import flightapp.presentation.customer.CustomerFlightListDialog;
import flightapp.presentation.general.FlightSearchDialog;
import flightapp.presentation.general.LoginDialog;

import flightapp.presentation.general.StartupDialog;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class MainWindow extends JFrame {

    private User currentUser;

    private StartupDialog.RunMode runMode;

    private final UserController userController;
    private final BookingController bookingController;
    private final FlightController flightController;
    private final PromotionController promotionController;

    private JButton btnLogout;
    private JPanel center;

    public MainWindow() {
        super("FlightApp");

        this.flightController = new FlightController();
        this.userController = new UserController();
        this.bookingController = new BookingController();
        this.promotionController = new PromotionController();


        initStartupFlow();

        initUI();
        rebuildCenterPanel();
    }


    private void initStartupFlow() {

        StartupDialog startup = new StartupDialog(this, userController);
        startup.setVisible(true);

        runMode = startup.getSelectedMode();

        if (runMode == null) {
            System.exit(0);
            return;
        }

        doLogin();
    }


    private void doLogin() {

        LoginDialog dialog = new LoginDialog(this);
        String email = dialog.showDialog();

        if (email == null || email.isBlank()) {
            System.exit(0);
            return;
        }

        try {
            User user = userController.loginByEmail(email.trim());

            if (user == null) {
                JOptionPane.showMessageDialog(this, "Invalid login.");
                System.exit(0);
                return;
            }


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


    private JPanel buildHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        panel.setBackground(new Color(245, 245, 245));

        JLabel lblTitle = new JLabel("FlightApp");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setOpaque(false);

        User u = currentUser;
        String role = u.getClass().getSimpleName();

        JLabel name = new JLabel(u.getFirstName() + " " + u.getLastName());
        name.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel email = new JLabel(u.getEmail());
        email.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel lblRole = new JLabel("Role: " + role);
        lblRole.setFont(new Font("Arial", Font.PLAIN, 14));

        userPanel.add(name);
        userPanel.add(email);
        userPanel.add(lblRole);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(btnLogout);

        panel.add(lblTitle, BorderLayout.WEST);
        panel.add(userPanel, BorderLayout.CENTER);
        panel.add(right, BorderLayout.EAST);

        return panel;
    }


    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> doLogout());

        JPanel right = new JPanel();
        right.add(btnLogout);

        add(buildHeaderPanel(), BorderLayout.NORTH);

        center = new JPanel(new GridLayout(4, 1, 10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(center, BorderLayout.CENTER);
    }


    private void doLogout() {
        dispose();
        new MainWindow().setVisible(true);
    }


    private void rebuildCenterPanel() {

        center.removeAll();


        JButton btnSearchFlights = new JButton("Search Flights");
            btnSearchFlights.addActionListener(e ->
            new FlightSearchDialog(this, flightController,bookingController,currentUser).setVisible(true)
        );
        center.add(btnSearchFlights);


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


        if (currentUser instanceof Agent agent) {
            JButton btnAgentPanel = new JButton("Agent Panel");
            btnAgentPanel.addActionListener(e ->
                    new AgentMainDialog(
                            this,
                            flightController,
                            bookingController,
                            promotionController,
                            agent
                    ).setVisible(true)
            );
            center.add(btnAgentPanel);
        }

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
