package flightapp.presentation;

import flightapp.AppContext;
import flightapp.business.controllers.BookingController;
import flightapp.business.controllers.FlightController;
import flightapp.business.domain.Customer;
import flightapp.business.domain.Flight;
import flightapp.business.domain.Reservation;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class FlightSearchDialog extends JDialog {

    private final FlightController flightController = new FlightController();
    private final BookingController bookingController = new BookingController();

    private final DefaultListModel<Flight> flightListModel = new DefaultListModel<>();

    public FlightSearchDialog(JFrame parent) {
        super(parent, "Search Flights", true);

        JTextField originField = new JTextField(8);
        JTextField destField   = new JTextField(8);
        JTextField dateField   = new JTextField(10); // yyyy-MM-dd

        JButton searchButton   = new JButton("Search");
        JButton bookButton     = new JButton("Book Selected");

        JList<Flight> flightList = new JList<>(flightListModel);
        flightList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        searchButton.addActionListener(e -> {
            try {
                String origin = originField.getText().trim();
                String dest   = destField.getText().trim();
                LocalDate date = LocalDate.parse(dateField.getText().trim());

                List<Flight> flights = flightController.searchFlights(origin, dest, date);
                flightListModel.clear();
                for (Flight f : flights) {
                    flightListModel.addElement(f);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
            }
        });

        bookButton.addActionListener(e -> {
            Flight selected = flightList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a flight.");
                return;
            }
            if (!AppContext.isLoggedIn()) {
                JOptionPane.showMessageDialog(this, "Please login first.");
                return;
            }

            Customer c = AppContext.getCurrentCustomer();
            Reservation r = bookingController.bookFlight(c, selected);
            JOptionPane.showMessageDialog(this,
                    "Booking confirmed. Reservation #" + r.getId());
        });

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Origin:"));
        inputPanel.add(originField);
        inputPanel.add(new JLabel("Destination:"));
        inputPanel.add(destField);
        inputPanel.add(new JLabel("Date (yyyy-MM-dd):"));
        inputPanel.add(dateField);
        inputPanel.add(searchButton);
        inputPanel.add(bookButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(inputPanel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(flightList), BorderLayout.CENTER);

        setSize(800, 400);
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}
