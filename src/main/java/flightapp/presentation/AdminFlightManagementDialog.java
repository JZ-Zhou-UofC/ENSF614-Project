package flightapp.presentation;

import flightapp.business.AppSession;
import flightapp.business.domain.Admin;
import flightapp.business.domain.Flight;
import flightapp.business.service.FlightService;
import flightapp.data.FlightDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AdminFlightManagementDialog extends JDialog {

    private final AppSession session;
    private final FlightDAO flightDAO;
    private final FlightService flightService;

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Origin", "Destination", "Departure", "Arrival", "Price", "Seats"}, 0
    );
    private final JTable table = new JTable(model);

    public AdminFlightManagementDialog(Frame owner,
                                       AppSession session,
                                       FlightDAO flightDAO,
                                       FlightService flightService) {
        super(owner, "Admin Flight Management", true);
        this.session = session;
        this.flightDAO = flightDAO;
        this.flightService = flightService;

        if (!(session.getCurrentUser() instanceof Admin)) {
            JOptionPane.showMessageDialog(owner,
                    "Only Admin users can manage flights.",
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

        btnRefresh.addActionListener(e -> loadFlights());
        btnSave.addActionListener(e -> saveSelectedRow());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnRefresh);
        south.add(btnSave);

        JPanel main = new JPanel(new BorderLayout(5, 5));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        main.add(scroll, BorderLayout.CENTER);
        main.add(south, BorderLayout.SOUTH);

        setContentPane(main);
        setSize(900, 400);
        setLocationRelativeTo(getOwner());
    }

    private void loadFlights() {
        model.setRowCount(0);
        try {
            List<Flight> flights = flightDAO.findAll();
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
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to load flights: " + ex.getMessage(),
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
            int id = Integer.parseInt(model.getValueAt(row, 0).toString());
            String origin = model.getValueAt(row, 1).toString();
            String dest = model.getValueAt(row, 2).toString();
            String depStr = model.getValueAt(row, 3).toString().trim();
            String arrStr = model.getValueAt(row, 4).toString().trim();
            double price = Double.parseDouble(model.getValueAt(row, 5).toString());
            int seats = Integer.parseInt(model.getValueAt(row, 6).toString());

            Flight f = flightDAO.findById(id);
            if (f == null) {
                JOptionPane.showMessageDialog(this,
                        "Flight not found in DB (id=" + id + ").",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            f.setOrigin(origin);
            f.setDestination(dest);
            f.setPrice(price);
            f.setSeatsAvailable(seats);

            if (!depStr.isEmpty()) {
                f.setDepartureTime(LocalDateTime.parse(depStr));
            } else {
                f.setDepartureTime(null);
            }

            if (!arrStr.isEmpty()) {
                f.setArrivalTime(LocalDateTime.parse(arrStr));
            } else {
                f.setArrivalTime(null);
            }

            Admin admin = (Admin) session.getCurrentUser();
            flightService.updateScheduleAsAdmin(admin, f);

            JOptionPane.showMessageDialog(this,
                    "Flight updated successfully.",
                    "Saved",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException | DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid value: " + ex.getMessage(),
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
