package flightapp.presentation;

import flightapp.AppContext;
import flightapp.business.controllers.FlightController;
import flightapp.business.domain.Flight;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminFlightManagementDialog extends JDialog {

    private final FlightController flightController = new FlightController();
    private final DefaultListModel<Flight> flightListModel = new DefaultListModel<>();
    private final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AdminFlightManagementDialog(JFrame parent) {
        super(parent, "Admin - Flight Management", true);

        if (!AppContext.isAdmin()) {
            JOptionPane.showMessageDialog(parent, "Admin access only.");
            dispose();
            return;
        }

        JList<Flight> flightList = new JList<>(flightListModel);
        flightList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTextField originField = new JTextField(6);
        JTextField destField   = new JTextField(6);
        JTextField depField    = new JTextField(16); // yyyy-MM-dd HH:mm
        JTextField arrField    = new JTextField(16);
        JTextField airlineField= new JTextField(8);
        JTextField priceField  = new JTextField(6);
        JTextField seatsField  = new JTextField(4);

        JButton addButton    = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton reloadButton = new JButton("Reload");

        addButton.addActionListener(e -> {
            try {
                Flight f = new Flight();
                f.setOrigin(originField.getText().trim());
                f.setDestination(destField.getText().trim());
                f.setDepartureTime(LocalDateTime.parse(depField.getText().trim(), dtFormatter));
                f.setArrivalTime(LocalDateTime.parse(arrField.getText().trim(), dtFormatter));
                f.setAirline(airlineField.getText().trim());
                f.setPrice(Double.parseDouble(priceField.getText().trim()));
                f.setSeatsAvailable(Integer.parseInt(seatsField.getText().trim()));

                flightController.addFlight(f);
                loadFlights();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
            }
        });

        updateButton.addActionListener(e -> {
            Flight selected = flightList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Select a flight to update.");
                return;
            }
            try {
                selected.setOrigin(originField.getText().trim());
                selected.setDestination(destField.getText().trim());
                selected.setDepartureTime(LocalDateTime.parse(depField.getText().trim(), dtFormatter));
                selected.setArrivalTime(LocalDateTime.parse(arrField.getText().trim(), dtFormatter));
                selected.setAirline(airlineField.getText().trim());
                selected.setPrice(Double.parseDouble(priceField.getText().trim()));
                selected.setSeatsAvailable(Integer.parseInt(seatsField.getText().trim()));

                flightController.updateFlight(selected);
                loadFlights();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
            }
        });

        deleteButton.addActionListener(e -> {
            Flight selected = flightList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Select a flight to delete.");
                return;
            }
            flightController.deleteFlight(selected.getId());
            loadFlights();
        });

        reloadButton.addActionListener(e -> loadFlights());

        flightList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Flight f = flightList.getSelectedValue();
                if (f != null) {
                    originField.setText(f.getOrigin());
                    destField.setText(f.getDestination());
                    depField.setText(f.getDepartureTime().format(dtFormatter));
                    arrField.setText(f.getArrivalTime().format(dtFormatter));
                    airlineField.setText(f.getAirline());
                    priceField.setText(Double.toString(f.getPrice()));
                    seatsField.setText(Integer.toString(f.getSeatsAvailable()));
                }
            }
        });

        JPanel form = new JPanel(new GridLayout(4, 4, 5, 5));
        form.add(new JLabel("Origin:"));
        form.add(originField);
        form.add(new JLabel("Destination:"));
        form.add(destField);

        form.add(new JLabel("Departure (yyyy-MM-dd HH:mm):"));
        form.add(depField);
        form.add(new JLabel("Arrival (yyyy-MM-dd HH:mm):"));
        form.add(arrField);

        form.add(new JLabel("Airline:"));
        form.add(airlineField);
        form.add(new JLabel("Price:"));
        form.add(priceField);

        form.add(new JLabel("Seats:"));
        form.add(seatsField);
        form.add(addButton);
        form.add(updateButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteButton);
        buttonPanel.add(reloadButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(form, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(flightList), BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        setSize(900, 500);
        setLocationRelativeTo(parent);

        loadFlights();
        setVisible(true);
    }

    private void loadFlights() {
        flightListModel.clear();
        List<Flight> flights = flightController.getAllFlights();
        for (Flight f : flights) {
            flightListModel.addElement(f);
        }
    }
}
