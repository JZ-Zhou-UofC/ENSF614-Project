package flightapp.presentation.admin;

import flightapp.business.controller.FlightController;
import flightapp.business.domain.Admin;
import flightapp.business.domain.Airplane;
import flightapp.business.domain.Flight;
import flightapp.data.AirplaneDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class CreateFlightDialog extends JDialog {

    private final Admin adminUser;
    private final FlightController flightController;
    private final AirplaneDAO airplaneDAO = new AirplaneDAO();

    private JComboBox<Airplane> airplaneCombo;
    private JTextField txtOrigin;
    private JTextField txtDestination;
    private JTextField txtDeparture;
    private JTextField txtArrival;
    private JTextField txtPrice;

    public CreateFlightDialog(
            Window parent,
            Admin adminUser,
            FlightController flightController
    ) {
        super(parent, "Create New Flight", ModalityType.APPLICATION_MODAL);

        this.adminUser = adminUser;
        this.flightController = flightController;

        initUI();
        loadAirplanes();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setSize(500, 350);
        setLocationRelativeTo(getOwner());

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));

        airplaneCombo = new JComboBox<>();
        txtOrigin = new JTextField();
        txtDestination = new JTextField();
        txtDeparture = new JTextField("2025-01-01T09:00");
        txtArrival = new JTextField("2025-01-01T11:00");
        txtPrice = new JTextField("199.99");

        form.add(new JLabel("Airplane:"));
        form.add(airplaneCombo);
        form.add(new JLabel("Origin:"));
        form.add(txtOrigin);
        form.add(new JLabel("Destination:"));
        form.add(txtDestination);
        form.add(new JLabel("Departure (yyyy-MM-ddTHH:mm):"));
        form.add(txtDeparture);
        form.add(new JLabel("Arrival (yyyy-MM-ddTHH:mm):"));
        form.add(txtArrival);
        form.add(new JLabel("Price:"));
        form.add(txtPrice);

        JButton btnCreate = new JButton("Create Flight");
        btnCreate.addActionListener(e -> createFlight());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnCreate);

        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadAirplanes() {
        try {
            List<Airplane> planes = airplaneDAO.findAll();
            for (Airplane a : planes) airplaneCombo.addItem(a);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load airplanes:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createFlight() {
        try {
            Airplane plane = (Airplane) airplaneCombo.getSelectedItem();
            if (plane == null) {
                JOptionPane.showMessageDialog(this, "No airplane selected!");
                return;
            }

            Flight f = new Flight();
            f.setAirplaneId(plane.getId());
            f.setOrigin(txtOrigin.getText().trim());
            f.setDestination(txtDestination.getText().trim());
            f.setDepartureTime(LocalDateTime.parse(txtDeparture.getText().trim()));
            f.setArrivalTime(LocalDateTime.parse(txtArrival.getText().trim()));
            f.setPrice(Double.parseDouble(txtPrice.getText().trim()));
            f.setSeatsAvailable(0); // FlightController will set real count

            flightController.createFlight(adminUser, f);

            JOptionPane.showMessageDialog(this,
                    "Flight created successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException | DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid input:\n" + ex.getMessage(),
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

