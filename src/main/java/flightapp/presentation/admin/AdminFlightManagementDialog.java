package flightapp.presentation.admin;

import flightapp.business.controller.FlightController;
import flightapp.business.domain.Admin;
import flightapp.business.domain.Flight;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AdminFlightManagementDialog extends JDialog {

    private final Admin adminUser;
    private final FlightController flightController;

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Origin", "Destination", "Departure", "Arrival", "Price", "Seats"}, 0
    );
    private final JTable table = new JTable(model);

    public AdminFlightManagementDialog(
            Window parent,
                  FlightController flightController,
            Admin adminUser
      
    ) {
        super(parent, "Admin Flight Management", ModalityType.APPLICATION_MODAL);

        this.adminUser = adminUser;
        this.flightController = flightController;

        if (adminUser == null) {
            JOptionPane.showMessageDialog(parent,
                    "Invalid admin user.",
                    "Access Denied",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        initUI();
        loadFlights();
    }

    private void initUI() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(table);

        JButton btnRefresh = new JButton("Refresh");
        JButton btnSave = new JButton("Save Changes");
        JButton btnCreate = new JButton("Create New Flight");
        JButton btnDelete = new JButton("Delete Flight");



        btnRefresh.addActionListener(e -> loadFlights());
        btnSave.addActionListener(e -> saveSelectedRow());
        btnCreate.addActionListener(e -> openCreateFlightDialog());
        btnDelete.addActionListener(e -> deleteSelectedFlight());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnRefresh);
        south.add(btnSave);
        south.add(btnCreate);
        south.add(btnDelete);

        JPanel main = new JPanel(new BorderLayout(5, 5));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        main.add(scroll, BorderLayout.CENTER);
        main.add(south, BorderLayout.SOUTH);


        setContentPane(main);
        setSize(900, 400);
        setLocationRelativeTo(getOwner());
    }

    private void openCreateFlightDialog() {
    new CreateFlightDialog(
            this,
            adminUser,
            flightController
    ).setVisible(true);

    loadFlights(); // Refresh after creation
}

    private void deleteSelectedFlight() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a flight row to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete flight ID " + id + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            flightController.deleteFlight(adminUser, id);

            JOptionPane.showMessageDialog(this,
                    "Flight deleted successfully.",
                    "Deleted",
                    JOptionPane.INFORMATION_MESSAGE);

            loadFlights(); // Refresh table

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to delete flight:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private void loadFlights() {
        model.setRowCount(0);

        try {
            List<Flight> flights = flightController.getAllFlights();

            for (Flight f : flights) {
                model.addRow(new Object[]{
                        f.getId(),
                        f.getOrigin(),
                        f.getDestination(),
                        f.getDepartureTime() != null ? f.getDepartureTime().toString() : "",
                        f.getArrivalTime() != null ? f.getArrivalTime().toString() : "",
                        f.getPrice(),
                        f.getSeatsAvailable()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load flights:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveSelectedRow() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a flight row to save.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Read values from UI
            int id = Integer.parseInt(model.getValueAt(row, 0).toString());
            String origin = model.getValueAt(row, 1).toString();
            String destination = model.getValueAt(row, 2).toString();
            String depStr = model.getValueAt(row, 3).toString().trim();
            String arrStr = model.getValueAt(row, 4).toString().trim();
            double price = Double.parseDouble(model.getValueAt(row, 5).toString());
            int seats = Integer.parseInt(model.getValueAt(row, 6).toString());

            // Load existing flight
            Flight flight = flightController.findById(id);
            if (flight == null) {
                JOptionPane.showMessageDialog(this,
                        "Flight not found in DB (ID=" + id + ")",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Apply changes
            flight.setOrigin(origin);
            flight.setDestination(destination);
            flight.setPrice(price);
            flight.setSeatsAvailable(seats);

            if (!depStr.isEmpty()) {
                flight.setDepartureTime(LocalDateTime.parse(depStr));
            } else {
                flight.setDepartureTime(null);
            }

            if (!arrStr.isEmpty()) {
                flight.setArrivalTime(LocalDateTime.parse(arrStr));
            } else {
                flight.setArrivalTime(null);
            }

            // Admin metadata
            flight.setLastModifiedAt(LocalDateTime.now());
            flight.setLastModifiedByUserId(adminUser.getId());

            // Save using controller
            flightController.updateFlightAsAdmin(adminUser, flight);

            JOptionPane.showMessageDialog(this,
                    "Flight updated successfully.",
                    "Saved",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException | DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid value: " + ex.getMessage(),
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
